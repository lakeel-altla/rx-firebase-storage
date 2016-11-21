package com.lakeel.altla.tango;

import com.google.atap.tangoservice.Tango;

import android.content.Intent;

public final class TangoPermissions {

    private TangoPermissions() {
    }

    public static Intent getRequestAdfLoadSavePermissionIntent() {
        return Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_ADF_LOAD_SAVE);
    }
}
