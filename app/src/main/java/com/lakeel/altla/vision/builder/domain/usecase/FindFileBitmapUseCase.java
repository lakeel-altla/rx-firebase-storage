package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.repository.FileBitmapRepository;

import android.graphics.Bitmap;

import java.io.File;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class FindFileBitmapUseCase {

    @Inject
    FileBitmapRepository fileBitmapRepository;

    @Inject
    public FindFileBitmapUseCase() {
    }

    public Single<Bitmap> execute(File file) {
        if (file == null) throw new ArgumentNullException("file");

        return fileBitmapRepository.find(file)
                                   .subscribeOn(Schedulers.io());
    }
}
