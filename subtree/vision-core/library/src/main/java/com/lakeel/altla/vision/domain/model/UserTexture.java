package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserTexture {

    public final String id;

    public final String name;

    public UserTexture(String id, String name) {
        if (id == null) throw new ArgumentNullException("id");
        if (name == null) throw new ArgumentNullException("name");

        this.id = id;
        this.name = name;
    }
}
