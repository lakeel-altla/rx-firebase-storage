package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.mapper.UserAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public FindAllAreaDescriptionUseCase() {
    }

    public Observable<UserAreaDescription> execute() {
        // Find all area descriptions that are stored in Tango.
        return tangoAreaDescriptionMetadataRepository.findAll()
                                                     // Map it to a model for internal use.
                                                     .map(UserAreaDescriptionMapper::map)
                                                     // Check whether it is synchronized with the server.
                                                     .flatMap(this::resolveAreaDescription)
                                                     .subscribeOn(Schedulers.io());
    }

    private Observable<UserAreaDescription> resolveAreaDescription(UserAreaDescription tangoAreaDescroption) {
        return userAreaDescriptionRepository.find(tangoAreaDescroption.id)
                                            .map(userAreaDescription -> {
                                                // Mark as synced.
                                                userAreaDescription.synced = true;
                                                return userAreaDescription;
                                            })
                                            .defaultIfEmpty(tangoAreaDescroption);
    }
}
