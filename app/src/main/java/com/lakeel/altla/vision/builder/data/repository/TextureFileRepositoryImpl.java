package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.storage.UploadTaskSingle;
import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;

import java.io.InputStream;

import rx.Single;

public final class TextureFileRepositoryImpl implements TextureFileRepository {

    private static final Log LOG = LogFactory.getLog(TextureFileRepositoryImpl.class);

    private final StorageReference baseDirectory;

    public TextureFileRepositoryImpl(StorageReference baseDirectory) {
        if (baseDirectory == null) throw new ArgumentNullException("baseDirectory");

        this.baseDirectory = baseDirectory;
    }

    @Override
    public Single<String> save(String fileId, InputStream stream, OnProgressListener onProgressListener) {
        // NOTE:
        //
        // Callbacks of the task for Firebase Storage are called by the thread for Firebase Storage.
        // Calling RxJava methods from them will be also called by its thread.
        // Note that a subsequent stream processing is also handled by its thread.

        StorageReference reference = baseDirectory.child(fileId);
        UploadTask task = reference.putStream(stream);

        return UploadTaskSingle.create(task, onProgressListener::onProgress)
                               .map(snapshot -> fileId);
    }

    @Override
    public Single<String> delete(String fileId) {
        LOG.d("Deleting the file: fileId = %s", fileId);

        return Single.create(subscriber -> {
            StorageReference reference = baseDirectory.child(fileId);
            reference.delete()
                     .addOnSuccessListener(aVoid -> {
                         LOG.d("Deleted the file.");
                         subscriber.onSuccess(fileId);
                     })
                     .addOnFailureListener(e -> {
                         LOG.e("Failed to delete the file: fileId = %s", fileId);
                         subscriber.onError(e);
                     });
        });
    }
}
