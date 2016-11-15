package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.UploadTaskSingle;
import com.lakeel.altla.vision.builder.domain.repository.FirebaseFileRepository;

import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseFileRepositoryImpl implements FirebaseFileRepository {

    private final StorageReference directory;

    @Inject
    public FirebaseFileRepositoryImpl(String uri, String path) {
        StorageReference root = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        directory = root.child(path);
    }

    @Override
    public Single<String> save(String uuid, InputStream stream, OnProgressListener onProgressListener) {
        // NOTE:
        //
        // Callbacks of the task for Firebase Storage are called by the thread for Firebase Storage.
        // Calling RxJava methods from them will be also called by its thread.
        // Note that a subsequent stream processing is also handled by its thread.

        StorageReference file = directory.child(uuid);
        UploadTask task = file.putStream(stream);

        return UploadTaskSingle.create(task, onProgressListener::onProgress)
                               .map(taskSnapshot -> uuid);
    }
}
