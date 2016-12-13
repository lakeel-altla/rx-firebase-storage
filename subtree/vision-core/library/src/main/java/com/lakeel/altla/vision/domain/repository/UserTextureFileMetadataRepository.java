package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.TextureFileMetadata;

import rx.Observable;

public interface UserTextureFileMetadataRepository {

    Observable<TextureFileMetadata> find(String id);
}
