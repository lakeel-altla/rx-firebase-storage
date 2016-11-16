package com.lakeel.altla.vision.builder.domain.repository;

import java.io.InputStream;

import rx.Single;

public interface TextureRepository {

    Single<String> save(String directoryPath, String filename, InputStream stream, OnProgressListener onProgressListener);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
