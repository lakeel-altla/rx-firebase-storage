package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.EditTextureModel;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.usecase.EnsureTextureCacheUseCase;
import com.lakeel.altla.vision.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindDocumentFilenameUseCase;
import com.lakeel.altla.vision.domain.usecase.FindFileBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserTextureUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserTextureUseCase;

import android.graphics.Bitmap;
import android.net.Uri;
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

    @Inject
    FindUserTextureUseCase findUserTextureUseCase;

    @Inject
    EnsureTextureCacheUseCase ensureTextureCacheUseCase;

    @Inject
    FindFileBitmapUseCase findFileBitmapUseCase;

    @Inject
    FindDocumentBitmapUseCase findDocumentBitmapUseCase;

    @Inject
    FindDocumentFilenameUseCase findDocumentFilenameUseCase;

    @Inject
    SaveUserTextureUseCase saveUserTextureUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RegisterTextureView view;

    private long prevBytesTransferred;

    private final EditTextureModel model = new EditTextureModel();

    private boolean localTextureSelected;

    @Inject
    public RegisterTexturePresenter() {
    }

    public void onCreate(@Nullable String id) {
        LOG.d("onCreate(): id = %s", id);

        model.id = id;
    }

    public void onCreateView(@NonNull RegisterTextureView view) {
        this.view = view;

        view.setTextureVisible(false);
        view.setLoadTextureProgressVisible(false);
    }

    public void onStart() {
        LOG.d("onStart()");

        view.showModel(model);

        if (localTextureSelected) {
            LOG.d("onStart(): Loading the selected local bitmap.");

            localTextureSelected = false;

            if (model.name == null || model.name.length() == 0) {
                loadLocalTextureBitmapAndName();
            } else {
                loadLocalTextureBitmap();
            }
        } else if (model.id != null) {
            LOG.d("onStart(): Loading the existing texture.");

            // Load the texture information.
            LOG.d("Loading the texture: id = %s", model.id);

            Subscription subscription = findUserTextureUseCase
                    // Find the texture entry to get its name.
                    .execute(model.id)
                    .toSingle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(entry -> {
                        LOG.d("Loaded the texture.");

                        model.name = entry.name;
                        view.showModel(model);

                        loadCachedTextureBitmap(entry.id);
                    }, e -> {
                        // TODO: How to recover.
                        LOG.w(String.format("Failed to load the texture: id = %s", model.id), e);
                    });
            compositeSubscription.add(subscription);
        } else {
            LOG.d("onStart(): Just onStart().");

            view.setTextureVisible(true);
        }
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onClickButtonSelectDocument() {
        view.showLocalTexturePicker();
    }

    public void onLocalTextureSelected(@NonNull Uri uri) {
        localTextureSelected = true;

        model.localUri = uri;

        // Release the bitmap if needed.
        if (model.bitmap != null) {
            model.bitmap.recycle();
            model.bitmap = null;
        }
    }

    private void loadCachedTextureBitmap(String id) {
        LOG.d("Loading the bitmap from the texture cache: id = %s", id);

        view.setTextureVisible(false);
        view.setLoadTextureProgressVisible(true);

        Subscription subscription = ensureTextureCacheUseCase
                // Ensure the texture cache.
                .execute(id, null)
                // Load the bitmap from the cache.
                .flatMap(findFileBitmapUseCase::execute)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    LOG.d("Loaded the bitmap from the texture cache.");

                    model.bitmap = bitmap;

                    view.setTextureVisible(true);
                    view.setLoadTextureProgressVisible(false);
                    view.showModel(model);
                }, e -> {
                    // TODO: How to recover.
                    LOG.w(String.format("Failed to load the bitmap from the texture cache: id = %s", id), e);

                    view.setLoadTextureProgressVisible(false);
                });

        compositeSubscription.add(subscription);
    }

    private void loadLocalTextureBitmap() {
        LOG.d("Loading the local texture: uri = %s", model.localUri);

        view.setTextureVisible(false);
        view.setLoadTextureProgressVisible(true);

        Subscription subscription = findDocumentBitmapUseCase
                .execute(model.localUri)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    LOG.d("Loaded the local texture.");

                    model.bitmap = bitmap;

                    view.setTextureVisible(true);
                    view.setLoadTextureProgressVisible(false);
                    view.showModel(model);
                }, e -> {
                    view.setLoadTextureProgressVisible(false);

                    if (e instanceof FileNotFoundException) {
                        LOG.w(String.format("The image could not be found: uri = %s", model.localUri), e);
                        view.showSnackbar(R.string.snackbar_image_file_not_found);
                    } else if (e instanceof IOException) {
                        LOG.w("Closing file failed.", e);
                    } else {
                        LOG.e("Unexpected error occured.", e);
                        view.showSnackbar(R.string.snackbar_unexpected_error_occured);
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void loadLocalTextureBitmapAndName() {
        LOG.d("Loading the bitmap and the name of the local texture: uri = %s", model.localUri);

        view.setTextureVisible(false);
        view.setLoadTextureProgressVisible(true);

        Subscription subscription = Single
                .just(new LocalBitmap(model.localUri))
                // Load the bitmap.
                .flatMap(this::loadLocalBitmap)
                // Load the filename as a texture name if needed.
                .flatMap(this::loadLocalFilename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(localBitmap -> {
                    LOG.d("Loaded the bitmap and the name of the local texture.");

                    model.bitmap = localBitmap.bitmap;
                    model.name = localBitmap.name;

                    view.setTextureVisible(true);
                    view.setLoadTextureProgressVisible(false);
                    view.showModel(model);
                }, e -> {
                    view.setLoadTextureProgressVisible(false);

                    if (e instanceof FileNotFoundException) {
                        LOG.w(String.format("The image could not be found: uri = %s",
                                            model.localUri), e);
                        view.showSnackbar(R.string.snackbar_image_file_not_found);
                    } else if (e instanceof IOException) {
                        LOG.w("Closing file failed.", e);
                    } else {
                        LOG.e("Unexpected error occured.", e);
                        view.showSnackbar(R.string.snackbar_unexpected_error_occured);
                    }
                });
        compositeSubscription.add(subscription);
    }

    private Single<LocalBitmap> loadLocalBitmap(LocalBitmap localBitmap) {
        return findDocumentBitmapUseCase.execute(localBitmap.uri)
                                        .map(bitmap -> {
                                            localBitmap.bitmap = bitmap;
                                            return localBitmap;
                                        });
    }

    private Single<LocalBitmap> loadLocalFilename(LocalBitmap localBitmap) {
        return findDocumentFilenameUseCase.execute(localBitmap.uri)
                                          .map(filename -> {
                                              localBitmap.name = filename;
                                              return localBitmap;
                                          });
    }

    public void onClickButtonRegister() {
        view.showUploadProgressDialog();

        String id = model.id;

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        LOG.i("Saving the user texture: id = %s", id);

        UserTexture userTexture = new UserTexture(id, model.name);

        String localUri = (model.localUri != null) ? model.localUri.toString() : null;

        Subscription subscription = saveUserTextureUseCase
                .execute(userTexture, localUri, (totalBytes, bytesTransferred) -> {
                    // The progress status.
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;
                    view.setUploadProgressDialogProgress(totalBytes, increment);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_userTexture -> {
                    // Done.
                    LOG.i("Saved the user texture.");

                    // Update ID of the model.
                    model.id = _userTexture.id;

                    view.hideUploadProgressDialog();
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    // Failed.
                    LOG.e("Failed to save the user texture.", e);

                    view.hideUploadProgressDialog();
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }

    public void afterNameChanged(String filename) {
        model.name = filename;
    }

    private final class LocalBitmap {

        private final Uri uri;

        private Bitmap bitmap;

        private String name;

        LocalBitmap(Uri uri) {
            this.uri = uri;
        }
    }
}
