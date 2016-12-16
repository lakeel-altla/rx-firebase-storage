package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.domain.model.UserConnection;
import com.lakeel.altla.vision.domain.repository.UserConnectionRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class SignOutUseCase {

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    public SignOutUseCase() {
    }

    public Single<UserConnection> execute() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("The current user not found.");
        }

        String userId = firebaseUser.getUid();
        String instanceId = FirebaseInstanceId.getInstance().getId();
        UserConnection userConnection = new UserConnection(userId, instanceId);

        return userConnectionRepository.setOffline(userConnection)
                                       .flatMap(this::signOut)
                                       .subscribeOn(Schedulers.io());
    }

    private Single<UserConnection> signOut(UserConnection userConnection) {
        FirebaseAuth.getInstance().signOut();
        return Single.just(userConnection);
    }
}
