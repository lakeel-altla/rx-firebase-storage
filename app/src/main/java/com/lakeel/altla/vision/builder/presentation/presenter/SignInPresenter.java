package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.SignInView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

/**
 * Defines the presenter for {@link SignInView}.
 */
public final class SignInPresenter implements GoogleApiClient.OnConnectionFailedListener {

    // NOTE:
    //
    // This class does not use any use-case classes because it's difficult to use them.
    // Google API and Firebase API depend on activities or fragments strongly.

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0;

    @Inject
    Resources resources;

    @Inject
    GoogleSignInOptions googleSignInOptions;

    @Inject
    AppCompatActivity activity;

    private SignInView view;

    private final FirebaseAuth.AuthStateListener authStateListener;

    private GoogleApiClient googleApiClient;

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
                    view.showMainFragment();
                    mIsSignedInDetected = true;
                } else {
                    LOG.d("onAuthStateChanged() fired twice.");
                }
            } else {
                LOG.i("signed out from firebase.");
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LOG.e("onConnectionFailed: %s", connectionResult);
        view.showSnackbar(R.string.snackbar_google_api_client_connection_failed);
    }

    public void onCreateView(@NonNull SignInView view) {
        this.view = view;

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
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

        if (resultCode == Activity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                LOG.i("Google Sign-In succeeded.");
                LOG.d("Authenticating with Firebase...");

                view.showProgressDialog();

                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener(activity, task -> {
                                    if (!task.isSuccessful()) {
                                        LOG.e("Firebase Sign-In failed.", task.getException());
                                    }
                                    view.hideProgressDialog();
                                });
                } else {
                    LOG.e("GoogleSignInAccount is null.");
                    view.showSnackbar(R.string.snackbar_google_sign_in_failed);
                }
            } else {
                LOG.e("Google Sign-In failed.");
                view.showSnackbar(R.string.snackbar_google_sign_in_failed);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            LOG.i("Google Sign-In canceled.");
            view.showSnackbar(R.string.snackbar_google_sign_in_reqiured);
        }

    }
}
