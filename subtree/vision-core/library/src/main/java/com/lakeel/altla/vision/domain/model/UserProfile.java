package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserProfile {

    public final String id;

    public String displayName;

    public String email;

    public String photoUri;

    public UserProfile(String id) {
        if (id == null) throw new ArgumentNullException("id");

        this.id = id;
    }
}
