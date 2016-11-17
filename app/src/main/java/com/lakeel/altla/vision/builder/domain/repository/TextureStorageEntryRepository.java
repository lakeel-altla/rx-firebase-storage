package com.lakeel.altla.vision.builder.domain.repository;

import com.lakeel.altla.vision.builder.domain.model.TextureStorageEntry;

import rx.Single;

public interface TextureStorageEntryRepository {

    Single<TextureStorageEntry> save(TextureStorageEntry entry, OnProgressListener onProgressListener);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
