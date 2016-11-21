package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.builder.data.repository.android.DocumentBitmapRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.android.DocumentFilenameRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.android.DocumentRepositoryImpl;
import com.lakeel.altla.vision.builder.data.repository.android.FileBitmapRepositoryImpl;
import com.lakeel.altla.vision.builder.domain.repository.DocumentBitmapRepository;
import com.lakeel.altla.vision.builder.domain.repository.DocumentFilenameRepository;
import com.lakeel.altla.vision.builder.domain.repository.DocumentRepository;
import com.lakeel.altla.vision.builder.domain.repository.FileBitmapRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public DocumentRepository provideDocumentRepository(ContentResolver contentResolver) {
        return new DocumentRepositoryImpl(contentResolver);
    }

    @ActivityScope
    @Provides
    public DocumentBitmapRepository provideDocumentBitmapRepository(ContentResolver contentResolver) {
        return new DocumentBitmapRepositoryImpl(contentResolver);
    }

    @ActivityScope
    @Provides
    public DocumentFilenameRepository provideDocumentFilenameRepository(ContentResolver contentResolver) {
        return new DocumentFilenameRepositoryImpl(contentResolver);
    }

    @ActivityScope
    @Provides
    public FileBitmapRepository provideFileBitmapRepository(ContentResolver contentResolver) {
        return new FileBitmapRepositoryImpl(contentResolver);
    }
}
