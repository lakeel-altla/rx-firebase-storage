package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserConnection {

    public final String userId;

    public final String instanceId;

    public UserConnection(String userId, String instanceId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (instanceId == null) throw new ArgumentNullException("instanceId");

        this.userId = userId;
        this.instanceId = instanceId;
    }
}
