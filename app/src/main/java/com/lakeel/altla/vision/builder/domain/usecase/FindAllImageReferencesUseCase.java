package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.domain.model.ImageReference;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllImageReferencesUseCase {

    @Inject
    ImageReferenceRepository imageReferenceRepository;

    @Inject
    public FindAllImageReferencesUseCase() {
    }

    public Observable<ImageReference> execute() {
        return imageReferenceRepository.findAll()
                                       .subscribeOn(Schedulers.io());
    }
}
