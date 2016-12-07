package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import android.content.res.Resources;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class GoogleSignInModule {

    @Singleton
    @Provides
    public GoogleSignInOptions provideGoogleSignInOptions(
            Resources resources, @Named(Names.GOOGLE_SIGN_IN_WEB_CLIENT_ID) int webClientId) {
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(webClientId))
                .requestEmail()
                .requestProfile()
                .build();

    }
}
