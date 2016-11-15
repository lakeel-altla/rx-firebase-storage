package com.lakeel.altla.vision.builder.presentation.di.module;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigModule {

    private static final String FIREBASE_STORAGE_URI = "gs://firebase-trial.appspot.com";

    private static final String FIREBASE_STORAGE_PATH_FILES = "builder/files";

    @Named(Names.FIREBASE_STORAGE_URI)
    @Singleton
    @Provides
    public String provideFirebaseStorageUri() {
        return FIREBASE_STORAGE_URI;
    }

    @Named(Names.FIREBASE_STORAGE_PATH_FILES)
    @Singleton
    @Provides
    public String provideFirebaseStoragePathFiles() {
        return FIREBASE_STORAGE_PATH_FILES;
    }
}
