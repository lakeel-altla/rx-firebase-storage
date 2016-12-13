package com.lakeel.altla.vision.domain.usecase;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.domain.model.UserProfile;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class SignInToFirebaseUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    public SignInToFirebaseUseCase() {
    }

    public Single<AuthResult> execute(GoogleSignInAccount googleSignInAccount) {
        return Single.just(new Model(googleSignInAccount))
                     .flatMap(this::getCredential)
                     .flatMap(this::signInWithCredential)
                     .flatMap(this::getAuthResult)
                     .flatMap(this::saveUserProfile)
                     .map(model -> model.authResult)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> getCredential(Model model) {
        return Single.create(subscriber -> {
            model.authCredential = GoogleAuthProvider.getCredential(model.googleSignInAccount.getIdToken(), null);
            subscriber.onSuccess(model);
        });
    }

    private Single<Model> signInWithCredential(Model model) {
        return Single.create(subscriber -> {
            model.authResultTask = FirebaseAuth.getInstance().signInWithCredential(model.authCredential);
            subscriber.onSuccess(model);
        });
    }

    private Single<Model> getAuthResult(Model model) {
        return RxGmsTask.asSingle(model.authResultTask)
                        .map(authResult -> {
                            model.authResult = authResult;
                            return model;
                        });
    }

    private Single<Model> saveUserProfile(Model model) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("FirebaseUser is null.");
        }

        String id = firebaseUser.getUid();

        UserProfile userProfile = new UserProfile();
        userProfile.displayName = firebaseUser.getDisplayName();
        if (firebaseUser.getPhotoUrl() != null) {
            userProfile.photoUri = firebaseUser.getPhotoUrl().toString();
        }

        return userProfileRepository.save(id, userProfile)
                                    .map(_userProfile -> model);
    }

    private final class Model {

        final GoogleSignInAccount googleSignInAccount;

        AuthCredential authCredential;

        Task<AuthResult> authResultTask;

        AuthResult authResult;

        Model(GoogleSignInAccount googleSignInAccount) {
            this.googleSignInAccount = googleSignInAccount;
        }
    }
}
