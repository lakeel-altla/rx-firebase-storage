package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import rx.Observable;
import rx.Single;

public final class UserTextureRepositoryImpl implements UserTextureRepository {

    private static final String PATH_USER_TEXTURES = "userTextures";

    private final DatabaseReference rootReference;

    public UserTextureRepositoryImpl(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Single<UserTexture> save(UserTexture userTexture) {
        if (userTexture == null) throw new ArgumentNullException("userTexture");

        UserTextureValue value = new UserTextureValue();
        value.name = userTexture.name;

        rootReference.child(PATH_USER_TEXTURES)
                     .child(resolveCurrentUserId())
                     .child(userTexture.id)
                     .setValue(value);

        return Single.just(userTexture);
    }

    @Override
    public Observable<UserTexture> find(String id) {
        if (id == null) throw new ArgumentNullException("id");

        DatabaseReference reference = rootReference.child(PATH_USER_TEXTURES)
                                                   .child(resolveCurrentUserId())
                                                   .child(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(reference)
                              .filter(DataSnapshot::exists)
                              .map(this::map);
    }

    @Override
    public Observable<UserTexture> findAll() {
        Query query = rootReference.child(PATH_USER_TEXTURES)
                                   .child(resolveCurrentUserId())
                                   .orderByChild(UserTextureValue.FIELD_NAME);

        return RxFirebaseQuery.asObservableForSingleValueEvent(query)
                              .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
                              .map(this::map);
    }

    @Override
    public Single<String> delete(String id) {
        if (id == null) throw new ArgumentNullException("id");

        rootReference.child(PATH_USER_TEXTURES)
                     .child(resolveCurrentUserId())
                     .child(id)
                     .removeValue();

        return Single.just(id);
    }

    private UserTexture map(DataSnapshot snapshot) {
        String id = snapshot.getKey();
        UserTextureValue value = snapshot.getValue(UserTextureValue.class);

        return new UserTexture(id, value.name);
    }

    private String resolveCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }

        return user.getUid();
    }

    public static final class UserTextureValue {

        private static final String FIELD_NAME = "name";

        public String name;
    }
}
