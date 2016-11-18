package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.domain.model.TextureEntry;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllTextureEntriesUseCase {

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    public FindAllTextureEntriesUseCase() {
    }

    public Observable<TextureEntry> execute() {
        return textureEntryRepository.findAllEntries()
                                     .subscribeOn(Schedulers.io());
    }
}
