package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    public DeleteTextureUseCase() {
    }

    public Single<String> execute(String id) {
        if (id == null) throw new ArgumentNullException("id");

        // Delete the user texture in Firebase Database.
        return userTextureRepository.delete(id)
                                    // Delete the user texture file in Firebase Storage.
                                    .flatMap(_id -> userTextureFileRepository.delete(id))
                                    // Delete the local cache of the user texture.
                                    .flatMap(_id -> textureCacheRepository.delete(id))
                                    .subscribeOn(Schedulers.io());
    }
}
