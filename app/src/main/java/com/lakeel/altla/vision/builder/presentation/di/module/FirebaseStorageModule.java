package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseStorageModule {

    private static final String FILES_DIRECTORY = "builder/files";

    @Singleton
    @Provides
    public FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT)
    @Singleton
    @Provides
    public StorageReference provideRootReference(
            FirebaseStorage storage,
            @Named(Names.FIREBASE_STORAGE_URI) String uri) {
        return storage.getReferenceFromUrl(uri);
    }

    @Named(Names.FIREBASE_STORAGE_REFERENCE_FILES_DIRECTORY)
    @Singleton
    @Provides
    public StorageReference provideFilesDirectoryReference(
            FirebaseStorage storage,
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference root) {
        return root.child(FILES_DIRECTORY);
    }
}
