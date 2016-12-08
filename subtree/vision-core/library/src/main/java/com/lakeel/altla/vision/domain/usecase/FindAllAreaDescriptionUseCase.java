package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionEntryRepository;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    AreaDescriptionEntryRepository areaDescriptionEntryRepository;

    @Inject
    public FindAllAreaDescriptionUseCase() {
    }

    public Observable<AreaDescription> execute() {
        return tangoAreaDescriptionMetadataRepository
                // Find all area descriptions that are stored in Tango.
                .findAll()
                // Map it to a model for internal use.
                .map(this::toModel)
                // Check whether it is synchronized with the server.
                .flatMap(this::resolveAreaDescription)
                .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescription> resolveAreaDescription(Model model) {
        return areaDescriptionEntryRepository
                .findEntry(model.id)
                .map(entry -> new AreaDescription(model.id, model.name, true))
                .defaultIfEmpty(new AreaDescription(model.id, model.name, false));
    }

    private Model toModel(TangoAreaDescriptionMetaData metaData) {
        Model model = new Model();
        model.id = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
        model.name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        return model;
    }

    /**
     * Defines the model for internal use.
     */
    private final class Model {

        public String id;

        public String name;
    }
}
