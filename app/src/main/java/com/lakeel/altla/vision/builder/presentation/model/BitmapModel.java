package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;
import android.net.Uri;

public final class BitmapModel {

    public final Uri uri;

    public final Bitmap bitmap;

    public BitmapModel(Uri uri, Bitmap bitmap) {
        this.uri = uri;
        this.bitmap = bitmap;
    }
}
