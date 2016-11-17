package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Single;

public final class TextureEntryRepositoryImpl implements TextureEntryRepository {

    private static final Log LOG = LogFactory.getLog(TextureEntryRepositoryImpl.class);

    private static final String PATH_TEXTURE_FILES = "textureFiles";

    private static final String PATH_TEXTURE_METADATAS = "textureMetadatas";

    private final DatabaseReference reference;

    private final FirebaseAuth auth;

    public TextureEntryRepositoryImpl(DatabaseReference reference, FirebaseAuth auth) {
        if (reference == null) throw new ArgumentNullException("reference");
        if (auth == null) throw new ArgumentNullException("auth");

        this.reference = reference;
        this.auth = auth;
    }

    @Override
    public Single<String> save(String entryId, String fileId, TextureMetadata metadata) {
        LOG.d("Saving the entry: entryId = %s, fileId = %s, metadata = %s", entryId, fileId, metadata);

        return Single.create(subscriber -> {

            Map<String, Object> updates = new HashMap<>();
            updates.put(PATH_TEXTURE_METADATAS + "/" + entryId, metadata);
            updates.put(PATH_TEXTURE_FILES + "/" + entryId, fileId);

            getUserFolder().updateChildren(updates)
                           .addOnSuccessListener(aVoid -> {
                               LOG.d("Saved the entry.");
                               subscriber.onSuccess(entryId);
                           })
                           .addOnFailureListener(e -> {
                               LOG.e("Failed to save the entry: entryId = %s, fileId = %s, metadata = %s",
                                     entryId, fileId, metadata);
                               subscriber.onError(e);
                           });
        });
    }

    @Override
    public Observable<String> findFileId(String entryId) {
        LOG.d("Finding the file id: entryId = %s", entryId);

        return Observable.create(subscriber -> {
            getUserFolder().child(PATH_TEXTURE_FILES)
                           .orderByKey()
                           .equalTo(entryId)
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   if (0 < dataSnapshot.getChildrenCount()) {
                                       DataSnapshot child = dataSnapshot.getChildren().iterator().next();

                                       String fileId = child.getValue(String.class);

                                       LOG.d("Found the file id: fileId = %s", fileId);

                                       subscriber.onNext(fileId);
                                   } else {
                                       LOG.d("Found no entry.");
                                   }

                                   subscriber.onCompleted();
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   LOG.e("Cancelled to find the file id: entryId = %s", entryId);
                                   subscriber.onError(new DatabaseErrorException(databaseError));
                               }
                           });
        });
    }

    private DatabaseReference getUserFolder() {
        String userId = resolveUserId();
        return reference.child(userId);
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }
}
