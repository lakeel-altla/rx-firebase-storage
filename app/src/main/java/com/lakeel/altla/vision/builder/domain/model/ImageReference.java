package com.lakeel.altla.vision.builder.domain.model;

import java.util.Date;

public final class ImageReference {

    public final String uri;

    public final Date createDate;

    public ImageReference(String uri) {
        this(uri, new Date());
    }

    public ImageReference(String uri, Date createDate) {
        this.uri = uri;
        this.createDate = createDate;
    }
}
