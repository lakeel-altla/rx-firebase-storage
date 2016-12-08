package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionEntryRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllAreaDescriptionEntriesUseCase {

    @Inject
    AreaDescriptionEntryRepository areaDescriptionEntryRepository;

    @Inject
    public FindAllAreaDescriptionEntriesUseCase() {
    }

    public Observable<AreaDescriptionEntry> execute() {
        return areaDescriptionEntryRepository.findAllEntries()
                                             .subscribeOn(Schedulers.io());
    }
}
