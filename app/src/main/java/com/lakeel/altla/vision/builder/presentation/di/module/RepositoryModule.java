package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.builder.data.repository.FirebaseFileRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.LocalDocumentRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.RealmImageReferenceRepository;
import com.lakeel.altla.vision.builder.domain.repository.FirebaseFileRepository;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;
import com.lakeel.altla.vision.builder.domain.repository.LocalDocumentRepository;
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
    public FirebaseFileRepository provideFirebaseFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_FILES_DIRECTORY) StorageReference reference) {
        return new FirebaseFileRepositoryImpl(reference);
    }
}
