package com.lakeel.altla.tango;

import com.google.atap.tangoservice.Tango;

import android.content.Intent;
import android.support.annotation.NonNull;

public final class TangoIntents {

    private static final String PACKAGE_NAME = "com.google.tango";

    private static final String CLASS_NAME = "com.google.atap.tango.RequestImportExportActivity";

    private static final String EXTRA_KEY_SOURCE_FILE = "SOURCE_FILE";

    private static final String EXTRA_KEY_SOURCE_UUID = "SOURCE_UUID";

    private static final String EXTRA_KEY_DESTINATION_FILE = "DESTINATION_FILE";

    private TangoIntents() {
    }

    @NonNull
    public static Intent createAdfLoadSaveRequestPermissionIntent() {
        return Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_ADF_LOAD_SAVE);
    }

    @NonNull
    public static Intent createAdfImportIntent(@NonNull String path) {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, CLASS_NAME);
        intent.putExtra(EXTRA_KEY_SOURCE_FILE, path);
        return intent;
    }

    @NonNull
    public static Intent createAdfExportIntent(@NonNull String uuid, @NonNull String destinationPath) {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, CLASS_NAME);
        intent.putExtra(EXTRA_KEY_SOURCE_UUID, uuid);
        intent.putExtra(EXTRA_KEY_DESTINATION_FILE, destinationPath);
        return intent;
    }
}
