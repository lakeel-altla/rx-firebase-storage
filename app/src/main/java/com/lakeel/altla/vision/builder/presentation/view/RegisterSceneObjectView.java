package com.lakeel.altla.vision.builder.presentation.view;

import android.graphics.Bitmap;
import android.support.annotation.StringRes;

public interface RegisterSceneObjectView {

    void showSnackbar(@StringRes int resId);

    void showImagePicker();

    void showPickedImage(Bitmap bitmap);

    void showUploadProgressDialog();

    void setUploadProgressDialogProgress(long max, long diff);

    void hideUploadProgressDialog();
}
