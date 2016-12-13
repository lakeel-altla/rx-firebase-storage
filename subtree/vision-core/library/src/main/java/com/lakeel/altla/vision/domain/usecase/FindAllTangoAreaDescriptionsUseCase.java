package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.mapper.UserAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllTangoAreaDescriptionsUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public FindAllTangoAreaDescriptionsUseCase() {
    }

    public Observable<UserAreaDescription> execute() {
        return tangoAreaDescriptionMetadataRepository.findAll()
                                                     .map(UserAreaDescriptionMapper::map)
                                                     .subscribeOn(Schedulers.io());
    }
}