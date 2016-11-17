package com.lakeel.altla.vision.builder.domain.repository;

import java.io.InputStream;

import rx.Single;

public interface TextureFileRepository {

    Single<String> save(String fileId, InputStream stream, OnProgressListener onProgressListener);

    Single<String> delete(String fileId);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
