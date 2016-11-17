package com.lakeel.altla.vision.builder.domain.model;

import java.io.InputStream;

public final class TextureStorageEntry {

    public String uuid;

    public InputStream stream;

    public TextureStorageEntry(String uuid, InputStream stream) {
        this.uuid = uuid;
        this.stream = stream;
    }
}
