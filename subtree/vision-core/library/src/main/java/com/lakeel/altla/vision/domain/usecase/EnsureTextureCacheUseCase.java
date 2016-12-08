package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.model.TextureReference;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.domain.repository.TextureFileMetadataRepository;
import com.lakeel.altla.vision.domain.repository.TextureFileRepository;

import android.support.annotation.NonNull;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class EnsureTextureCacheUseCase {

    private static final Log LOG = LogFactory.getLog(EnsureTextureCacheUseCase.class);

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    TextureFileRepository textureFileRepository;

    @Inject
    TextureFileMetadataRepository textureFileMetadataRepository;

    @Inject
    public EnsureTextureCacheUseCase() {
    }

    public Single<File> execute(String id, OnProgressListener onProgressListener) {
        // Find the texture reference.
        return textureEntryRepository.findReference(id)
                                     // Ensure that the cache is up to date.
                                     .flatMap(reference -> ensureCacheUpToDate(reference, onProgressListener))
                                     .toSingle()
                                     .subscribeOn(Schedulers.io());
    }

    private Observable<File> ensureCacheUpToDate(TextureReference reference, OnProgressListener onProgressListener) {
        // Find the cache file.
        return textureCacheRepository.find(reference.fileId)
                                     .map(file -> new TimestampModel(reference, file))
                                     // Find the timestamp of the file in Firebase Storage.
                                     .flatMap(this::findRemoteTimestamp)
                                     // Download the file from Firebase Storage if its cache is outdated;
                                     // otherwise return the cache.
                                     .flatMap(model -> downloadIfCacheOutdated(model, onProgressListener))
                                     // Download the file from Firebase Storage if no cache exists.
                                     .switchIfEmpty(download(reference, onProgressListener));
    }

    private Observable<TimestampModel> findRemoteTimestamp(TimestampModel model) {
        return textureFileMetadataRepository.find(model.reference.fileId)
                                            .map(metadata -> {
                                                model.remoteUpdateTimeMillis = metadata.updateTimeMillis;
                                                return model;
                                            });
    }

    private Observable<File> downloadIfCacheOutdated(TimestampModel model, OnProgressListener onProgressListener) {
        if (model.isCacheOutdated()) {
            LOG.d("The cache is outdated.");
            return download(model.reference, onProgressListener);
        } else {
            LOG.d("The cache is up to date.");
            return Observable.just(model.cacheFile);
        }
    }

    private Observable<File> download(TextureReference reference, OnProgressListener onProgressListener) {
        return textureCacheRepository.create(reference.fileId)
                                     .toObservable()
                                     .map(file -> new DownloadModel(reference.fileId, file))
                                     .flatMap(downloadInfo -> download(downloadInfo, onProgressListener));
    }

    private Observable<File> download(DownloadModel downloadModel, OnProgressListener onProgressListener) {
        return textureFileRepository.download(downloadModel.fileId, downloadModel.file, onProgressListener::onProgress)
                                    .toObservable()
                                    .map(fileId -> downloadModel.file);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private final class TimestampModel {

        final TextureReference reference;

        final File cacheFile;

        long remoteUpdateTimeMillis;

        TimestampModel(@NonNull TextureReference reference, @NonNull File cacheFile) {
            this.reference = reference;
            this.cacheFile = cacheFile;
        }

        boolean isCacheOutdated() {
            return cacheFile.lastModified() < remoteUpdateTimeMillis;
        }
    }

    private final class DownloadModel {

        final String fileId;

        final File file;

        DownloadModel(String fileId, File file) {
            this.fileId = fileId;
            this.file = file;
        }
    }
}
