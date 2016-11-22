package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;

public final class TextureModel {

    public final String id;

    public final String name;

    public Bitmap bitmap;

    public TextureModel(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
