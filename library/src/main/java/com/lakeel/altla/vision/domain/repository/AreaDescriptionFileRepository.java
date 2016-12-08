package com.lakeel.altla.vision.domain.repository;

import java.io.File;
import java.io.InputStream;

import rx.Single;

public interface AreaDescriptionFileRepository {

    Single<String> upload(String id, InputStream stream, OnProgressListener onProgressListener);

    Single<String> download(String id, File destination, OnProgressListener onProgressListener);

    Single<String> delete(String id);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
