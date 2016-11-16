package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseDatabaseModule {

    @Singleton
    @Provides
    public FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
}
