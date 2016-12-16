package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserConnection;
import com.lakeel.altla.vision.domain.repository.ConnectionRepository;
import com.lakeel.altla.vision.domain.repository.UserConnectionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class ObserveConnectionUseCase {

    @Inject
    ConnectionRepository connectionRepository;

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    public ObserveConnectionUseCase() {
    }

    public Observable<UserConnection> execute(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        String instanceId = FirebaseInstanceId.getInstance().getId();
        UserConnection userConnection = new UserConnection(userId, instanceId);

        return connectionRepository.observe()
                                   .flatMap(connected -> registerUserConnection(userConnection, connected))
                                   .map(connected -> userConnection)
                                   .subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> registerUserConnection(UserConnection userConnection, Boolean connected) {
        if (connected) {
            return userConnectionRepository.setOnline(userConnection)
                                           .toObservable()
                                           .map(_userConnection -> connected);
        } else {
            return Observable.just(connected);
        }
    }
}
