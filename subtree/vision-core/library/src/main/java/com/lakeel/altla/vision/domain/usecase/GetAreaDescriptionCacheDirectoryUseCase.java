package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class GetAreaDescriptionCacheDirectoryUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public GetAreaDescriptionCacheDirectoryUseCase() {
    }

    public Single<File> execute() {
        return areaDescriptionCacheRepository.getDirectory()
                                             .subscribeOn(Schedulers.io());
    }
}
