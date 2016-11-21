package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;

public final class TextureModel {

    public final String name;

    public final Bitmap bitmap;

    public TextureModel(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }
}
