package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class TextureReference {

    public final String id;

    public final String fileId;

    public TextureReference(String id, String fileId) {
        if (id == null) throw new ArgumentNullException("id");
        if (fileId == null) throw new ArgumentNullException("fileId");

        this.id = id;
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "TextureReference{" +
               "id='" + id + '\'' +
               ", fileId='" + fileId + '\'' +
               '}';
    }
}
