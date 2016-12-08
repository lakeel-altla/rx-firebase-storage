package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.DocumentBitmapRepository;

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
