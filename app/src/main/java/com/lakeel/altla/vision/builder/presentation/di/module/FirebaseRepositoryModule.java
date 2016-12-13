package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.TextureEntryRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.TextureFileMetadataRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.TextureFileRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepositoryImpl;
import com.lakeel.altla.vision.di.ActivityScope;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.domain.repository.TextureFileMetadataRepository;
import com.lakeel.altla.vision.domain.repository.TextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public UserProfileRepository provideUserProfileRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserProfileRepositoryImpl(reference);
    }


    @ActivityScope
    @Provides
    public TextureEntryRepository provideTextureEntryRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new TextureEntryRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public TextureFileRepository provideTextureFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new TextureFileRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public TextureFileMetadataRepository provideTextureFileMetadataRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new TextureFileMetadataRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionRepository provideAreaDescriptionEntryRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserAreaDescriptionRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionFileRepository provideAreaDescriptionFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new UserAreaDescriptionFileRepositoryImpl(reference);
    }
}
