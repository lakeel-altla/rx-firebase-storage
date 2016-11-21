package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.repository.DocumentFilenameRepository;

import android.net.Uri;

import javax.inject.Inject;

import rx.Single;

public final class FindDocumentFilenameUseCase {

    @Inject
    DocumentFilenameRepository documentFilenameRepository;

    @Inject
    public FindDocumentFilenameUseCase() {
    }

    public Single<String> execute(Uri uri) {
        if (uri == null) throw new ArgumentNullException("uri");

        return documentFilenameRepository.find(uri);
    }
}
