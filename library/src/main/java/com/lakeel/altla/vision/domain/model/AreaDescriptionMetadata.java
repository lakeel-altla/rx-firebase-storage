package com.lakeel.altla.vision.domain.model;

public final class AreaDescriptionMetadata {

    public long creationTime;

    public Vector3 position;

    public Quaternion rotation;

    public static final class Vector3 {

        public double x;

        public double y;

        public double z;

        public Vector3() {
        }

        public Vector3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static final class Quaternion {

        public double x;

        public double y;

        public double z;

        public double w;

        public Quaternion() {
        }

        public Quaternion(double x, double y, double z, double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }
}
