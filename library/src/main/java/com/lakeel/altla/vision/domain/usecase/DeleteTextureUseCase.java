package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.domain.repository.TextureFileRepository;

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

        // Search TextureReference in advance.
        return textureEntryRepository.findReference(id)
                                     .toSingle()
                                     // Delete the texture data in Firebase Database.
                                     .flatMap(reference -> textureEntryRepository.delete(id)
                                                                                 .map(_id -> reference))
                                     // Delete the texture file in Firebase Storage.
                                     .flatMap(reference -> textureFileRepository.delete(reference.fileId)
                                                                                .map(_id -> reference))
                                     // Delete the local cache of the texture.
                                     .flatMap(reference -> textureCacheRepository.delete(reference.fileId))
                                     .map(fileId -> id)
                                     .subscribeOn(Schedulers.io());
    }
}
