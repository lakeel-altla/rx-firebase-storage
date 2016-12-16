package com.lakeel.altla.rx.firebase.storage;

/**
 * The listener that called when the progress changes.
 */
public interface OnProgressListener {

    /**
     * Called when the progress changes.
     *
     * @param totalBytes       The total bumber of bytes.
     * @param bytesTransferred The number of bytes transferred.
     */
    void onProgress(long totalBytes, long bytesTransferred);
}
