package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.R;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigModule {

    private static final String FIREBASE_STORAGE_URI = "gs://firebase-trial.appspot.com";

    @Named(Names.FIREBASE_STORAGE_URI)
    @Singleton
    @Provides
    public String provideFirebaseStorageUri() {
        return FIREBASE_STORAGE_URI;
    }

    @Named(Names.GOOGLE_SIGN_IN_WEB_CLIENT_ID)
    @Singleton
    @Provides
    public int provideGoogleSignInWebClientId() {
        return R.string.default_web_client_id;
    }
}
