package com.lakeel.altla.vision.builder.presentation.model;

import android.support.annotation.NonNull;

public final class AreaDescriptionModel {

    public final String id;

    public final String name;

    public boolean synced;

    public AreaDescriptionModel(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
}
