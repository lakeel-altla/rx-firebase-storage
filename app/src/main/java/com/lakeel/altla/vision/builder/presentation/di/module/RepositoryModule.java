package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.builder.data.repository.LocalDocumentRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.RealmImageReferenceRepository;
import com.lakeel.altla.vision.builder.data.repository.TextureEntryRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.TextureFileRepositoryImpl;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureEntryRepository;
import com.lakeel.altla.vision.builder.domain.repository.TextureFileRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;
import android.content.Context;

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
    public TextureEntryRepository provideTextureEntryRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_APP_ROOT) DatabaseReference reference, FirebaseAuth auth) {
        return new TextureEntryRepositoryImpl(reference, auth);
    }

    @ActivityScope
    @Provides
    public TextureFileRepository provideTextureFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_DIRECTORY_TEXTURES) StorageReference reference,
            @Named(Names.ACTIVITY_CONTEXT) Context context) {
        return new TextureFileRepositoryImpl(reference, context);
    }
}
