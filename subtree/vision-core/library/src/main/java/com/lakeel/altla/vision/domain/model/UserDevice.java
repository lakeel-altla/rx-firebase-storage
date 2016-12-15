package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

import android.os.Build;

public final class UserDevice {

    public final String userId;

    public final String instanceId;

    public final long creationTime;

    public final String osName = "android";

    public final String osModel = Build.MODEL;

    public final String osVersion = Build.VERSION.RELEASE;

    public UserDevice(String userId, String instanceId, long creationTime) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (instanceId == null) throw new ArgumentNullException("instanceId");

        this.userId = userId;
        this.instanceId = instanceId;
        this.creationTime = creationTime;
    }
}
