package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.TextureModel;
import com.lakeel.altla.vision.builder.presentation.presenter.MainPresenter;

import android.support.annotation.NonNull;

public interface TextureModelListItemView {

    void setItemPresenter(@NonNull MainPresenter.ModelItemPresenter itemPresenter);

    void showModel(@NonNull TextureModel model);

    void showProgress(int max, int progress);

    void hideProgress();

    void startDrag();

    void setSelected(int selectedPosition, boolean selected);

    void showDeleteTextureConfirmationDialog();
}
