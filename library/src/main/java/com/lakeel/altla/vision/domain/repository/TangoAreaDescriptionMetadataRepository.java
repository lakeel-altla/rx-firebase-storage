package com.lakeel.altla.vision.domain.repository;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import rx.Observable;
import rx.Single;

public interface TangoAreaDescriptionMetadataRepository {

    Observable<TangoAreaDescriptionMetaData> find(String id);

    Observable<TangoAreaDescriptionMetaData> findAll();

    Single<String> delete(String id);
}
