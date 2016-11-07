package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.data.repository.RealmImageReferenceRepository;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

    @ActivityScope
    @Provides
    public ImageReferenceRepository provideImageReferenceRepository() {
        return new RealmImageReferenceRepository();
    }
}
