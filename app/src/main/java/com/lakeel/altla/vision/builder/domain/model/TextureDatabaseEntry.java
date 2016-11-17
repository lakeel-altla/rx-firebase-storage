package com.lakeel.altla.vision.builder.domain.model;

public final class TextureDatabaseEntry {

    public String uuid;

    public Metadata metadata;

    public TextureDatabaseEntry() {
    }

    public TextureDatabaseEntry(String uuid, Metadata metadata) {
        this.uuid = uuid;
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "TextureDatabaseEntry{" +
               "uuid='" + uuid + '\'' +
               ", metadata=" + metadata +
               '}';
    }

    public static final class Metadata {

        public String filename;

        @Override
        public String toString() {
            return "Metadata{" +
                   "filename='" + filename + '\'' +
                   '}';
        }
    }
}
