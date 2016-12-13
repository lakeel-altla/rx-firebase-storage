package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.mapper.UserAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.AreaDescription;
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

    public Observable<AreaDescription> execute() {
        // Find all area descriptions that are stored in Tango.
        return tangoAreaDescriptionMetadataRepository.findAll()
                                                     // Map it to a model for internal use.
                                                     .map(UserAreaDescriptionMapper::map)
                                                     // Check whether it is synchronized with the server.
                                                     .flatMap(this::resolveAreaDescription)
                                                     .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescription> resolveAreaDescription(UserAreaDescription tangoAreaDescroption) {
        return userAreaDescriptionRepository.find(tangoAreaDescroption.id)
                                            .map(userAreaDescription -> new AreaDescription(userAreaDescription.id,
                                                                                            userAreaDescription.name,
                                                                                            true))
                                            .defaultIfEmpty(new AreaDescription(tangoAreaDescroption.id,
                                                                                tangoAreaDescroption.name,
                                                                                false));
    }

    /**
     * Defines the model for internal use.
     */
    private final class Model {

        public String id;

        public String name;
    }
}
