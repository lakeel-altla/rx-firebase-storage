package com.lakeel.altla.vision.builder.presentation.model;

public enum ObjectEditMode {

    NONE(0),
    TRANSLATE(1),
    ROTATE(2),
    SCALE(3);

    private final int mValue;

    ObjectEditMode(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
