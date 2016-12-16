package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserConnection;

import rx.Single;

public interface UserConnectionRepository {

    Single<UserConnection> setOnline(UserConnection userConnection);

    Single<UserConnection> setOffline(UserConnection userConnection);
}
