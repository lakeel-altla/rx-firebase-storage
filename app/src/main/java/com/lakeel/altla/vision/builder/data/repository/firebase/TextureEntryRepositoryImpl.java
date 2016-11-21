package com.lakeel.altla.vision.builder.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.rx.tasks.RxGmsTask;
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

        Map<String, Object> updates = new HashMap<>();
        updates.put(PATH_TEXTURE_ENTRIES + "/" + id, name);
        updates.put(PATH_TEXTURE_REFERENCES + "/" + id, fileId);
        updates.put(PATH_TEXTURE_METADATAS + "/" + id, metadata);

        Task<Void> task = getUserFolder().updateChildren(updates);
        return RxGmsTask.asSingle(task).map(aVoid -> id);
    }

    @Override
    public Observable<TextureEntry> findEntry(String id) {
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
                                       subscriber.onNext(entry);
                                   }

                                   subscriber.onCompleted();
                               }

                               @Override
                               public void onCancelled(DatabaseError error) {
                                   subscriber.onError(new DatabaseErrorException(error));
                               }
                           });
        });
    }

    @Override
    public Observable<TextureEntry> findAllEntries() {
        return Observable.create(subscriber -> {
            getUserFolder().child(PATH_TEXTURE_ENTRIES)
                           .orderByValue()
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot snapshot) {
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
                                   subscriber.onError(new DatabaseErrorException(error));
                               }
                           });
        });
    }

    @Override
    public Observable<TextureReference> findReference(String id) {
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

                                       subscriber.onNext(reference);
                                   }

                                   subscriber.onCompleted();
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
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
