package com.lakeel.altla.vision.builder.domain.repository;

import com.lakeel.altla.vision.builder.domain.model.TextureEntry;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.model.TextureReference;

import rx.Observable;
import rx.Single;

public interface TextureEntryRepository {

    Single<String> save(String id, String name, String fileId, TextureMetadata metadata);

    Observable<TextureEntry> findEntry(String id);

    Observable<TextureEntry> findAllEntries();

    Observable<TextureReference> findReference(String id);
}
