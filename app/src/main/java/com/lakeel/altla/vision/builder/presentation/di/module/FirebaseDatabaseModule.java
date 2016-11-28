package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseDatabaseModule {

    private static final String PATH_APP_ROOT = "builder";

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

    @Named(Names.FIREBASE_DATABASE_REFERENCE_APP_ROOT)
    @Singleton
    @Provides
    public DatabaseReference provideAppRootReference(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference root) {
        return root.child(PATH_APP_ROOT);
    }
}
