package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.TextureEntry;
import com.lakeel.altla.vision.domain.model.TextureMetadata;
import com.lakeel.altla.vision.domain.model.TextureReference;
import com.lakeel.altla.vision.domain.repository.TextureEntryRepository;

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
        Query query = getUserFolder().child(PATH_TEXTURE_ENTRIES)
                                     .orderByKey()
                                     .equalTo(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(query)
                              .map(snapshot -> {
                                  DataSnapshot child = snapshot.getChildren().iterator().next();
                                  String name = child.getValue(String.class);
                                  return new TextureEntry(id, name);
                              });
    }

    @Override
    public Observable<TextureEntry> findAllEntries() {
        Query query = getUserFolder().child(PATH_TEXTURE_ENTRIES)
                                     .orderByValue();

        return RxFirebaseQuery.asObservableForSingleValueEvent(query)
                              .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
                              .map(snapshot -> {
                                  String id = snapshot.getKey();
                                  String name = snapshot.getValue(String.class);
                                  return new TextureEntry(id, name);
                              });
    }

    @Override
    public Observable<TextureReference> findReference(String id) {
        Query query = getUserFolder().child(PATH_TEXTURE_REFERENCES)
                                     .orderByKey()
                                     .equalTo(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(query)
                              .flatMap(snapshot -> Observable.create(subscriber -> {
                                  if (0 < snapshot.getChildrenCount()) {
                                      DataSnapshot child = snapshot.getChildren().iterator().next();
                                      String fileId = child.getValue(String.class);

                                      TextureReference reference = new TextureReference(id, fileId);
                                      subscriber.onNext(reference);
                                  }

                                  subscriber.onCompleted();
                              }));
    }

    @Override
    public Single<String> delete(String id) {
        // Use updateChildren(...) to atomize multiple reference deletes,
        Map<String, Object> updates = new HashMap<>();
        updates.put(PATH_TEXTURE_ENTRIES + "/" + id, null);
        updates.put(PATH_TEXTURE_REFERENCES + "/" + id, null);
        updates.put(PATH_TEXTURE_METADATAS + "/" + id, null);

        Task<Void> task = getUserFolder().updateChildren(updates);
        return RxGmsTask.asSingle(task).map(aVoid -> id);
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
