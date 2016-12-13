package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import rx.Observable;
import rx.Single;

public final class UserAreaDescriptionRepositoryImpl implements UserAreaDescriptionRepository {

    private static final String PATH_USER_AREA_DESCRIPTIONS = "userAreaDescriptions";

    private final DatabaseReference rootReference;

    public UserAreaDescriptionRepositoryImpl(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Single<UserAreaDescription> save(UserAreaDescription userAreaDescription) {
        if (userAreaDescription == null) throw new ArgumentNullException("userAreaDescription");

        UserAreaDescriptionValue value = new UserAreaDescriptionValue();
        value.name = userAreaDescription.name;
        value.creationTime = userAreaDescription.creationTime;

        rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(resolveCurrentUserId())
                     .child(userAreaDescription.id)
                     .setValue(value);

        return Single.just(userAreaDescription);
    }

    @Override
    public Observable<UserAreaDescription> find(String id) {
        if (id == null) throw new ArgumentNullException("id");

        DatabaseReference reference = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                                   .child(resolveCurrentUserId())
                                                   .child(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(reference)
                              .flatMap(this::parseUserAreaDescriptionValue)
                              .map(value -> new UserAreaDescription(id, value.name, value.creationTime));
    }

    private Observable<UserAreaDescriptionValue> parseUserAreaDescriptionValue(DataSnapshot snapshot) {
        return Observable.create(subscriber -> {
            if (snapshot.exists()) {
                UserAreaDescriptionValue value = snapshot.getValue(UserAreaDescriptionValue.class);
                subscriber.onNext(value);
            }
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<UserAreaDescription> findAll() {
        Query query = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                   .child(resolveCurrentUserId())
                                   .orderByValue();

        return RxFirebaseQuery
                .asObservableForSingleValueEvent(query)
                .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
                .map(snapshot -> {
                    String id = snapshot.getKey();
                    UserAreaDescriptionValue value = snapshot.getValue(UserAreaDescriptionValue.class);
                    return new UserAreaDescription(id, value.name, value.creationTime);
                });
    }

    @Override
    public Single<String> delete(String id) {
        rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(resolveCurrentUserId())
                     .child(id)
                     .removeValue();

        return Single.just(id);
    }

    private String resolveCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }

    public static final class UserAreaDescriptionValue {

        public String name;

        public long creationTime;
    }
}
