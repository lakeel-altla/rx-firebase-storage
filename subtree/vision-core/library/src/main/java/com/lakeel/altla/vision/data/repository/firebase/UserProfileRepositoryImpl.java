package com.lakeel.altla.vision.data.repository.firebase;

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
    public Single<UserProfile> save(String id, UserProfile userProfile) {
        if (id == null) throw new ArgumentNullException("id");
        if (userProfile == null) throw new ArgumentNullException("userProfile");

        rootReference.child(PATH_USER_PROFILES)
                     .child(id)
                     .setValue(userProfile, (databaseError, databaseReference) -> {
                         if (databaseError == null) {
                             LOG.d("Saved the user profile: id = %s", id);
                         } else {
                             LOG.e("Failed to save the user profile: id = %s, error = %s", id, databaseError);
                         }
                     });

        return Single.just(userProfile);
    }

    @Override
    public Observable<UserProfile> find(String id) {
        if (id == null) throw new ArgumentNullException("id");

        DatabaseReference reference = rootReference.child(PATH_USER_PROFILES).child(id);

        return RxFirebaseQuery.asObservableForSingleValueEvent(reference)
                              .map(snapshot -> snapshot.getValue(UserProfile.class));
    }
}
