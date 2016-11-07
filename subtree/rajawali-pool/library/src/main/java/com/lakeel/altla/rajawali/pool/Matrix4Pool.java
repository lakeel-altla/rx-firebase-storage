package com.lakeel.altla.rajawali.pool;

import org.rajawali3d.math.Matrix4;

/**
 * Defines the pool that manages Matrix instances per thread.
 */
public final class Matrix4Pool {

    private static final ThreadLocal<Pool<Matrix4>> THREAD_LOCAL = new ThreadLocal<Pool<Matrix4>>() {
        @Override
        protected Pool<Matrix4> initialValue() {
            return new Pool<>(new Pool.Factory<Matrix4>() {
                @Override
                public Matrix4 create() {
                    return new Matrix4();
                }
            }, new Pool.Recycler<Matrix4>() {
                @Override
                public void recycle(Matrix4 object) {
                    object.zero();
                }
            });
        }
    };

    private Matrix4Pool() {
    }

    public static Pool<Matrix4> getPool() {
        return THREAD_LOCAL.get();
    }

    public static Pool.Holder<Matrix4> get() {
        return getPool().get();
    }
}
