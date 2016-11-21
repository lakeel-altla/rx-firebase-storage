package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.data.repository.android.LocalDocumentRepositoryImpl;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public LocalDocumentRepository provideLocalDocumentRepository(ContentResolver contentResolver) {
        return new LocalDocumentRepositoryImpl(contentResolver);
    }
}
