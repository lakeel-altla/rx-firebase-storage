package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindDocumentFilenameUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.RegisterTextureUseCase;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class RegisterTexturePresenter {

    private static final Log LOG = LogFactory.getLog(RegisterTexturePresenter.class);

    private static final String STATE_ID = "id";

    @Inject
    FindDocumentBitmapUseCase findDocumentBitmapUseCase;

    @Inject
    FindDocumentFilenameUseCase findDocumentFilenameUseCase;

    @Inject
    RegisterTextureUseCase registerTextureUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RegisterTextureView view;

    private Uri uri;

    private String filename;

    private long prevBytesTransferred;

    private String id;

    @Inject
    public RegisterTexturePresenter() {
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Restore the uuid.
        if (savedInstanceState != null) {
            id = savedInstanceState.getString(STATE_ID);
        }

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public void onCreateView(@NonNull RegisterTextureView view) {
        this.view = view;
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ID, id);
    }

    public void onClickButtonSelectDocument() {
        view.showImagePicker();
    }

    public void onImagePicked(Uri uri) {
        LOG.d("Loading the bitmap & the filename: uri = %s", uri);

        Subscription subscription = findDocumentBitmapUseCase
                .execute(uri)
                .flatMap(bitmap -> findFilename(uri, bitmap))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmapAndFilename -> {
                    LOG.d("Loaded the bitmap & the filename.");

                    this.uri = uri;
                    filename = bitmapAndFilename.filename;

                    view.showImage(bitmapAndFilename.bitmap);
                    view.showFilename(bitmapAndFilename.filename);
                }, e -> {
                    if (e instanceof FileNotFoundException) {
                        LOG.w(String.format("The image could not be found: uri = %s", uri), e);
                        view.showSnackbar(R.string.snackbar_image_file_not_found);
                    } else if (e instanceof IOException) {
                        LOG.w("Closing file failed.", e);
                    } else {
                        view.showSnackbar(R.string.snackbar_unexpected_error_occured);
                        LOG.e("Unexpected error occured.", e);
                    }
                });
        compositeSubscription.add(subscription);
    }

    private Single<BitmapAndFilename> findFilename(Uri uri, Bitmap bitmap) {
        return findDocumentFilenameUseCase.execute(uri)
                                          .map(filename -> new BitmapAndFilename(bitmap, filename));
    }

    public void onClickButtonRegister() {
        LOG.i("Registering the texture: id = %s", id);

        view.showUploadProgressDialog();

        TextureMetadata metadata = new TextureMetadata();

        Subscription subscription = registerTextureUseCase
                .execute(id, filename, uri.toString(), metadata, (totalBytes, bytesTransferred) -> {
                    // The progress status.
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;
                    view.setUploadProgressDialogProgress(totalBytes, increment);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entryId -> {
                    // Done.
                    LOG.i("Registered the texture.");

                    view.hideUploadProgressDialog();
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    // Failed.
                    LOG.e("Failed to register the texture.", e);

                    view.hideUploadProgressDialog();
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }

    public void afterFilenameChanged(String filename) {
        this.filename = filename;
    }

    private class BitmapAndFilename {

        final Bitmap bitmap;

        final String filename;

        BitmapAndFilename(Bitmap bitmap, String filename) {
            this.bitmap = bitmap;
            this.filename = filename;
        }
    }
}
