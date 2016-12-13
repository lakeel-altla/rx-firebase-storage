package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionEntryRepository;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Single;

public final class AreaDescriptionEntryRepositoryImpl implements AreaDescriptionEntryRepository {

    private static final String PATH_USER_AREA_DESCRIPTIONS_ENTRIES = "userAreaDescriptionEntries";

    private static final String PATH_USER_AREA_DESCRIPTIONS_METADATAS = "userAreaDescriptionMetadatas";

    private final DatabaseReference rootReference;

    private final FirebaseAuth auth;

    public AreaDescriptionEntryRepositoryImpl(DatabaseReference rootReference, FirebaseAuth auth) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");
        if (auth == null) throw new ArgumentNullException("auth");

        this.rootReference = rootReference;
        this.auth = auth;
    }

    @Override
    public Single<AreaDescriptionEntry> save(AreaDescriptionEntry entry, AreaDescriptionMetadata metadata) {
        if (entry == null) throw new ArgumentNullException("entry");
        if (metadata == null) throw new ArgumentNullException("metadata");

        String userId = resolveUserId();

        Map<String, Object> updates = new HashMap<>();
        updates.put(PATH_USER_AREA_DESCRIPTIONS_ENTRIES + "/" + userId + "/" + entry.id, entry.name);
        updates.put(PATH_USER_AREA_DESCRIPTIONS_METADATAS + "/" + userId + "/" + entry.id, metadata);

        Task<Void> task = rootReference.updateChildren(updates);
        return RxGmsTask.asSingle(task).map(aVoid -> entry);
    }

    @Override
    public Observable<AreaDescriptionEntry> findEntry(String id) {
        if (id == null) throw new ArgumentNullException("id");

        Query query = rootReference.child(PATH_USER_AREA_DESCRIPTIONS_ENTRIES)
                                   .child(resolveUserId())
                                   .orderByKey()
                                   .equalTo(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(query)
                              .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
                              .map(snapshot -> {
                                  String name = snapshot.getValue(String.class);
                                  return new AreaDescriptionEntry(id, name);
                              });
    }

    @Override
    public Observable<AreaDescriptionEntry> findAllEntries() {
        Query query = rootReference.child(PATH_USER_AREA_DESCRIPTIONS_ENTRIES)
                                   .child(resolveUserId())
                                   .orderByValue();

        return RxFirebaseQuery.asObservableForSingleValueEvent(query)
                              .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
                              .map(snapshot -> {
                                  String id = snapshot.getKey();
                                  String name = snapshot.getValue(String.class);
                                  return new AreaDescriptionEntry(id, name);
                              });
    }

    @Override
    public Single<String> delete(String id) {
        String userId = resolveUserId();

        // Use updateChildren(...) to atomize multiple reference deletes,
        Map<String, Object> updates = new HashMap<>();
        updates.put(PATH_USER_AREA_DESCRIPTIONS_ENTRIES + "/" + userId + "/" + id, null);
        updates.put(PATH_USER_AREA_DESCRIPTIONS_METADATAS + "/" + userId + "/" + id, null);

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
