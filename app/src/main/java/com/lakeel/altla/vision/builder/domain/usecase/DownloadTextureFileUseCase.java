package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.domain.model.TextureReference;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class DownloadTextureFileUseCase {

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    TextureFileRepository textureFileRepository;

    @Inject
    public DownloadTextureFileUseCase() {
    }

    public Single<File> execute(String id, OnProgressListener onProgressListener) {
        return findReference(id)
                .toSingle()
                .flatMap(reference -> downloadTexture(reference.fileId, onProgressListener))
                .subscribeOn(Schedulers.io());
    }

    private Observable<TextureReference> findReference(String id) {
        return textureEntryRepository.findReference(id);
    }

    private Single<File> downloadTexture(String fileId, OnProgressListener onProgressListener) {
        return textureFileRepository.download(fileId, onProgressListener::onProgress);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
