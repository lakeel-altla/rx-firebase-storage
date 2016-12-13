package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.DocumentRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class SaveUserTextureUseCase {

    @Inject
    DocumentRepository documentRepository;

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    public SaveUserTextureUseCase() {
    }

    public Single<UserTexture> execute(UserTexture userTexture, String localUri,
                                       OnProgressListener onProgressListener) {
        if (userTexture == null) throw new ArgumentNullException("userTexture");
        if (localUri == null) throw new ArgumentNullException("localUri");

        return Single.just(new Model(userTexture, localUri, onProgressListener))
                     // Open the stream to the android local file.
                     .flatMap(this::openStream)
                     // Get total bytes of the stream.
                     .flatMap(this::getTotalBytes)
                     // Upload its file to Firebase Storage.
                     .flatMap(this::uploadUserTextureFile)
                     // Save the user texture to Firebase Database.
                     .flatMap(this::saveUserTexture)
                     // Return the id.
                     .map(model -> model.userTexture)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> openStream(Model model) {
        return documentRepository.openStream(model.localUri)
                                 .map(stream -> {
                                     model.stream = stream;
                                     return model;
                                 });
    }

    private Single<Model> getTotalBytes(Model model) {
        return Single.<Long>create(subscriber -> {
            try {
                long totalBytes = model.stream.available();
                subscriber.onSuccess(totalBytes);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).map(totalBytes -> {
            model.totalBytes = totalBytes;
            return model;
        });
    }

    private Single<Model> uploadUserTextureFile(Model model) {
        // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
        return userTextureFileRepository
                .save(model.userTexture.id,
                      model.stream,
                      (totalBytes, bytesTransferred) ->
                              model.onProgressListener.onProgress(model.totalBytes, bytesTransferred)
                )
                .map(fileId -> model);
    }

    private Single<Model> saveUserTexture(Model model) {
        return userTextureRepository.save(model.userTexture)
                                    .map(id -> model);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private final class Model {

        final UserTexture userTexture;

        final String localUri;

        final OnProgressListener onProgressListener;

        InputStream stream;

        long totalBytes;

        Model(UserTexture userTexture, String localUri, OnProgressListener onProgressListener) {
            this.userTexture = userTexture;
            this.localUri = localUri;
            this.onProgressListener = onProgressListener;
        }
    }
}
