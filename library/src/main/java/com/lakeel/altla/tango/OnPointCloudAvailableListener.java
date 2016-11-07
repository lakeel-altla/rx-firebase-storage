package com.lakeel.altla.tango;

import com.google.atap.tangoservice.TangoPointCloudData;

public interface OnPointCloudAvailableListener {

    void onPointCloudAvailable(TangoPointCloudData pointCloud);
}
