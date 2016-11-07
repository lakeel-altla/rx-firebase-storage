package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.BitmapModel;
import com.lakeel.altla.vision.builder.presentation.presenter.MainPresenter;

import android.support.annotation.NonNull;

public interface ModelListItemView {

    void setItemPresenter(@NonNull MainPresenter.ModelItemPresenter itemPresenter);

    void showModel(@NonNull BitmapModel model);

    void startDrag();
}
