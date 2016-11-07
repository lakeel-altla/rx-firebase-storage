package com.lakeel.altla.vision.builder.domain.repository;

import com.lakeel.altla.vision.builder.domain.model.ImageReference;

import rx.Observable;
import rx.Single;

public interface ImageReferenceRepository {

    Single<ImageReference> create(ImageReference imageReference);

    Observable<ImageReference> findAll();
}
