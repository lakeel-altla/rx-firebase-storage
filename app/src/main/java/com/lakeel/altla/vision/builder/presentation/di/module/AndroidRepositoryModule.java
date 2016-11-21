package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.data.repository.android.DocumentRepositoryImpl;
import com.lakeel.altla.vision.builder.domain.repository.DocumentRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public DocumentRepository provideDocumentRepository(ContentResolver contentResolver) {
        return new DocumentRepositoryImpl(contentResolver);
    }
}
