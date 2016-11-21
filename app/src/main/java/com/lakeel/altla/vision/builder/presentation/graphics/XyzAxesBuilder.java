package com.lakeel.altla.vision.builder.presentation.graphics;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import android.graphics.Color;

import java.util.Collections;
import java.util.Stack;

public final class XyzAxesBuilder {

    private float thickness = 1;

    private float lengthX = 1;

    private float lengthY = 1;

    private float lengthZ = 1;

    private int colorX = Color.RED;

    private int colorY = Color.GREEN;

    private int colorZ = Color.BLUE;

    public XyzAxesBuilder setThickness(float value) {
        thickness = value;
        return this;
    }

    public XyzAxesBuilder setLength(float value) {
        lengthX = value;
        lengthY = value;
        lengthZ = value;
        return this;
    }

    public XyzAxesBuilder setLengthX(float value) {
        lengthX = value;
        return this;
    }

    public XyzAxesBuilder setLengthY(float value) {
        lengthY = value;
        return this;
    }

    public XyzAxesBuilder setLengthZ(float value) {
        lengthZ = value;
        return this;
    }

    public XyzAxesBuilder setColor(int value) {
        colorX = value;
        colorY = value;
        colorZ = value;
        return this;
    }

    public XyzAxesBuilder setColorX(int value) {
        colorX = value;
        return this;
    }

    public XyzAxesBuilder setColorY(int value) {
        colorY = value;
        return this;
    }

    public XyzAxesBuilder setColorZ(int value) {
        colorZ = value;
        return this;
    }

    public Line3D build() {
        Vector3 o = new Vector3(0, 0, 0);
        Vector3 x = new Vector3(lengthX, 0, 0);
        Vector3 y = new Vector3(0, lengthY, 0);
        Vector3 z = new Vector3(0, 0, lengthZ);

        Stack<Vector3> points = new Stack<>();
        Collections.addAll(points, o, x, o, y, o, z);

        int[] colors = new int[6];
        colors[0] = colorX;
        colors[1] = colorX;
        colors[2] = colorY;
        colors[3] = colorY;
        colors[4] = colorZ;
        colors[5] = colorZ;

        Material material = new Material();
        material.useVertexColors(true);

        Line3D line3D = new Line3D(points, thickness, colors);
        line3D.setMaterial(material);

        return line3D;
    }
}
