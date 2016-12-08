package com.lakeel.altla.vision.domain.repository;

import java.io.InputStream;

import rx.Single;

public interface DocumentRepository {

    Single<InputStream> openStream(String uri);
}
