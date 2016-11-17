package com.lakeel.altla.vision.builder.domain.repository;

import com.lakeel.altla.vision.builder.domain.model.TextureDatabaseEntry;

import rx.Observable;
import rx.Single;

public interface TextureDatabaseEntryRepository {

    Single<TextureDatabaseEntry> save(TextureDatabaseEntry entry);

    Observable<TextureDatabaseEntry> findByFilename(String filename);
}
