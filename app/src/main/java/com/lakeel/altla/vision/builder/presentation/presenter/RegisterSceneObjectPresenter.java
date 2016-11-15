package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.usecase.UploadFileUseCase;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentBitmapLoader;
import com.lakeel.altla.vision.builder.presentation.view.RegisterSceneObjectView;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class RegisterSceneObjectPresenter {

    private static final Log LOG = LogFactory.getLog(RegisterSceneObjectPresenter.class);

    @Inject
    DocumentBitmapLoader documentBitmapLoader;

    @Inject
    UploadFileUseCase uploadFileUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RegisterSceneObjectView view;

    private boolean editMode;

    private Uri uri;

    @Inject
    public RegisterSceneObjectPresenter() {
    }

    public void onCreate(boolean editMode) {
        this.editMode = editMode;
    }

    public void onCreateView(@NonNull RegisterSceneObjectView view) {
        this.view = view;
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onClickButtonSelectDocument() {
        view.showImagePicker();
    }

    public void onImagePicked(Uri uri) {
        Subscription subscription = documentBitmapLoader
                .loadAsSingle(uri)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    this.uri = uri;
                    view.showPickedImage(bitmap);
                }, e -> {
                    if (e instanceof FileNotFoundException) {
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
        Subscription subscription = uploadFileUseCase
                .execute(uri.toString(), (totalBytes, bytesTransferred) -> {
                    // TODO
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(uuid -> {
                    // TODO
                    view.closeRegisterSceneObjectFragment();
                }, e -> {
                    // TODO
                    LOG.e("Uploading file failed.", e);
                });
        compositeSubscription.add(subscription);
    }
}
