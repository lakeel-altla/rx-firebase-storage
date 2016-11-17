package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.builder.data.repository.LocalDocumentRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.RealmImageReferenceRepository;
import com.lakeel.altla.vision.builder.data.repository.TextureDatabaseEntryRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.TextureStorageEntryRepositoryImpl;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureDatabaseEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureStorageEntryRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

    @ActivityScope
    @Provides
    public ImageReferenceRepository provideImageReferenceRepository() {
        return new RealmImageReferenceRepository();
    }

    @ActivityScope
    @Provides
    public LocalDocumentRepository provideLocalDocumentRepository(ContentResolver contentResolver) {
        return new LocalDocumentRepositoryImpl(contentResolver);
    }

    @ActivityScope
    @Provides
    public TextureDatabaseEntryRepository provideTextureListRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_TEXTURES) DatabaseReference reference) {
        return new TextureDatabaseEntryRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public TextureStorageEntryRepository provideTextureRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_DIRECTORY_TEXTURES) StorageReference reference) {
        return new TextureStorageEntryRepositoryImpl(reference);
    }
}
