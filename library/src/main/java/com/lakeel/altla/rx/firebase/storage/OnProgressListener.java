package com.lakeel.altla.rx.firebase.storage;

public interface OnProgressListener {

    void onProgress(long totalBytes, long bytesTransferred);
}
