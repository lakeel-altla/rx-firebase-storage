package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.UploadTaskSingle;
import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.model.TextureStorageEntry;
import com.lakeel.altla.vision.builder.domain.repository.TextureStorageEntryRepository;

import rx.Single;

public final class TextureStorageEntryRepositoryImpl implements TextureStorageEntryRepository {

    private final StorageReference baseDirectory;

    public TextureStorageEntryRepositoryImpl(StorageReference baseDirectory) {
        if (baseDirectory == null) throw new ArgumentNullException("baseDirectory");

        this.baseDirectory = baseDirectory;
    }

    @Override
    public Single<TextureStorageEntry> save(TextureStorageEntry entry, OnProgressListener onProgressListener) {
        // NOTE:
        //
        // Callbacks of the task for Firebase Storage are called by the thread for Firebase Storage.
        // Calling RxJava methods from them will be also called by its thread.
        // Note that a subsequent stream processing is also handled by its thread.

        StorageReference file = baseDirectory.child(entry.uuid);
        UploadTask task = file.putStream(entry.stream);

        return UploadTaskSingle.create(task, onProgressListener::onProgress)
                               .map(taskSnapshot -> entry);
    }
}
