package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteTextureUseCase {

    @Inject
    TextureEntryRepository textureEntryRepository;

    @Inject
    TextureFileRepository textureFileRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    public DeleteTextureUseCase() {
    }

    public Single<String> execute(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return textureEntryRepository.findReference(id)
                                     .toSingle()
                                     .flatMap(reference -> textureEntryRepository.delete(id)
                                                                                 .map(_id -> reference))
                                     .flatMap(reference -> textureFileRepository.delete(reference.fileId)
                                                                                .map(_id -> reference))
                                     .flatMap(reference -> textureCacheRepository.delete(reference.fileId))
                                     .map(fileId -> id)
                                     .subscribeOn(Schedulers.io());
    }
}
