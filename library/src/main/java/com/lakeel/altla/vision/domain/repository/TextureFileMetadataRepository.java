package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.TextureFileMetadata;

import rx.Observable;

public interface TextureFileMetadataRepository {

    Observable<TextureFileMetadata> find(String fileId);
}
