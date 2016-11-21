package com.lakeel.altla.vision.builder.data.repository.android;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.repository.ContentException;
import com.lakeel.altla.vision.builder.domain.repository.FileBitmapRepository;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import rx.Single;

public final class FileBitmapRepositoryImpl implements FileBitmapRepository {

    private final ContentResolver contentResolver;

    public FileBitmapRepositoryImpl(ContentResolver contentResolver) {
        if (contentResolver == null) throw new ArgumentNullException("contentResolver");

        this.contentResolver = contentResolver;
    }

    @Override
    public Single<Bitmap> find(File file) {
        return Single.create(subscriber -> {
            try (FileInputStream stream = new FileInputStream(file)) {
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                if (bitmap != null) {
                    subscriber.onSuccess(bitmap);
                } else {
                    throw new ContentException("Failed to decode the file stream: file = " + file);
                }
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }
}
