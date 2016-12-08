package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.domain.repository.ContentException;
import com.lakeel.altla.vision.domain.repository.DocumentFilenameRepository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import rx.Single;

public final class DocumentFilenameRepositoryImpl implements DocumentFilenameRepository {

    public final ContentResolver contentResolver;

    public DocumentFilenameRepositoryImpl(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public Single<String> find(Uri uri) {
        return Single.create(subscriber -> {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null) {
                int index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);

                String filename = null;
                try {
                    if (cursor.moveToFirst()) {
                        filename = cursor.getString(index);
                    }
                } finally {
                    cursor.close();
                }

                subscriber.onSuccess(filename);
            } else {
                subscriber.onError(new ContentException("Can not get a cursor to resolve the filename."));
            }
        });
    }
}
