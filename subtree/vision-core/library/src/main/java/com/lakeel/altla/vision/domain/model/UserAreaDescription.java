package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserAreaDescription {

    public final String id;

    public final String name;

    public final long creationTime;

    public boolean synced;

    public UserAreaDescription(String id, String name, long creationTime) {
        if (id == null) throw new ArgumentNullException("id");
        if (name == null) throw new ArgumentNullException("name");

        this.id = id;
        this.name = name;
        this.creationTime = creationTime;
    }
}
