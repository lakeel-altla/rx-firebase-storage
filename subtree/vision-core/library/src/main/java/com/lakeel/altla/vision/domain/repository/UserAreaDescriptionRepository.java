package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import rx.Observable;
import rx.Single;

public interface UserAreaDescriptionRepository {

    Single<UserAreaDescription> save(UserAreaDescription userAreaDescription);

    Observable<UserAreaDescription> find(String id);

    Observable<UserAreaDescription> findAll();

    Single<String> delete(String id);
}
