package com.lakeel.altla.vision.builder.presentation.helper;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import java.io.FileDescriptor;
import java.io.IOException;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DocumentBitmapLoader {

    public final ContentResolver contentResolver;

    public DocumentBitmapLoader(@NonNull ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @NonNull
    public Bitmap load(@NonNull Uri uri) throws IOException {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try (ParcelFileDescriptor descriptor = contentResolver.openFileDescriptor(uri, "r")) {
            if (descriptor == null) {
                throw new LoadFailedException("Opening file descriptor failed: uri = " + uri);
            }

            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            if (bitmap != null) {
                return bitmap;
            } else {
                throw new LoadFailedException("Decoding file descriptor failed: uri = " + uri);
            }
        }
    }

    @NonNull
    public Single<Bitmap> loadAsSingle(@NonNull Uri uri) {
        return Single.<Bitmap>create(subscriber -> {
            try {
                Bitmap bitmap = load(uri);
                subscriber.onSuccess(bitmap);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    public final class LoadFailedException extends RuntimeException {

        private LoadFailedException(String message) {
            super(message);
        }
    }
}
