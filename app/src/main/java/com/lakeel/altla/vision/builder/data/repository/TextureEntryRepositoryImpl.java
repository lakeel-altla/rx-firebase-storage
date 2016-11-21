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
import com.lakeel.altla.vision.builder.domain.model.TextureEntry;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.model.TextureReference;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Single;

public final class TextureEntryRepositoryImpl implements TextureEntryRepository {

    private static final Log LOG = LogFactory.getLog(TextureEntryRepositoryImpl.class);

    private static final String PATH_TEXTURE_ENTRIES = "textureEntries";

    private static final String PATH_TEXTURE_REFERENCES = "textureReferences";

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
    public Single<String> save(String id, String name, String fileId, TextureMetadata metadata) {
        LOG.d("Saving the entry: id = %s, name = %s, fileId = %s, metadata = %s", id, name, fileId, metadata);

        return Single.create(subscriber -> {

            Map<String, Object> updates = new HashMap<>();
            updates.put(PATH_TEXTURE_ENTRIES + "/" + id, name);
            updates.put(PATH_TEXTURE_REFERENCES + "/" + id, fileId);
            updates.put(PATH_TEXTURE_METADATAS + "/" + id, metadata);

            getUserFolder().updateChildren(updates)
                           .addOnSuccessListener(aVoid -> {
                               LOG.d("Saved the entry.");
                               subscriber.onSuccess(id);
                           })
                           .addOnFailureListener(e -> {
                               LOG.e("Failed to save the entry: id = %s, name = %s, fileId = %s, metadata = %s",
                                     id, name, fileId, metadata);
                               subscriber.onError(e);
                           });
        });
    }

    @Override
    public Observable<TextureEntry> findEntry(String id) {
        LOG.d("Finding the entry: id = %s", id);

        return Observable.create(subscriber -> {
            getUserFolder().child(PATH_TEXTURE_ENTRIES)
                           .orderByKey()
                           .equalTo(id)
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot snapshot) {
                                   if (0 < snapshot.getChildrenCount()) {
                                       DataSnapshot child = snapshot.getChildren().iterator().next();

                                       String name = child.getValue(String.class);

                                       TextureEntry entry = new TextureEntry(id, name);

                                       LOG.d("Found the entry: entry = %s", entry);

                                       subscriber.onNext(entry);
                                   } else {
                                       LOG.d("Found no entry.");
                                   }

                                   subscriber.onCompleted();
                               }

                               @Override
                               public void onCancelled(DatabaseError error) {
                                   LOG.e("Cancelled to find the entry: id = %s", id);
                                   subscriber.onError(new DatabaseErrorException(error));
                               }
                           });
        });
    }

    @Override
    public Observable<TextureEntry> findAllEntries() {
        LOG.d("Finding all entries.");

        return Observable.create(subscriber -> {
            getUserFolder().child(PATH_TEXTURE_ENTRIES)
                           .orderByValue()
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot snapshot) {
                                   LOG.d("Found all entries: count = %d", snapshot.getChildrenCount());

                                   for (DataSnapshot child : snapshot.getChildren()) {
                                       String id = child.getKey();
                                       String name = child.getValue(String.class);

                                       TextureEntry entry = new TextureEntry(id, name);
                                       subscriber.onNext(entry);
                                   }

                                   subscriber.onCompleted();
                               }

                               @Override
                               public void onCancelled(DatabaseError error) {
                                   LOG.e("Cancelled to find all entries.");
                                   subscriber.onError(new DatabaseErrorException(error));
                               }
                           });
        });
    }

    @Override
    public Observable<TextureReference> findReference(String id) {
        LOG.d("Finding the reference: id = %s", id);

        return Observable.create(subscriber -> {
            getUserFolder().child(PATH_TEXTURE_REFERENCES)
                           .orderByKey()
                           .equalTo(id)
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot snapshot) {
                                   if (0 < snapshot.getChildrenCount()) {
                                       DataSnapshot child = snapshot.getChildren().iterator().next();

                                       String fileId = child.getValue(String.class);

                                       TextureReference reference = new TextureReference(id, fileId);

                                       LOG.d("Found the reference: reference = %s", reference);

                                       subscriber.onNext(reference);
                                   } else {
                                       LOG.d("Found no reference.");
                                   }

                                   subscriber.onCompleted();
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   LOG.e("Cancelled to find the reference: id = %s", id);
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
