package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.domain.repository.FirebaseFileRepository;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class UploadFileUseCase {

    @Inject
    LocalDocumentRepository localDocumentRepository;

    @Inject
    FirebaseFileRepository firebaseFileRepository;

    @Inject
    public UploadFileUseCase() {
    }

    public Single<String> execute(String uri, OnProgressListener onProgressListener) {
        String uuid = UUID.randomUUID().toString();
        return localDocumentRepository.openStream(uri)
                                      .flatMap(stream -> upload(uuid, stream, onProgressListener))
                                      .subscribeOn(Schedulers.io());
    }

    private Single<String> upload(String uuid, InputStream stream, OnProgressListener onProgressListener) {
        // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
        try {
            long available = stream.available();
            return firebaseFileRepository.save(
                    uuid, stream,
                    (totalBytes, bytesTransferred) -> onProgressListener.onProgress(available, bytesTransferred));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
