package com.lakeel.altla.vision.builder.presentation.helper;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class DocumentFilenameLoader {

    private static final Log LOG = LogFactory.getLog(DocumentFilenameLoader.class);

    public final ContentResolver contentResolver;

    public DocumentFilenameLoader(@NonNull ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Nullable
    public String load(@NonNull Uri uri) {
        LOG.d("Resolving the filename of the document: uri = %s", uri);

        String filename = null;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            int index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
            try {
                if (cursor.moveToFirst()) {
                    filename = cursor.getString(index);
                    LOG.d("Resolved the filename of the document: filename = %s", filename);
                }
            } finally {
                cursor.close();
            }
        } else {
            LOG.w("Can not get a cursor to resolve the filename.");
        }

        return filename;
    }
}
