package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.AreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaDescriptionListPresenter;

import android.support.annotation.NonNull;

public interface AreaDescriptionListItemView {

    void setItemPresenter(@NonNull AreaDescriptionListPresenter.ItemPresenter itemPresenter);

    void showModel(@NonNull AreaDescriptionModel model);
}
