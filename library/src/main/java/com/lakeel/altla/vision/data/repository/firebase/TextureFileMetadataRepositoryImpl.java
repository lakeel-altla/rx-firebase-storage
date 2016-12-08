package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.TextureFileMetadata;
import com.lakeel.altla.vision.domain.repository.TextureFileMetadataRepository;

import rx.Observable;

public final class TextureFileMetadataRepositoryImpl implements TextureFileMetadataRepository {

    private static final String PATH_TEXTURES = "textures";

    private final StorageReference baseDirectory;

    private final FirebaseAuth auth;

    public TextureFileMetadataRepositoryImpl(StorageReference baseDirectory, FirebaseAuth auth) {
        if (baseDirectory == null) throw new ArgumentNullException("baseDirectory");
        if (auth == null) throw new ArgumentNullException("auth");

        this.baseDirectory = baseDirectory;
        this.auth = auth;
    }

    @Override
    public Observable<TextureFileMetadata> find(String fileId) {
        if (fileId == null) throw new ArgumentNullException("fileId");

        Task<StorageMetadata> task = baseDirectory.child(resolveUserId())
                                                  .child(PATH_TEXTURES)
                                                  .child(fileId)
                                                  .getMetadata();

        return RxGmsTask.asObservable(task)
                        .map(storageMetadata -> {
                            TextureFileMetadata fileMetadata = new TextureFileMetadata();
                            fileMetadata.createTimeMillis = storageMetadata.getCreationTimeMillis();
                            fileMetadata.updateTimeMillis = storageMetadata.getUpdatedTimeMillis();
                            return fileMetadata;
                        });
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }
}
