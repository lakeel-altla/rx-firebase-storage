package com.lakeel.altla.vision.domain.usecase;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.lakeel.altla.rx.tasks.RxGmsTask;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class SignInToFirebaseUseCase {

    @Inject
    FirebaseAuth auth;

    @Inject
    public SignInToFirebaseUseCase() {
    }

    public Single<AuthResult> execute(GoogleSignInAccount googleSignInAccount) {
        return Single
                .just(googleSignInAccount)
                .map(_account -> GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null))
                .flatMap(authCredential -> Single.just(auth.signInWithCredential(authCredential)))
                .flatMap(RxGmsTask::asSingle)
                .subscribeOn(Schedulers.io());
    }
}
