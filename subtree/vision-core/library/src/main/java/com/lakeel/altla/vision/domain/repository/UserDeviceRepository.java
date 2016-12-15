package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserDevice;

import rx.Single;

public interface UserDeviceRepository {

    Single<UserDevice> save(UserDevice userDevice);
}
