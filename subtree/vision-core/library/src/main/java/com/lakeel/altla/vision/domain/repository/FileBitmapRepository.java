package com.lakeel.altla.vision.domain.repository;

import android.graphics.Bitmap;

import java.io.File;

import rx.Single;

public interface FileBitmapRepository {

    Single<Bitmap> find(File file);
}
