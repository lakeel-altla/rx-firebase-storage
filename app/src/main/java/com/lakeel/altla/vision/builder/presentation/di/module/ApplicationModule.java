package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.app.MyApplication;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final MyApplication mApplication;

    public ApplicationModule(@NonNull MyApplication application) {
        mApplication = application;
    }

    @Named(Names.APPLICATION_CONTEXT)
    @Singleton
    @Provides
    public Context provideContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    public Resources provideResources() {
        return mApplication.getResources();
    }

    @Singleton
    @Provides
    public GoogleSignInOptions provideGoogleSignInOptions(Resources resources) {
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

    }
}
