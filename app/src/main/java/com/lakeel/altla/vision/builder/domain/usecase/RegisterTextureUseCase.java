package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.model.TextureDatabaseEntry;
import com.lakeel.altla.vision.builder.domain.model.TextureStorageEntry;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureDatabaseEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureStorageEntryRepository;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class RegisterTextureUseCase {

    @Inject
    LocalDocumentRepository localDocumentRepository;

    @Inject
    TextureDatabaseEntryRepository textureDatabaseEntryRepository;

    @Inject
    TextureStorageEntryRepository textureStorageEntryRepository;

    @Inject
    public RegisterTextureUseCase() {
    }

    public Single<TextureDatabaseEntry> execute(String localUri, TextureDatabaseEntry.Metadata metadata,
                                                OnProgressListener onProgressListener) {
        if (localUri == null) throw new ArgumentNullException("localUri");
        if (metadata == null) throw new ArgumentNullException("metadata");

        return findTextureEntryByFilename(metadata.filename)
                .defaultIfEmpty(createTextureEntry(metadata))
                .toSingle()
                .flatMap(databaseEntry -> openStream(localUri, databaseEntry))
                .flatMap(margedEntry -> uploadTexture(margedEntry, onProgressListener))
                .flatMap(margedEntry -> addTextureToList(margedEntry))
                .subscribeOn(Schedulers.io());
    }

    private Observable<TextureDatabaseEntry> findTextureEntryByFilename(String filename) {
        return textureDatabaseEntryRepository.findByFilename(filename);
    }

    private TextureDatabaseEntry createTextureEntry(TextureDatabaseEntry.Metadata metadata) {
        String uuid = UUID.randomUUID().toString();
        return new TextureDatabaseEntry(uuid, metadata);
    }

    private Single<MargedEntry> openStream(String localUri, TextureDatabaseEntry databaseEntry) {
        return localDocumentRepository
                .openStream(localUri)
                .map(stream -> new MargedEntry(databaseEntry, new TextureStorageEntry(databaseEntry.uuid, stream)));
    }

    private Single<MargedEntry> uploadTexture(MargedEntry margedEntry, OnProgressListener onProgressListener) {
        TextureStorageEntry storageEntry = margedEntry.storageEntry;

        try {
            // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
            long available = storageEntry.stream.available();
            return textureStorageEntryRepository
                    .save(storageEntry,
                          (totalBytes, bytesTransferred) -> onProgressListener.onProgress(available, bytesTransferred))
                    .map(s -> margedEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Single<TextureDatabaseEntry> addTextureToList(MargedEntry entry) {
        return textureDatabaseEntryRepository.save(entry.databaseEntry);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private class MargedEntry {

        final TextureDatabaseEntry databaseEntry;

        final TextureStorageEntry storageEntry;

        MargedEntry(TextureDatabaseEntry databaseEntry, TextureStorageEntry storageEntry) {
            this.databaseEntry = databaseEntry;
            this.storageEntry = storageEntry;
        }
    }
}
