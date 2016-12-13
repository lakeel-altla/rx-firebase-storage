package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllUserTexturesUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    public FindAllUserTexturesUseCase() {
    }

    public Observable<UserTexture> execute() {
        return userTextureRepository.findAll()
                                    .subscribeOn(Schedulers.io());
    }
}
