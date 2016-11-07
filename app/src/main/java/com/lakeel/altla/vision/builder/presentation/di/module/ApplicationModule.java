package com.lakeel.altla.vision.builder.presentation.di.module;

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
}
