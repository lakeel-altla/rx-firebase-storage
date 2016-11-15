package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.domain.repository.FirebaseFileRepository;

import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseFileRepositoryImpl implements FirebaseFileRepository {

    private static final Log LOG = LogFactory.getLog(FirebaseFileRepositoryImpl.class);

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

        return Single.create(subscriber -> {
            LOG.d("Saving file to Firebase Storage...");

            StorageReference file = directory.child(uuid);
            UploadTask task = file.putStream(stream);
            task.addOnSuccessListener(taskSnapshot -> subscriber.onSuccess(uuid))
                .addOnFailureListener(subscriber::onError)
                .addOnProgressListener(snapshot -> onProgressListener.onProgress(
                        snapshot.getTotalByteCount(), snapshot.getBytesTransferred()));
        });
    }
}
