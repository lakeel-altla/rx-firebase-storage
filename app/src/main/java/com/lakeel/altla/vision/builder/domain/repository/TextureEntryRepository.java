package com.lakeel.altla.vision.builder.domain.repository;

import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;

import rx.Observable;
import rx.Single;

public interface TextureEntryRepository {

    Single<String> save(String entryId, String fileId, TextureMetadata metadata);

    Observable<String> findFileId(String entryId);
}
