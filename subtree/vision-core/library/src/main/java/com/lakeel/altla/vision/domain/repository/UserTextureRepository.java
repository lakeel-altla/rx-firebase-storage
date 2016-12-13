package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserTexture;

import rx.Observable;
import rx.Single;

public interface UserTextureRepository {

    Single<UserTexture> save(UserTexture userTexture);

    Observable<UserTexture> find(String id);

    Observable<UserTexture> findAll();

    Single<String> delete(String id);
}
