package com.lakeel.altla.vision.builder.domain.repository;

import android.graphics.Bitmap;
import android.net.Uri;

import rx.Single;

public interface DocumentBitmapRepository {

    Single<Bitmap> find(Uri uri);
}
