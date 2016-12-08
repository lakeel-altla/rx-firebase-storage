package com.lakeel.altla.vision.domain.repository;

public final class ContentException extends RuntimeException {

    public ContentException(String message) {
        super(message);
    }

    public ContentException(Throwable cause) {
        super(cause);
    }
}
