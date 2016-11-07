package com.lakeel.altla.vision.builder.presentation.graphics;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import android.graphics.Color;

import java.util.Collections;
import java.util.Stack;

public final class XyzAxesBuilder {

    private float mThickness = 1;

    private float mLengthX = 1;

    private float mLengthY = 1;

    private float mLengthZ = 1;

    private int mColorX = Color.RED;

    private int mColorY = Color.GREEN;

    private int mColorZ = Color.BLUE;

    public XyzAxesBuilder setThickness(float value) {
        mThickness = value;
        return this;
    }

    public XyzAxesBuilder setLength(float value) {
        mLengthX = value;
        mLengthY = value;
        mLengthZ = value;
        return this;
    }

    public XyzAxesBuilder setLengthX(float value) {
        mLengthX = value;
        return this;
    }

    public XyzAxesBuilder setLengthY(float value) {
        mLengthY = value;
        return this;
    }

    public XyzAxesBuilder setLengthZ(float value) {
        mLengthZ = value;
        return this;
    }

    public XyzAxesBuilder setColor(int value) {
        mColorX = value;
        mColorY = value;
        mColorZ = value;
        return this;
    }

    public XyzAxesBuilder setColorX(int value) {
        mColorX = value;
        return this;
    }

    public XyzAxesBuilder setColorY(int value) {
        mColorY = value;
        return this;
    }

    public XyzAxesBuilder setColorZ(int value) {
        mColorZ = value;
        return this;
    }

    public Line3D build() {
        Vector3 o = new Vector3(0, 0, 0);
        Vector3 x = new Vector3(mLengthX, 0, 0);
        Vector3 y = new Vector3(0, mLengthY, 0);
        Vector3 z = new Vector3(0, 0, mLengthZ);

        Stack<Vector3> points = new Stack<>();
        Collections.addAll(points, o, x, o, y, o, z);

        int[] colors = new int[6];
        colors[0] = mColorX;
        colors[1] = mColorX;
        colors[2] = mColorY;
        colors[3] = mColorY;
        colors[4] = mColorZ;
        colors[5] = mColorZ;

        Material material = new Material();
        material.useVertexColors(true);

        Line3D line3D = new Line3D(points, mThickness, colors);
        line3D.setMaterial(material);

        return line3D;
    }
}
