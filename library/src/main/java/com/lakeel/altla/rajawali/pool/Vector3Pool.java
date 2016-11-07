package com.lakeel.altla.rajawali.pool;

import org.rajawali3d.math.vector.Vector3;

/**
 * Defines the pool that manages Vector3 instances per thread.
 */
public final class Vector3Pool {

    private static final ThreadLocal<Pool<Vector3>> THREAD_LOCAL = new ThreadLocal<Pool<Vector3>>() {
        @Override
        protected Pool<Vector3> initialValue() {
            return new Pool<>(new Pool.Factory<Vector3>() {
                @Override
                public Vector3 create() {
                    return new Vector3();
                }
            }, new Pool.Recycler<Vector3>() {
                @Override
                public void recycle(Vector3 object) {
                    object.setAll(0, 0, 0);
                }
            });
        }
    };

    private Vector3Pool() {
    }

    public static Pool<Vector3> getPool() {
        return THREAD_LOCAL.get();
    }

    public static Pool.Holder<Vector3> get() {
        return getPool().get();
    }
}
