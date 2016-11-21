package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class RegisterTextureUseCase {

    @Inject
    LocalDocumentRepository localDocumentRepository;

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    TextureFileRepository textureFileRepository;

    @Inject
    public RegisterTextureUseCase() {
    }

    public Single<String> execute(String id, String name, String localUri, TextureMetadata metadata,
                                  OnProgressListener onProgressListener) {
        if (id == null) throw new ArgumentNullException("id");
        if (name == null) throw new ArgumentNullException("name");
        if (localUri == null) throw new ArgumentNullException("localUri");
        if (metadata == null) throw new ArgumentNullException("metadata");

        // Find the existing entry.
        return findFileId(id)
                // Delete a previous file if it exists.
                .flatMap(fileId -> deleteFile(fileId))
                // Create a new file id if no previous file exists.
                .defaultIfEmpty(UUID.randomUUID().toString())
                .toSingle()
                // Open the stream to the android local file.
                .flatMap(fileId -> openStream(localUri, fileId))
                // Upload its file to Fierbase Storage.
                .flatMap(file -> uploadTexture(file, onProgressListener))
                // Save the entry to Firebase Database.
                .flatMap(fileId -> saveTextureEntry(id, name, fileId, metadata))
                .subscribeOn(Schedulers.io());
    }

    private Observable<String> findFileId(String entryId) {
        return textureEntryRepository.findReference(entryId)
                                     .map(reference -> reference.fileId);
    }

    private Observable<String> deleteFile(String fileId) {
        return textureFileRepository.delete(fileId)
                                    .toObservable();
    }

    private Single<TextureFile> openStream(String localUri, String fileId) {
        return localDocumentRepository
                .openStream(localUri)
                .map(stream -> new TextureFile(fileId, stream));
    }

    private Single<String> uploadTexture(TextureFile file, OnProgressListener onProgressListener) {
        try {
            // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
            long available = file.stream.available();
            return textureFileRepository.save(
                    file.fileId, file.stream,
                    (totalBytes, bytesTransferred) -> onProgressListener.onProgress(available, bytesTransferred));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Single<String> saveTextureEntry(String id, String name, String fileId, TextureMetadata metadata) {
        return textureEntryRepository.save(id, name, fileId, metadata);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private final class TextureFile {

        String fileId;

        InputStream stream;

        TextureFile(String fileId, InputStream stream) {
            this.fileId = fileId;
            this.stream = stream;
        }
    }
}
