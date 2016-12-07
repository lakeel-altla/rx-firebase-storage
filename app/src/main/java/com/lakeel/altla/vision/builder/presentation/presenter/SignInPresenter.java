package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.usecase.SignInToFirebaseUseCase;
import com.lakeel.altla.vision.builder.presentation.view.SignInView;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Defines the presenter for {@link SignInView}.
 */
public final class SignInPresenter {

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    AppCompatActivity activity;

    @Inject
    SignInToFirebaseUseCase signInToFirebaseUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final FirebaseAuth.AuthStateListener authStateListener;

    private SignInView view;

    private boolean mIsSignedInDetected;

    @Inject
    public SignInPresenter() {
        // See:
        // http://stackoverflow.com/questions/37674823/firebase-android-onauthstatechanged-fire-twice-after-signinwithemailandpasswor
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (!mIsSignedInDetected) {
                    LOG.i("Signed in to firebase: %s", user.getUid());
                    view.hideProgressDialog();
                    view.showMainFragment();
                    mIsSignedInDetected = true;
                } else {
                    LOG.d("onAuthStateChanged() is fired twice.");
                }
            } else {
                LOG.d("Signed out.");
            }
        };
    }

    public void onCreateView(@NonNull SignInView view) {
        this.view = view;
    }

    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    public void onSignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        view.startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_GOOGLE_SIGN_IN) {
            // Ignore.
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            LOG.d("Canceled to sign in to Google.");
            view.showSnackbar(R.string.snackbar_google_sign_in_reqiured);
        }

        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (!googleSignInResult.isSuccess()) {
            LOG.e("Failed to sign in to Google.");
            view.showSnackbar(R.string.snackbar_google_sign_in_failed);
        }

        GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
        if (googleSignInAccount == null) {
            LOG.e("GoogleSignInAccount is null");
            view.showSnackbar(R.string.snackbar_google_sign_in_failed);
        }

        // If the sign-in is successful, Firebase's callback is called first rather than RX's processing
        // so that the dialog is closed with Firebase's callback.
        view.showProgressDialog();

        Subscription subscription = signInToFirebaseUseCase
                .execute(googleSignInAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(authResult -> {
                    // As the main thread is called first, the fragments are discarded in Firebase's callback,
                    // so the RX processing is also canceled and will not be called here.
                }, e -> {
                    LOG.e("Failed to sign in to Firebase.", e);
                    view.hideProgressDialog();
                });
        compositeSubscription.add(subscription);
    }
}
