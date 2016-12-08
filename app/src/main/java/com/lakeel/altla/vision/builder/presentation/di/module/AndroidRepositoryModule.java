package com.lakeel.altla.vision.builder.presentation.di.module;

import com.lakeel.altla.vision.data.repository.android.DocumentBitmapRepositoryImpl;
import com.lakeel.altla.vision.data.repository.android.DocumentFilenameRepositoryImpl;
import com.lakeel.altla.vision.data.repository.android.DocumentRepositoryImpl;
import com.lakeel.altla.vision.data.repository.android.FileBitmapRepositoryImpl;
import com.lakeel.altla.vision.data.repository.android.TextureCacheRepositoryImpl;
import com.lakeel.altla.vision.domain.repository.DocumentBitmapRepository;
import com.lakeel.altla.vision.domain.repository.DocumentFilenameRepository;
import com.lakeel.altla.vision.domain.repository.DocumentRepository;
import com.lakeel.altla.vision.domain.repository.FileBitmapRepository;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;
import android.content.Context;

import javax.inject.Named;

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
    public FileBitmapRepository provideFileBitmapRepository() {
        return new FileBitmapRepositoryImpl();
    }

    @ActivityScope
    @Provides
    public TextureCacheRepository provideTextureCacheRepository(@Named(Names.ACTIVITY_CONTEXT) Context context) {
        return new TextureCacheRepositoryImpl(context);
    }
}
