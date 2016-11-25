package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.model.TextureMetadata;
import com.lakeel.altla.vision.builder.domain.usecase.AddTextureUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.DownloadTextureFileUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindDocumentFilenameUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindFileBitmapUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindTextureEntryUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.UpdateTextureUseCase;
import com.lakeel.altla.vision.builder.presentation.model.EditTextureModel;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;

import android.net.Uri;
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

    @Inject
    FindTextureEntryUseCase findTextureEntryUseCase;

    @Inject
    DownloadTextureFileUseCase downloadTextureFileUseCase;

    @Inject
    FindFileBitmapUseCase findFileBitmapUseCase;

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

    private long prevBytesTransferred;

    private final EditTextureModel model = new EditTextureModel();

    private Uri pickedImageUri;

    @Inject
    public RegisterTexturePresenter() {
    }

    public void onCreate(@Nullable String id) {
        model.id = id;
    }

    public void onCreateView(@NonNull RegisterTextureView view) {
        this.view = view;
    }

    public void onStart() {
        LOG.d("onStart()");

        if (model.id != null) {
            // Load the texture information.
            LOG.d("Loading the texture: id = %s", model.id);

            // TODO: use the progress ring.

            Subscription subscription = findTextureEntryUseCase
                    // Find the texture entry to get its name.
                    .execute(model.id)
                    // Store the name into the model.
                    .map(entry -> {
                        model.name = entry.name;
                        return model;
                    })
                    .toSingle()
                    // Ensure the texture cache.
                    .flatMap(model -> downloadTextureFileUseCase.execute(model.id, (totalBytes, bytesTransferred) -> {
                        // Update the progress bar.
                        // TODO
                    }))
                    // Load the bitmap from the cache.
                    .flatMap(findFileBitmapUseCase::execute)
                    // Store the bitmap into the model.
                    .map(bitmap -> {
                        model.bitmap = bitmap;
                        return model;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        LOG.d("Loaded the texture.");

                        if (pickedImageUri == null) {
                            view.showModel(model);
                        } else {
                            loadPickedImage();
                        }
                    }, e -> {
                        // TODO: How to recover.
                        LOG.w(String.format("Failed to load the texture: id = %s", model.id), e);
                    });
            compositeSubscription.add(subscription);
        }
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onClickButtonSelectDocument() {
        view.showImagePicker();
    }

    public void onImagePicked(Uri uri) {
        pickedImageUri = uri;
    }

    private void loadPickedImage() {
        LOG.d("Loading the bitmap & the filename: pickedImageUri = %s", pickedImageUri);

        Subscription subscription = findDocumentBitmapUseCase
                .execute(pickedImageUri)
                .map(bitmap -> {
                    EditTextureModel model = new EditTextureModel();
                    model.localUri = pickedImageUri;
                    model.bitmap = bitmap;
                    return model;
                })
                .flatMap(this::findFilename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    LOG.d("Loaded the bitmap & the filename.");

                    // Copy the finished model to the instance field model.
                    this.model.localUri = model.localUri;
                    this.model.bitmap = model.bitmap;
                    this.model.name = model.name;

                    view.showModel(this.model);
                }, e -> {
                    if (e instanceof FileNotFoundException) {
                        LOG.w(String.format("The image could not be found: pickedImageUri = %s", pickedImageUri), e);
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

    private Single<EditTextureModel> findFilename(EditTextureModel model) {
        return findDocumentFilenameUseCase.execute(model.localUri)
                                          .map(filename -> {
                                              model.name = filename;
                                              return model;
                                          });
    }

    public void onClickButtonRegister() {
        view.showUploadProgressDialog();

        TextureMetadata metadata = new TextureMetadata();

        if (model.id == null) {
            LOG.i("Adding the texture.");

            Subscription subscription = addTextureUseCase
                    .execute(model.name, model.localUri.toString(), metadata, (totalBytes, bytesTransferred) -> {
                        // The progress status.
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        view.setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                        // Done.
                        LOG.i("Added the texture: id = %s", id);

                        // Store the id into the model.
                        model.id = id;

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
            LOG.i("Updating the texture: id = %s", model.id);

            Subscription subscription = updateTextureUseCase
                    .execute(model.id, model.name, model.localUri.toString(), metadata,
                             (totalBytes, bytesTransferred) -> {
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

    public void afterNameChanged(String filename) {
        model.name = filename;
    }
}
