package com.lakeel.altla.vision.builder.presentation.model;

public enum Axis {

    X(0),
    Y(1),
    Z(2);

    private final int mValue;

    Axis(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
