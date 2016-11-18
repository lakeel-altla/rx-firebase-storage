package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;

public final class BitmapModel {

    public final String name;

    public final Bitmap bitmap;

    public BitmapModel(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }
}
