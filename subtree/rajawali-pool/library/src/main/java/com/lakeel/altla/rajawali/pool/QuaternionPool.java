package com.lakeel.altla.rajawali.pool;

import org.rajawali3d.math.Quaternion;

/**
 * Defines the pool that manages Quaternion instances per thread.
 */
public final class QuaternionPool {

    private static final ThreadLocal<Pool<Quaternion>> THREAD_LOCAL = new ThreadLocal<Pool<Quaternion>>() {
        @Override
        protected Pool<Quaternion> initialValue() {
            return new Pool<>(new Pool.Factory<Quaternion>() {
                @Override
                public Quaternion create() {
                    return new Quaternion();
                }
            }, new Pool.Recycler<Quaternion>() {
                @Override
                public void recycle(Quaternion object) {
                    object.setAll(0, 0, 0, 0);
                }
            });
        }
    };

    private QuaternionPool() {
    }

    public static Pool<Quaternion> getPool() {
        return THREAD_LOCAL.get();
    }

    public static Pool.Holder<Quaternion> get() {
        return getPool().get();
    }
}
