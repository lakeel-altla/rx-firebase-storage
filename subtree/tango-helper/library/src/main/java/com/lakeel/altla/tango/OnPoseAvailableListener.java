package com.lakeel.altla.tango;

import com.google.atap.tangoservice.TangoPoseData;

public interface OnPoseAvailableListener {

    void onPoseAvailable(TangoPoseData pose);
}
