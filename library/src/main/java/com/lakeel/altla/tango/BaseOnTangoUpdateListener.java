package com.lakeel.altla.tango;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

public class BaseOnTangoUpdateListener implements Tango.OnTangoUpdateListener {

    protected BaseOnTangoUpdateListener() {
    }

    @Override
    public void onPoseAvailable(TangoPoseData pose) {
    }

    @Override
    public void onPointCloudAvailable(TangoPointCloudData tangoPointCloudData) {

    }

    @Deprecated
    @Override
    public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
    }

    @Override
    public void onFrameAvailable(int cameraId) {
    }

    @Override
    public void onTangoEvent(TangoEvent event) {
    }
}
