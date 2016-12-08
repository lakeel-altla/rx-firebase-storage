package com.lakeel.altla.vision.builder.presentation.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Defines the sign in view.
 */
public interface SignInView {

    void showTangoPermissionFragment();

    void startActivityForResult(@NonNull Intent intent, int requestCode);

    void showSnackbar(@StringRes int resId);

    void showProgressDialog();

    void hideProgressDialog();
}
