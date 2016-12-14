package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserProfile;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import rx.Observable;
import rx.Single;

public final class UserProfileRepositoryImpl implements UserProfileRepository {

    private static final Log LOG = LogFactory.getLog(UserProfileRepositoryImpl.class);

    private static final String PATH_USER_PROFILES = "userProfiles";

    private final DatabaseReference rootReference;

    public UserProfileRepositoryImpl(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Single<UserProfile> save(UserProfile userProfile) {
        if (userProfile == null) throw new ArgumentNullException("userProfile");

        UserProfileValue value = new UserProfileValue();
        value.displayName = userProfile.displayName;
        value.photoUri = userProfile.photoUri;

        rootReference.child(PATH_USER_PROFILES)
                     .child(userProfile.id)
                     .setValue(value, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save the user profile: id = %s", userProfile.id),
                                   error.toException());
                         }
                     });

        return Single.just(userProfile);
    }

    @Override
    public Observable<UserProfile> find(String id) {
        if (id == null) throw new ArgumentNullException("id");

        DatabaseReference reference = rootReference.child(PATH_USER_PROFILES).child(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(reference)
                              .flatMap(this::parseUserProfile);
    }

    private Observable<UserProfile> parseUserProfile(DataSnapshot snapshot) {
        return Observable.create(subscriber -> {
            if (snapshot.exists()) {
                String id = snapshot.getKey();
                UserProfileValue value = snapshot.getValue(UserProfileValue.class);

                UserProfile userProfile = new UserProfile(id);
                userProfile.displayName = value.displayName;
                userProfile.photoUri = value.photoUri;

                subscriber.onNext(userProfile);
            }
            subscriber.onCompleted();
        });
    }

    public static final class UserProfileValue {

        public String displayName;

        public String photoUri;
    }
}
