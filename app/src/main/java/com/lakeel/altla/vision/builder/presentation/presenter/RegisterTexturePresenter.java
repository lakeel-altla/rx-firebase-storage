package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.usecase.AddTextureUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindDocumentFilenameUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.UpdateTextureUseCase;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

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
    AddTextureUseCase addTextureUseCase;

    @Inject
    UpdateTextureUseCase updateTextureUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RegisterTextureView view;

    private Uri uri;

    private String filename;

    private long prevBytesTransferred;

    private String id;

    @Inject
    public RegisterTexturePresenter() {
    }

    public void onCreate(@Nullable String id) {
        this.id = id;
    }

    public void onCreateView(@NonNull RegisterTextureView view) {
        this.view = view;

        this.view.saveIdAsInstanceState(id);
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

    private Single<DocumentModel> findFilename(Uri uri, Bitmap bitmap) {
        return findDocumentFilenameUseCase.execute(uri)
                                          .map(filename -> new DocumentModel(bitmap, filename));
    }

    public void onClickButtonRegister() {
        view.showUploadProgressDialog();

        TextureMetadata metadata = new TextureMetadata();

        if (id == null) {
            LOG.i("Adding the texture.");

            Subscription subscription = addTextureUseCase
                    .execute(filename, uri.toString(), metadata, (totalBytes, bytesTransferred) -> {
                        // The progress status.
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        view.setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                        // Done.
                        LOG.i("Added the texture: id = %s", id);

                        this.id = id;

                        view.hideUploadProgressDialog();
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        // Failed.
                        LOG.e("Failed to add the texture.", e);

                        view.hideUploadProgressDialog();
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeSubscription.add(subscription);
        } else {
            LOG.i("Updating the texture: id = %s", id);

            Subscription subscription = updateTextureUseCase
                    .execute(id, filename, uri.toString(), metadata, (totalBytes, bytesTransferred) -> {
                        // The progress status.
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        view.setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                        // Done.
                        LOG.i("Updated the texture.");

                        view.hideUploadProgressDialog();
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        // Failed.
                        LOG.e("Failed to update the texture.", e);

                        view.hideUploadProgressDialog();
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeSubscription.add(subscription);
        }
    }

    public void afterFilenameChanged(String filename) {
        this.filename = filename;
    }

    public static final class State {

        private boolean editMode;

        private String id;

        private String name;
    }

    private class DocumentModel {

        final Bitmap bitmap;

        final String filename;

        DocumentModel(Bitmap bitmap, String filename) {
            this.bitmap = bitmap;
            this.filename = filename;
        }
    }
}
