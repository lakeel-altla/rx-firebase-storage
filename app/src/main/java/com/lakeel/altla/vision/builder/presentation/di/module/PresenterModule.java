package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentBitmapLoader;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentFilenameLoader;

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

    @ActivityScope
    @Provides
    public DocumentFilenameLoader provideDocumentFilenameLoader(ContentResolver contentResolver) {
        return new DocumentFilenameLoader(contentResolver);
    }
}
