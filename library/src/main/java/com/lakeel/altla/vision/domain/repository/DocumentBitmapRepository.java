package com.lakeel.altla.vision.domain.repository;

import android.graphics.Bitmap;
import android.net.Uri;

import rx.Single;

public interface DocumentBitmapRepository {

    Single<Bitmap> find(Uri uri);
}
