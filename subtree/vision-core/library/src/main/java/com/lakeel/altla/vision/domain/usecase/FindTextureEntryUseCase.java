package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.model.TextureEntry;
import com.lakeel.altla.vision.domain.repository.TextureEntryRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindTextureEntryUseCase {

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    public FindTextureEntryUseCase() {
    }

    public Observable<TextureEntry> execute(String id) {
        return textureEntryRepository.findEntry(id)
                                     .subscribeOn(Schedulers.io());
    }
}
