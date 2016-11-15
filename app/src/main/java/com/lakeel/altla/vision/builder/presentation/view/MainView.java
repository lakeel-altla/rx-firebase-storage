package com.lakeel.altla.vision.builder.presentation.view;

import com.google.atap.tango.ux.TangoUx;

import com.lakeel.altla.vision.builder.presentation.model.Axis;

import org.rajawali3d.renderer.ISurfaceRenderer;

import android.support.annotation.StringRes;

/**
 * Defines the main view.
 */
public interface MainView {

    void setTangoUxLayout(TangoUx tangoUx);

    void setSurfaceRenderer(ISurfaceRenderer renderer);

//    void requestRender();

    void showSnackbar(@StringRes int resId);

    void setModelPaneVisible(boolean visible);

    void showRegisterSceneObjectFragment(boolean editMode);

//    void showSelectImageMethodDialog(@ArrayRes int itemsId);

//    void showImagePicker();

    void updateModels();

    void setObjectMenuVisible(boolean visible);

    void setTranslateObjectSelected(boolean selected);

    void setTranslateObjectMenuVisible(boolean visible);

    void setTranslateObjectAxisSelected(Axis axis, boolean selected);

    void setRotateObjectSelected(boolean selected);

    void setRotateObjectMenuVisible(boolean visible);

    void setRotateObjectAxisSelected(Axis axis, boolean selected);

    void setScaleObjectSelected(boolean selected);
}
