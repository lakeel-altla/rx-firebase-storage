package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class AreaDescription {

    public final String id;

    public final String name;

    public final boolean synced;

    public AreaDescription(String id, String name, boolean synced) {
        if (id == null) throw new ArgumentNullException("id");
        if (name == null) throw new ArgumentNullException("name");

        this.id = id;
        this.name = name;
        this.synced = synced;
    }
}
