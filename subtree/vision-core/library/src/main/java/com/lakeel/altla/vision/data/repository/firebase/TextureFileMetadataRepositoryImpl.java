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

    private static final String PATH_USER_TEXTURES = "userTextures";

    private final StorageReference rootReference;

    public TextureFileMetadataRepositoryImpl(StorageReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Observable<TextureFileMetadata> find(String fileId) {
        if (fileId == null) throw new ArgumentNullException("fileId");

        Task<StorageMetadata> task = rootReference.child(PATH_USER_TEXTURES)
                                                  .child(resolveCurrentUserId())
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

    private String resolveCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }
}
