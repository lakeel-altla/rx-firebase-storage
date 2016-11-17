package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.model.TextureDatabaseEntry;
import com.lakeel.altla.vision.builder.domain.usecase.RegisterTextureUseCase;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentBitmapLoader;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentFilenameLoader;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class RegisterTexturePresenter {

    private static final Log LOG = LogFactory.getLog(RegisterTexturePresenter.class);

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

    @Inject
    public RegisterTexturePresenter() {
    }

    public void onCreateView(@NonNull RegisterTextureView view) {
        this.view = view;
    }

    public void onStop() {
        compositeSubscription.clear();
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
        LOG.d("Registering the texture.");

        view.showUploadProgressDialog();

        TextureDatabaseEntry.Metadata metadata = new TextureDatabaseEntry.Metadata();
        metadata.filename = filename;

        Subscription subscription =
                registerTextureUseCase.execute(uri.toString(), metadata, (totalBytes, bytesTransferred) -> {
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;
                    view.setUploadProgressDialogProgress(totalBytes, increment);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(textureList -> {
                    LOG.d("Registered the texture.");
                    view.hideUploadProgressDialog();
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
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
