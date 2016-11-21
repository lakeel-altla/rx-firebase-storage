package com.lakeel.altla.tango;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

import java.util.LinkedList;
import java.util.List;

public final class TangoUpdateDispatcher implements Tango.OnTangoUpdateListener {

    private final Listeners<OnPoseAvailableListener> mOnPoseAvailableListeners = new Listeners<>();

    private final Listeners<OnPointCloudAvailableListener> mOnPointCloudAvailableListeners = new Listeners<>();

    private final Listeners<OnFrameAvailableListener> mOnFrameAvailableListeners = new Listeners<>();

    private final Listeners<OnTangoEventListener> mOnTangoEventListeners = new Listeners<>();

    @Override
    public void onPoseAvailable(TangoPoseData pose) {
        synchronized (mOnPoseAvailableListeners) {
            for (OnPoseAvailableListener listener : mOnPoseAvailableListeners.mListeners) {
                listener.onPoseAvailable(pose);
            }
        }
    }

    @Override
    public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
        synchronized (mOnPointCloudAvailableListeners) {
            for (OnPointCloudAvailableListener listener : mOnPointCloudAvailableListeners.mListeners) {
                listener.onPointCloudAvailable(pointCloud);
            }
        }
    }

    @Deprecated
    @Override
    public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        synchronized (mOnFrameAvailableListeners) {
            for (OnFrameAvailableListener listener : mOnFrameAvailableListeners.mListeners) {
                listener.onFrameAvailable(cameraId);
            }
        }
    }

    @Override
    public void onTangoEvent(TangoEvent event) {
        synchronized (mOnTangoEventListeners) {
            for (OnTangoEventListener listener : mOnTangoEventListeners.mListeners) {
                listener.onTangoEvent(event);
            }
        }
    }

    public Listeners<OnPoseAvailableListener> getOnPoseAvailableListeners() {
        return mOnPoseAvailableListeners;
    }

    public Listeners<OnPointCloudAvailableListener> getOnPointCloudAvailableListeners() {
        return mOnPointCloudAvailableListeners;
    }

    public Listeners<OnFrameAvailableListener> getOnFrameAvailableListeners() {
        return mOnFrameAvailableListeners;
    }

    public Listeners<OnTangoEventListener> getOnTangoEventListeners() {
        return mOnTangoEventListeners;
    }

    public class Listeners<T> {

        private final List<T> mListeners = new LinkedList<>();

        private Listeners() {
        }

        public synchronized void add(T listener) {
            mListeners.add(listener);
        }

        public synchronized void remove(T listener) {
            mListeners.remove(listener);
        }

        public synchronized void clear() {
            mListeners.clear();
        }
    }
}
