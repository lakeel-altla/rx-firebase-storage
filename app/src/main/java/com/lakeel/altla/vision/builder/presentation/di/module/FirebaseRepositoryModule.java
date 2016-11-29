package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.builder.data.repository.firebase.TextureEntryRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.firebase.TextureFileMetadataRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.firebase.TextureFileRepositoryImpl;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileMetadataRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public TextureEntryRepository provideTextureEntryRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_APP_ROOT) DatabaseReference reference, FirebaseAuth auth) {
        return new TextureEntryRepositoryImpl(reference, auth);
    }

    @ActivityScope
    @Provides
    public TextureFileRepository provideTextureFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_APP_ROOT) StorageReference reference, FirebaseAuth auth) {
        return new TextureFileRepositoryImpl(reference, auth);
    }

    @ActivityScope
    @Provides
    public TextureFileMetadataRepository provideTextureFileMetadataRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_APP_ROOT) StorageReference reference, FirebaseAuth auth) {
        return new TextureFileMetadataRepositoryImpl(reference, auth);
    }
}
