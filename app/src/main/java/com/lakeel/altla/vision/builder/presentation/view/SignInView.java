package com.lakeel.altla.vision.builder.presentation.view;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface SignInView {

    void showMainFragment();

    void startActivityForResult(@NonNull Intent intent, int requestCode);

    void showGoogleApiClientConnectionFailedSnackbar();

    void showGoogleSignInFailedSnackbar();

    void showGoogleSignInRequiredSnackbar();

    void showProgressDialog();

    void hideProgressDialog();
}
