package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import android.support.annotation.NonNull;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class EnsureTextureCacheUseCase {

    private static final Log LOG = LogFactory.getLog(EnsureTextureCacheUseCase.class);

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    UserTextureFileMetadataRepository userTextureFileMetadataRepository;

    @Inject
    public EnsureTextureCacheUseCase() {
    }

    public Single<File> execute(String id, OnProgressListener onProgressListener) {
        if (id == null) throw new ArgumentNullException("id");

        // Find the texture reference.
        return userTextureRepository.find(id)
                                    // Ensure that the cache is up to date.
                                    .flatMap(userTexture -> ensureCacheUpToDate(userTexture, onProgressListener))
                                    .toSingle()
                                    .subscribeOn(Schedulers.io());
    }

    private Observable<File> ensureCacheUpToDate(UserTexture userTexture, OnProgressListener onProgressListener) {
        // Find the cache file.
        return textureCacheRepository.find(userTexture.id)
                                     .map(file -> new TimestampModel(userTexture, file))
                                     // Find the timestamp of the file in Firebase Storage.
                                     .flatMap(this::findRemoteTimestamp)
                                     // Download the file from Firebase Storage if its cache is outdated;
                                     // otherwise return the cache.
                                     .flatMap(model -> downloadIfCacheOutdated(model, onProgressListener))
                                     // Download the file from Firebase Storage if no cache exists.
                                     .switchIfEmpty(download(userTexture, onProgressListener));
    }

    private Observable<TimestampModel> findRemoteTimestamp(TimestampModel model) {
        return userTextureFileMetadataRepository.find(model.userTexture.id)
                                                .map(metadata -> {
                                                    model.remoteUpdateTimeMillis = metadata.updateTimeMillis;
                                                    return model;
                                                });
    }

    private Observable<File> downloadIfCacheOutdated(TimestampModel model, OnProgressListener onProgressListener) {
        if (model.isCacheOutdated()) {
            LOG.d("The cache is outdated.");
            return download(model.userTexture, onProgressListener);
        } else {
            LOG.d("The cache is up to date.");
            return Observable.just(model.cacheFile);
        }
    }

    private Observable<File> download(UserTexture userTexture, OnProgressListener onProgressListener) {
        return textureCacheRepository.create(userTexture.id)
                                     .toObservable()
                                     .map(file -> new DownloadModel(userTexture.id, file))
                                     .flatMap(downloadInfo -> download(downloadInfo, onProgressListener));
    }

    private Observable<File> download(DownloadModel downloadModel, OnProgressListener onProgressListener) {
        return userTextureFileRepository.download(downloadModel.id, downloadModel.file, onProgressListener::onProgress)
                                        .toObservable()
                                        .map(fileId -> downloadModel.file);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private final class TimestampModel {

        final UserTexture userTexture;

        final File cacheFile;

        long remoteUpdateTimeMillis;

        TimestampModel(@NonNull UserTexture userTexture, @NonNull File cacheFile) {
            this.userTexture = userTexture;
            this.cacheFile = cacheFile;
        }

        boolean isCacheOutdated() {
            return cacheFile.lastModified() < remoteUpdateTimeMillis;
        }
    }

    private final class DownloadModel {

        final String id;

        final File file;

        DownloadModel(String id, File file) {
            this.id = id;
            this.file = file;
        }
    }
}
