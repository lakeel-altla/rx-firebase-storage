package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.presentation.view.TangoPermissionView;

import android.support.annotation.NonNull;

import javax.inject.Inject;

public final class TangoPermissionPresenter {

    private TangoPermissionView view;

    @Inject
    public TangoPermissionPresenter() {
    }

    public void onCreateView(@NonNull TangoPermissionView view) {
        this.view = view;

        onConfirmPermission();
    }

    public void onConfirmPermission() {
        view.startTangoPermissionActivity();
    }

    public void onTangoPermissionResult(boolean isCanceled) {
        if (!isCanceled) {
            view.showMainFragment();
        } else {
            view.showAreaLearningPermissionRequiredSnackbar();
        }
    }
}
