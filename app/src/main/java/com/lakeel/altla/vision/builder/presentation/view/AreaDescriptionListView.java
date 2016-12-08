package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface AreaDescriptionListView {

    void updateItems();

    void updateItem(int position);

    void updateItemRemoved(int position);

    void showExportActivity(@NonNull String id, @NonNull File destinationDirectory);

    void showUploadProgressDialog();

    void setUploadProgressDialogProgress(long max, long diff);

    void hideUploadProgressDialog();

    void showDesyncProgressDialog();

    void hideDesyncProgressDialog();

    void showSnackbar(@StringRes int resId);
}
