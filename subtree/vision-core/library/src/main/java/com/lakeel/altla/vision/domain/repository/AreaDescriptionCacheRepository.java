package com.lakeel.altla.vision.domain.repository;

import java.io.File;

import rx.Single;

public interface AreaDescriptionCacheRepository {

    Single<File> getDirectory();

    Single<File> getFile(String id);
}
