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

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class SignInWithGoogleUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    public SignInWithGoogleUseCase() {
    }

    public Single<String> execute(GoogleSignInAccount googleSignInAccount) {
        return Single.just(googleSignInAccount)
                     .flatMap(this::signIn)
                     .flatMap(this::ensureUserProfile)
                     .map(userProfile -> userProfile.id)
                     .subscribeOn(Schedulers.io());
    }

    private Single<FirebaseUser> signIn(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        Task<AuthResult> task = FirebaseAuth.getInstance().signInWithCredential(credential);

        return RxGmsTask.asSingle(task)
                        .map(AuthResult::getUser);
    }

    private Single<UserProfile> ensureUserProfile(FirebaseUser firebaseUser) {
        // Check if the user profile exists.
        return userProfileRepository.find(firebaseUser.getUid())
                                    // Create the user profile if it does not exist.
                                    .switchIfEmpty(saveUserProfile(firebaseUser))
                                    .toSingle();
    }

    private Observable<UserProfile> saveUserProfile(final FirebaseUser firebaseUser) {
        return Single
                .<UserProfile>create(subscriber -> {
                    UserProfile userProfile = new UserProfile(firebaseUser.getUid());
                    userProfile.displayName = firebaseUser.getDisplayName();
                    userProfile.email = firebaseUser.getEmail();
                    if (firebaseUser.getPhotoUrl() != null) {
                        userProfile.photoUri = firebaseUser.getPhotoUrl().toString();
                    }

                    subscriber.onSuccess(userProfile);
                })
                .flatMap(userProfileRepository::save)
                .toObservable();
    }
}
