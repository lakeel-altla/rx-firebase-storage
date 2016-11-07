package com.lakeel.altla.tango;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

public final class TangoUpdateDelegator implements Tango.OnTangoUpdateListener {

    private OnPoseAvailableListener mOnPoseAvailableListener;

    private OnPointCloudAvailableListener mOnPointCloudAvailableListener;

    private OnFrameAvailableListener mOnFrameAvailableListener;

    private OnTangoEventListener mOnTangoEventListener;

    @Override
    public void onPoseAvailable(TangoPoseData pose) {
        if (mOnPoseAvailableListener != null) {
            mOnPoseAvailableListener.onPoseAvailable(pose);
        }
    }

    @Override
    public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
        if (mOnPointCloudAvailableListener != null) {
            mOnPointCloudAvailableListener.onPointCloudAvailable(pointCloud);
        }
    }

    @Deprecated
    @Override
    public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        if (mOnFrameAvailableListener != null) {
            mOnFrameAvailableListener.onFrameAvailable(cameraId);
        }
    }

    @Override
    public void onTangoEvent(TangoEvent event) {
        if (mOnTangoEventListener != null) {
            mOnTangoEventListener.onTangoEvent(event);
        }
    }

    public OnPoseAvailableListener getOnPoseAvailableListener() {
        return mOnPoseAvailableListener;
    }

    public void setOnPoseAvailableListener(OnPoseAvailableListener listener) {
        mOnPoseAvailableListener = listener;
    }

    public OnPointCloudAvailableListener getOnPointCloudAvailableListener() {
        return mOnPointCloudAvailableListener;
    }

    public void setOnPointCloudAvailableListener(
            OnPointCloudAvailableListener onPointCloudAvailableListener) {
        mOnPointCloudAvailableListener = onPointCloudAvailableListener;
    }

    public OnFrameAvailableListener getOnFrameAvailableListener() {
        return mOnFrameAvailableListener;
    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {
        mOnFrameAvailableListener = listener;
    }

    public OnTangoEventListener getOnTangoEventListener() {
        return mOnTangoEventListener;
    }

    public void setOnTangoEventListener(OnTangoEventListener listener) {
        mOnTangoEventListener = listener;
    }

    public void clear() {
        mOnPoseAvailableListener = null;
        mOnPointCloudAvailableListener = null;
        mOnFrameAvailableListener = null;
        mOnTangoEventListener = null;
    }
}
