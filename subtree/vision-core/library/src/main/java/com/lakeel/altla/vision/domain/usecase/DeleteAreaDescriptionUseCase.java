package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    public DeleteAreaDescriptionUseCase() {
    }

    public Single<String> execute(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return userAreaDescriptionFileRepository.delete(id)
                                                .flatMap(userAreaDescriptionRepository::delete)
                                                .subscribeOn(Schedulers.io());
    }
}
