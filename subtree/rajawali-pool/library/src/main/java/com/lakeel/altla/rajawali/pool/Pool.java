package com.lakeel.altla.rajawali.pool;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Defines the object pool.
 *
 * @param <T> The type of objects pooled.
 */
public final class Pool<T> {

    private final Factory<T> factory;

    private final Recycler<T> recycler;

    private final Deque<Holder<T>> deque = new ArrayDeque<>();

    private int activeObjectCount;

    /**
     * Initializes this instance.
     *
     * @param factory  The factory that creates objects pooled by this instance.
     * @param recycler The recycler that recycles objects pooled by this instance.
     */
    public Pool(Factory<T> factory, Recycler<T> recycler) {
        if (factory == null) throw new IllegalArgumentException("'factory' must be not null.");
        if (recycler == null) throw new IllegalArgumentException("'recycler' must be not null.");

        this.factory = factory;
        this.recycler = recycler;
    }

    /**
     * Gets the holder that contains an object pooled.
     *
     * @return The holder that contains an object pooled.
     */
    public Holder<T> get() {
        Holder<T> holder = deque.poll();
        if (holder == null) {
            T object = factory.create();
            if (object == null) throw new IllegalStateException("The factory must not return null.");

            holder = new Holder<>(this, object);
        }

        holder.active = true;
        activeObjectCount++;

        return holder;
    }

    /**
     * Recycles the holder.
     *
     * @param holder The holder that is recycled.
     */
    public void recycle(Holder<T> holder) {
        if (holder == null) throw new IllegalArgumentException("'holder' must be not null.");

        holder.active = false;
        recycler.recycle(holder.get());
        deque.push(holder);
        activeObjectCount--;
    }

    /**
     * Gets the number of active holders.
     *
     * @return The number of active holders.
     */
    public int getActiveObjectCount() {
        return activeObjectCount;
    }

    /**
     * Gets the number of passive holders.
     *
     * @return The number of passive holders.
     */
    public int getPassiveObjectCount() {
        return deque.size();
    }

    /**
     * Defines the holder that contains objects pooled by this instance.
     *
     * @param <T> The type of an object that is hold by the holder.
     */
    public static final class Holder<T> implements AutoCloseable {

        private final Pool<T> pool;

        private final T object;

        private boolean active;

        Holder(Pool<T> pool, T object) {
            this.pool = pool;
            this.object = object;
        }

        /**
         * Recycles this instance.
         */
        @Override
        public void close() {
            if (!active) throw new IllegalStateException("This holder is already closed.");

            pool.recycle(this);
        }

        /**
         * Gets the object as an entity.
         *
         * @return The object as an entity.
         */
        public T get() {
            return object;
        }
    }

    /**
     * Defines the factory that creates objects managed by {@link Pool}.
     *
     * @param <T> The type of an object that is managed by {@link Pool}.
     */
    public interface Factory<T> {

        T create();
    }

    /**
     * Defines the recycler that recycles objects managed by {@link Pool}.
     *
     * @param <T> The type of an object that is managed by {@link Pool}.
     */
    public interface Recycler<T> {

        void recycle(T object);
    }
}
