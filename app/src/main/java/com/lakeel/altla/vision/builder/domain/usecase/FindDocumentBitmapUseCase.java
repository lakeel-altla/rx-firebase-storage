package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.repository.DocumentBitmapRepository;

import android.graphics.Bitmap;
import android.net.Uri;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class FindDocumentBitmapUseCase {

    @Inject
    DocumentBitmapRepository documentBitmapRepository;

    @Inject
    public FindDocumentBitmapUseCase() {
    }

    public Single<Bitmap> execute(Uri uri) {
        if (uri == null) throw new ArgumentNullException("uri");

        return documentBitmapRepository.find(uri)
                                       .subscribeOn(Schedulers.io());
    }
}
