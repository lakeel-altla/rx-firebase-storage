package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseStorageModule {

    private static final String DIRECTORY_TEXTURES = "builder/textures";

    @Singleton
    @Provides
    public FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Named(Names.FIREBASE_STORAGE_REFERENCE_DIRECTORY_ROOT)
    @Singleton
    @Provides
    public StorageReference provideRootReference(
            FirebaseStorage storage,
            @Named(Names.FIREBASE_STORAGE_URI) String uri) {
        return storage.getReferenceFromUrl(uri);
    }

    @Named(Names.FIREBASE_STORAGE_REFERENCE_DIRECTORY_TEXTURES)
    @Singleton
    @Provides
    public StorageReference provideTexturesDirectoryReference(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_DIRECTORY_ROOT) StorageReference root) {
        return root.child(DIRECTORY_TEXTURES);
    }
}
