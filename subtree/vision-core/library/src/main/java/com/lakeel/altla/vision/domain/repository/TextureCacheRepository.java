package com.lakeel.altla.vision.domain.repository;

import java.io.File;

import rx.Observable;
import rx.Single;

public interface TextureCacheRepository {

    Observable<File> find(String fileId);

    Single<File> create(String fileId);

    Single<String> delete(String fileId);
}
