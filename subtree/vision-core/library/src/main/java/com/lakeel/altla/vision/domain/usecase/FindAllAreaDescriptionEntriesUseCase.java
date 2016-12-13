package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllAreaDescriptionEntriesUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public FindAllAreaDescriptionEntriesUseCase() {
    }

    public Observable<UserAreaDescription> execute() {
        return userAreaDescriptionRepository.findAll()
                                            .subscribeOn(Schedulers.io());
    }
}
