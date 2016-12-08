package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TextureFileRepository;

import java.io.File;
import java.io.InputStream;

import rx.Single;

public final class TextureFileRepositoryImpl implements TextureFileRepository {

    private static final String PATH_TEXTURES = "textures";

    private final StorageReference baseDirectory;

    private final FirebaseAuth auth;

    public TextureFileRepositoryImpl(StorageReference baseDirectory, FirebaseAuth auth) {
        if (baseDirectory == null) throw new ArgumentNullException("baseDirectory");
        if (auth == null) throw new ArgumentNullException("auth");

        this.baseDirectory = baseDirectory;
        this.auth = auth;
    }

    @Override
    public Single<String> save(String fileId, InputStream stream, OnProgressListener onProgressListener) {
        // NOTE:
        //
        // Callbacks of the task for Firebase Storage are called by the thread for Firebase Storage.
        // Calling RxJava methods from them will be also called by its thread.
        // Note that a subsequent stream processing is also handled by its thread.

        StorageReference reference = baseDirectory.child(resolveUserId())
                                                  .child(PATH_TEXTURES)
                                                  .child(fileId);
        UploadTask task = reference.putStream(stream);

        return RxFirebaseStorageTask.asSingle(task, onProgressListener::onProgress)
                                    .map(snapshot -> fileId);
    }

    @Override
    public Single<String> delete(String fileId) {

        StorageReference reference = baseDirectory.child(resolveUserId())
                                                  .child(PATH_TEXTURES)
                                                  .child(fileId);
        Task<Void> task = reference.delete();

        return RxGmsTask.asObservable(task).map(aVoid -> fileId).toSingle();
    }

    @Override
    public Single<String> download(String fileId, File destination, OnProgressListener onProgressListener) {

        StorageReference reference = baseDirectory.child(resolveUserId())
                                                  .child(PATH_TEXTURES)
                                                  .child(fileId);
        FileDownloadTask task = reference.getFile(destination);

        return RxFirebaseStorageTask.asSingle(task, onProgressListener::onProgress)
                                    .map(snapshot -> fileId);
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }
}
