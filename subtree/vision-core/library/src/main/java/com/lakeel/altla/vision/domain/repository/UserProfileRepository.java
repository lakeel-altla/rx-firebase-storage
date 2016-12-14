package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserProfile;

import rx.Observable;
import rx.Single;

public interface UserProfileRepository {

    Single<UserProfile> save(UserProfile userProfile);

    Observable<UserProfile> find(String id);

    Observable<UserProfile> observe(String id);
}
