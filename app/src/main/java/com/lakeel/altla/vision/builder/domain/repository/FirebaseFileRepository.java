package com.lakeel.altla.vision.builder.domain.repository;

import java.io.InputStream;

import rx.Single;

public interface FirebaseFileRepository {

    Single<String> save(String uuid, InputStream stream, OnProgressListener onProgressListener);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
