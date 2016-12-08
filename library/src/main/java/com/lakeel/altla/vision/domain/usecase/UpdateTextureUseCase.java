package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.TextureMetadata;
import com.lakeel.altla.vision.domain.repository.DocumentRepository;
import com.lakeel.altla.vision.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.domain.repository.TextureFileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class UpdateTextureUseCase {

    @Inject
    DocumentRepository documentRepository;

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    TextureFileRepository textureFileRepository;

    @Inject
    public UpdateTextureUseCase() {
    }

    public Single<String> execute(String id, String name, String localUri, TextureMetadata metadata,
                                  OnProgressListener onProgressListener) {
        if (id == null) throw new ArgumentNullException("id");
        if (name == null) throw new ArgumentNullException("name");
        if (localUri == null) throw new ArgumentNullException("localUri");
        if (metadata == null) throw new ArgumentNullException("metadata");

        return Single.just(new Model(id, name, localUri, metadata, onProgressListener))
                     // Find or create the file id.
                     .flatMap(this::resolveFileId)
                     // Open the stream to the android local file.
                     .flatMap(this::openStream)
                     // Get total bytes of the stream.
                     .flatMap(this::getTotalBytes)
                     // Upload its file to Firebase Storage.
                     .flatMap(this::uploadTexture)
                     // Save the entry to Firebase Database.
                     .flatMap(this::saveTextureEntry)
                     // Return the id.
                     .map(model -> model.id)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> resolveFileId(Model model) {
        // Find the existing entry.
        return textureEntryRepository.findReference(model.id)
                                     // Get its file id.
                                     .map(reference -> reference.fileId)
                                     // Create a new file id if no previous file exists.
                                     .defaultIfEmpty(UUID.randomUUID().toString())
                                     .map(fileId -> {
                                         model.fileId = fileId;
                                         return model;
                                     })
                                     .toSingle();
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

    private Single<Model> uploadTexture(Model model) {
        // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
        return textureFileRepository.save(model.fileId, model.stream,
                                          (totalBytes, bytesTransferred) ->
                                                  model.onProgressListener
                                                          .onProgress(model.totalBytes, bytesTransferred)
        )
                                    .map(fileId -> model);
    }

    private Single<Model> saveTextureEntry(Model model) {
        return textureEntryRepository.save(model.id, model.name, model.fileId, model.metadata)
                                     .map(id -> model);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private final class Model {

        final String id;

        final String name;

        final String localUri;

        final TextureMetadata metadata;

        final OnProgressListener onProgressListener;

        String fileId;

        InputStream stream;

        long totalBytes;

        Model(String id, String name, String localUri, TextureMetadata metadata,
              OnProgressListener onProgressListener) {
            this.id = id;
            this.name = name;
            this.localUri = localUri;
            this.metadata = metadata;
            this.onProgressListener = onProgressListener;
        }
    }
}
