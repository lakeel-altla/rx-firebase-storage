package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentBitmapLoader;

import android.content.ContentResolver;

import dagger.Module;
import dagger.Provides;

@Module
public final class PresenterModule {

    @ActivityScope
    @Provides
    public DocumentBitmapLoader provideDocumentBitmapLoader(ContentResolver contentResolver) {
        return new DocumentBitmapLoader(contentResolver);
    }
}
