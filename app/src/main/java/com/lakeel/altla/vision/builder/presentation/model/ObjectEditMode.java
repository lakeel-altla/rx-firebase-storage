package com.lakeel.altla.vision.builder.presentation.model;

public enum ObjectEditMode {

    NONE(0),
    TRANSLATE(1),
    ROTATE(2),
    SCALE(3);

    private final int value;

    ObjectEditMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
