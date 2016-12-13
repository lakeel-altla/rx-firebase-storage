package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseStorageModule {

    @Singleton
    @Provides
    public FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT)
    @Singleton
    @Provides
    public StorageReference provideRootReference(FirebaseStorage storage,
                                                 @Named(Names.FIREBASE_STORAGE_URI) String uri) {
        return storage.getReferenceFromUrl(uri);
    }
}
