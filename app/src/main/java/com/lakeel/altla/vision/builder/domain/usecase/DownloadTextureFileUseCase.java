package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.domain.model.TextureReference;
import com.lakeel.altla.vision.builder.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class DownloadTextureFileUseCase {

    private static final Log LOG = LogFactory.getLog(DownloadTextureFileUseCase.class);

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    TextureFileRepository textureFileRepository;

    @Inject
    public DownloadTextureFileUseCase() {
    }

    public Single<File> execute(String id, OnProgressListener onProgressListener) {
        return textureEntryRepository.findReference(id)
                                     .flatMap(reference -> ensureCache(reference, onProgressListener))
                                     .toSingle()
                                     .subscribeOn(Schedulers.io());
    }

    private Observable<File> ensureCache(TextureReference reference, OnProgressListener onProgressListener) {
        return textureCacheRepository.find(reference.fileId)
                                     .switchIfEmpty(download(reference, onProgressListener));
    }

    private Observable<File> download(TextureReference reference, OnProgressListener onProgressListener) {
        return textureCacheRepository.create(reference.fileId)
                                     .toObservable()
                                     .map(file -> new DownloadInfo(reference.fileId, file))
                                     .flatMap(downloadInfo -> download(downloadInfo, onProgressListener));
    }

    private Observable<File> download(DownloadInfo downloadInfo, OnProgressListener onProgressListener) {
        return textureFileRepository.download(downloadInfo.fileId, downloadInfo.file, onProgressListener::onProgress)
                                    .toObservable()
                                    .map(fileId -> downloadInfo.file);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private class DownloadInfo {

        final String fileId;

        final File file;

        DownloadInfo(String fileId, File file) {
            this.fileId = fileId;
            this.file = file;
        }
    }
}
