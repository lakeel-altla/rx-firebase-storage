package com.lakeel.altla.tango;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;

import android.support.annotation.NonNull;

public final class TangoUxListener implements OnPoseAvailableListener, OnPointCloudAvailableListener,
                                              OnTangoEventListener {

    private final TangoUx mTangoUx;

    public TangoUxListener(@NonNull TangoUx tangoUx) {
        mTangoUx = tangoUx;
    }

    @Override
    public void onPoseAvailable(TangoPoseData pose) {
        mTangoUx.updatePoseStatus(pose.statusCode);
    }

    @Override
    public void onTangoEvent(TangoEvent event) {
        mTangoUx.updateTangoEvent(event);
    }

    @Override
    public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
        mTangoUx.updateXyzCount(pointCloud.numPoints);
    }
}
