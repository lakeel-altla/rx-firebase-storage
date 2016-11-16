package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureRepository;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class RegisterTextureUseCase {

    @Inject
    LocalDocumentRepository localDocumentRepository;

    @Inject
    TextureRepository textureRepository;

    @Inject
    public RegisterTextureUseCase() {
    }

    public Single<String> execute(String localUri, String directoryPath, String filename,
                                  OnProgressListener onProgressListener) {
        return localDocumentRepository.openStream(localUri)
                                      .flatMap(stream -> upload(directoryPath, filename, stream, onProgressListener))
                                      .subscribeOn(Schedulers.io());
    }

    /**
     * Uploads an image to Firebase Storage.
     *
     * @param directoryPath      TODO.
     * @param filename           TODO.
     * @param stream             The stream to an uploading image.
     * @param onProgressListener The callback to know the progress status of an uploading image.
     * @return The Single instance that emits the UUID of the uploaded image.
     */
    private Single<String> upload(String directoryPath, String filename, InputStream stream,
                                  OnProgressListener onProgressListener) {
        try {
            // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
            long available = stream.available();
            return textureRepository.save(
                    directoryPath, filename, stream,
                    (totalBytes, bytesTransferred) -> onProgressListener.onProgress(available, bytesTransferred));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
