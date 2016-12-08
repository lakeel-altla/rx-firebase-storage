package com.lakeel.altla.vision.builder.presentation.di.module;

import android.os.Environment;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ExternalStorageModule {

    @Named(Names.EXTERNAL_STORAGE_ROOT)
    @Singleton
    @Provides
    public File provideExternalStorageRoot() {
        return Environment.getExternalStorageDirectory();
    }
}
