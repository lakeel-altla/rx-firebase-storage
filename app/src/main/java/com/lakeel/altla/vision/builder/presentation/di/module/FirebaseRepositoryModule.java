package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.data.repository.firebase.ConnectionRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserConnectionRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileMetadataRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepositoryImpl;
import com.lakeel.altla.vision.di.ActivityScope;
import com.lakeel.altla.vision.domain.repository.ConnectionRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.repository.UserConnectionRepository;
import com.lakeel.altla.vision.domain.repository.UserDeviceRepository;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public ConnectionRepository provideConnectionRepository(FirebaseDatabase database) {
        return new ConnectionRepositoryImpl(database);
    }

    @ActivityScope
    @Provides
    public UserConnectionRepository provideUserConnectionRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserConnectionRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserProfileRepository provideUserProfileRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserProfileRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserDeviceRepository provideUserDeviceRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserDeviceRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserTextureRepository provideTextureEntryRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserTextureRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserTextureFileRepository provideTextureFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new UserTextureFileRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserTextureFileMetadataRepository provideTextureFileMetadataRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new UserTextureFileMetadataRepositoryImpl(reference);
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
