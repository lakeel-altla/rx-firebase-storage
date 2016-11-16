package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.UploadTaskSingle;
import com.lakeel.altla.vision.builder.domain.repository.TextureRepository;

import java.io.InputStream;

import rx.Single;

public final class TextureRepositoryImpl implements TextureRepository {

    private final StorageReference baseDirectory;

    public TextureRepositoryImpl(StorageReference baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public Single<String> save(String directoryPath, String filename, InputStream stream,
                               OnProgressListener onProgressListener) {
        // NOTE:
        //
        // Callbacks of the task for Firebase Storage are called by the thread for Firebase Storage.
        // Calling RxJava methods from them will be also called by its thread.
        // Note that a subsequent stream processing is also handled by its thread.

        StorageReference directory;
        if (directoryPath == null) {
            directory = baseDirectory;
        } else {
            directory = baseDirectory.child(directoryPath);
        }

        StorageReference file = directory.child(filename);
        UploadTask task = file.putStream(stream);

        return UploadTaskSingle.create(task, onProgressListener::onProgress)
                               .map(taskSnapshot -> file.getPath());
    }
}
