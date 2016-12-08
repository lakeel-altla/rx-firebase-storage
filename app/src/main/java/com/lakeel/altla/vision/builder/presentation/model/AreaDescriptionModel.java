package com.lakeel.altla.vision.builder.presentation.model;

import android.support.annotation.NonNull;

public final class AreaDescriptionModel {

    public final String id;

    public final String name;

    public final boolean synced;

    public AreaDescriptionModel(@NonNull String id, @NonNull String name, boolean synced) {
        this.id = id;
        this.name = name;
        this.synced = synced;
    }
}
