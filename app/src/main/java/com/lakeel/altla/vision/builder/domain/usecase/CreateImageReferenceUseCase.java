package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.domain.model.ImageReference;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class CreateImageReferenceUseCase {

    @Inject
    ImageReferenceRepository mImageReferenceRepository;

    @Inject
    public CreateImageReferenceUseCase() {
    }

    public Single<ImageReference> execute(ImageReference imageReference) {
        return mImageReferenceRepository.create(imageReference)
                                        .subscribeOn(Schedulers.io());
    }
}
