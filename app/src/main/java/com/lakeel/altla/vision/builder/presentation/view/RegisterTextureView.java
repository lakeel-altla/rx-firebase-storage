package com.lakeel.altla.vision.builder.presentation.view;

import android.graphics.Bitmap;
import android.support.annotation.StringRes;

public interface RegisterTextureView {

    void showSnackbar(@StringRes int resId);

    void showImagePicker();

    void showImage(Bitmap bitmap);

    void showFilename(String filename);

    void showUploadProgressDialog();

    void setUploadProgressDialogProgress(long max, long diff);

    void hideUploadProgressDialog();

    void saveIdAsInstanceState(String id);
}
