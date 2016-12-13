package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Named;
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

    @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT)
    @Singleton
    @Provides
    public DatabaseReference provideRootReference(FirebaseDatabase database) {
        return database.getReference();
    }
}
