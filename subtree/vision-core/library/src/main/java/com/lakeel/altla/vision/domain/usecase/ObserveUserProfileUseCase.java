package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserProfile;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class ObserveUserProfileUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    public ObserveUserProfileUseCase() {
    }

    public Observable<UserProfile> execute(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return userProfileRepository.observe(id)
                                    .subscribeOn(Schedulers.io());
    }
}
