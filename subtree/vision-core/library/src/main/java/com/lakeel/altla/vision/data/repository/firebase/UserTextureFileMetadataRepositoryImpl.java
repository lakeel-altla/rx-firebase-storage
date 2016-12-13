package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.TextureFileMetadata;
import com.lakeel.altla.vision.domain.repository.UserTextureFileMetadataRepository;

import rx.Observable;

public final class UserTextureFileMetadataRepositoryImpl implements UserTextureFileMetadataRepository {

    private static final String PATH_USER_TEXTURES = "userTextures";

    private final StorageReference rootReference;

    public UserTextureFileMetadataRepositoryImpl(StorageReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Observable<TextureFileMetadata> find(String id) {
        if (id == null) throw new ArgumentNullException("id");

        Task<StorageMetadata> task = rootReference.child(PATH_USER_TEXTURES)
                                                  .child(resolveCurrentUserId())
                                                  .child(id)
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
