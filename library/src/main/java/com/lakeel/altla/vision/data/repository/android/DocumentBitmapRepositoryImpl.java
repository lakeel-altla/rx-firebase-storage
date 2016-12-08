package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.ContentException;
import com.lakeel.altla.vision.domain.repository.DocumentBitmapRepository;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;

import rx.Single;

public final class DocumentBitmapRepositoryImpl implements DocumentBitmapRepository {

    private final ContentResolver contentResolver;

    public DocumentBitmapRepositoryImpl(ContentResolver contentResolver) {
        if (contentResolver == null) throw new ArgumentNullException("contentResolver");

        this.contentResolver = contentResolver;
    }

    @Override
    public Single<Bitmap> find(Uri uri) {
        return Single.create(subscriber -> {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try (ParcelFileDescriptor descriptor = contentResolver.openFileDescriptor(uri, "r")) {
                if (descriptor == null) {
                    throw new ContentException("Failed to open the file descriptor: uri = " + uri);
                }

                FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                if (bitmap != null) {
                    subscriber.onSuccess(bitmap);
                } else {
                    throw new ContentException("Failed to decode the file descriptor: uri = " + uri);
                }
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }
}
