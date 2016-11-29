package com.lakeel.altla.vision.builder.domain.repository;

import com.lakeel.altla.vision.builder.domain.model.TextureFileMetadata;

import rx.Observable;

public interface TextureFileMetadataRepository {

    Observable<TextureFileMetadata> find(String fileId);
}
