package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.usecase.RegisterTextureUseCase;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentBitmapLoader;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentFilenameLoader;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class RegisterTexturePresenter {

    private static final Log LOG = LogFactory.getLog(RegisterTexturePresenter.class);

    private static final String STATE_ENTRY_ID = "entryId";

    @Inject
    DocumentBitmapLoader documentBitmapLoader;

    @Inject
    DocumentFilenameLoader documentFilenameLoader;

    @Inject
    RegisterTextureUseCase registerTextureUseCase;

    @Inject
    ContentResolver contentResolver;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RegisterTextureView view;

    private Uri uri;

    private String filename;

    private long prevBytesTransferred;

    private String entryId;

    @Inject
    public RegisterTexturePresenter() {
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Restore the uuid.
        if (savedInstanceState != null) {
            entryId = savedInstanceState.getString(STATE_ENTRY_ID);
        }

        if (entryId == null) {
            entryId = UUID.randomUUID().toString();
        }
    }

    public void onCreateView(@NonNull RegisterTextureView view) {
        this.view = view;
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ENTRY_ID, entryId);
    }

    public void onClickButtonSelectDocument() {
        view.showImagePicker();
    }

    public void onImagePicked(Uri uri) {
        LOG.d("Loading the image: uri = %s", uri);

        Subscription subscription = documentBitmapLoader
                .loadAsSingle(uri)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    this.uri = uri;
                    filename = documentFilenameLoader.load(uri);

                    LOG.d("Loaded the image.");

                    view.showImage(bitmap);
                    view.showFilename(filename);
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

    public void onClickButtonRegister() {
        LOG.i("Registering the texture: entryId = %s", entryId);

        view.showUploadProgressDialog();

        TextureMetadata metadata = new TextureMetadata();
        metadata.filename = filename;

        Subscription subscription = registerTextureUseCase
                .execute(entryId, uri.toString(), metadata, (totalBytes, bytesTransferred) -> {
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
}
