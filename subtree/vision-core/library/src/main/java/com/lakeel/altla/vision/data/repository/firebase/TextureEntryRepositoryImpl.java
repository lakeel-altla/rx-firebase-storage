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

    private static final String PATH_USER_TEXTURE_ENTRIES = "userTextureEntries";

    private static final String PATH_USER_TEXTURE_REFERENCES = "userTextureReferences";

    private static final String PATH_USER_TEXTURE_METADATAS = "userTextureMetadatas";

    private final DatabaseReference rootReference;

    private final FirebaseAuth auth;

    public TextureEntryRepositoryImpl(DatabaseReference rootReference, FirebaseAuth auth) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");
        if (auth == null) throw new ArgumentNullException("auth");

        this.rootReference = rootReference;
        this.auth = auth;
    }

    @Override
    public Single<String> save(String id, String name, String fileId, TextureMetadata metadata) {

        String userId = resolveUserId();

        Map<String, Object> updates = new HashMap<>();
        updates.put(PATH_USER_TEXTURE_ENTRIES + "/" + userId + "/" + id, name);
        updates.put(PATH_USER_TEXTURE_REFERENCES + "/" + userId + "/" + id, fileId);
        updates.put(PATH_USER_TEXTURE_METADATAS + "/" + userId + "/" + id, metadata);

        Task<Void> task = rootReference.updateChildren(updates);
        return RxGmsTask.asSingle(task).map(aVoid -> id);
    }

    @Override
    public Observable<TextureEntry> findEntry(String id) {
        Query query = rootReference.child(PATH_USER_TEXTURE_ENTRIES)
                                   .child(resolveUserId())
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
        Query query = rootReference.child(PATH_USER_TEXTURE_ENTRIES)
                                   .child(resolveUserId())
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
        Query query = rootReference.child(PATH_USER_TEXTURE_REFERENCES)
                                   .child(resolveUserId())
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

        String userId = resolveUserId();

        // Use updateChildren(...) to atomize multiple reference deletes,
        Map<String, Object> updates = new HashMap<>();
        updates.put(PATH_USER_TEXTURE_ENTRIES + "/" + userId + "/" + id, null);
        updates.put(PATH_USER_TEXTURE_REFERENCES + "/" + userId + "/" + id, null);
        updates.put(PATH_USER_TEXTURE_METADATAS + "/" + userId + "/" + id, null);

        Task<Void> task = rootReference.updateChildren(updates);
        return RxGmsTask.asSingle(task).map(aVoid -> id);
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }

        return user.getUid();
    }
}
