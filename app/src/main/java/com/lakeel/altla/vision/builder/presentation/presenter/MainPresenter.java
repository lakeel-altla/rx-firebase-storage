package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.vision.builder.domain.model.ImageReference;
import com.lakeel.altla.vision.builder.domain.usecase.CreateImageReferenceUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindAllImageReferencesUseCase;
import com.lakeel.altla.vision.builder.presentation.di.module.Names;
import com.lakeel.altla.vision.builder.presentation.helper.DocumentBitmapLoader;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.BitmapModel;
import com.lakeel.altla.vision.builder.presentation.model.ObjectEditMode;
import com.lakeel.altla.vision.builder.presentation.view.MainView;
import com.lakeel.altla.vision.builder.presentation.view.ModelListItemView;
import com.lakeel.altla.vision.builder.presentation.view.renderer.MainRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.subscriptions.CompositeSubscription;

/**
 * Defines the presenter for {@link MainView}.
 */
public final class MainPresenter
        implements OnFrameAvailableListener, MainRenderer.OnPickedObjectChangedListener {

    private static final Log LOG = LogFactory.getLog(MainPresenter.class);

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context context;

    @Inject
    Tango tango;

    @Inject
    TangoUx tangoUx;

    @Inject
    TangoUpdateDispatcher tangoUpdateDispatcher;

    @Inject
    FindAllImageReferencesUseCase findAllImageReferencesUseCase;

    @Inject
    CreateImageReferenceUseCase createImageReferenceUseCase;

    @Inject
    DocumentBitmapLoader documentBitmapLoader;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final List<BitmapModel> models = new ArrayList<>();

    private MainView view;

    private MainRenderer renderer;

    private int lastSelectedPosition = -1;

    private boolean isModelPaneVisible;

    private boolean hasPickedObject;

    private volatile boolean active = true;

    private ObjectEditMode objectEditMode = ObjectEditMode.NONE;

    @Inject
    public MainPresenter() {
    }

    public void onCreateView(@NonNull MainView view) {
        LOG.v("onCreateView");

        this.view = view;

        this.view.setTangoUxLayout(tangoUx);

        renderer = new MainRenderer(context);
        renderer.setOnPickedObjectChangedListener(this);
        this.view.setSurfaceRenderer(renderer);

        this.view.setModelPaneVisible(false);
        this.view.setObjectMenuVisible(false);
        this.view.setTranslateObjectSelected(false);
        this.view.setRotateObjectSelected(false);
        this.view.setTranslateObjectMenuVisible(false);
        this.view.setRotateObjectMenuVisible(false);
        this.view.setTranslateObjectAxisSelected(Axis.X, true);
        this.view.setRotateObjectAxisSelected(Axis.Y, true);
    }

    public void onStart() {
        models.clear();

        Subscription subscription = findAllImageReferencesUseCase
                .execute()
                .map(this::loadBitmapModel)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageModels -> {
                    LOG.v("Image models exist: count = %d", imageModels.size());
                    models.addAll(imageModels);
                    view.updateModels();
                });
        compositeSubscription.add(subscription);
    }

    public void onResume() {
        renderer.connectToTangoCamera(tango);
        tangoUpdateDispatcher.getOnFrameAvailableListeners().add(this);
        active = true;
    }

    private BitmapModel loadBitmapModel(ImageReference imageReference) {
        Uri uri = Uri.parse(imageReference.uri);

        try {
            Bitmap bitmap = documentBitmapLoader.load(uri);
            return new BitmapModel(uri, bitmap);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    public void onPause() {
        active = false;
        tangoUpdateDispatcher.getOnFrameAvailableListeners().remove(this);
        renderer.disconnectFromTangoCamera();
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        if (active && cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
            renderer.onFrameAvailable();
            // TODO: remove the following commented code.
//            view.requestRender();
        }
    }

    @Override
    public void onPickedObjectChanged(String oldName, String newName) {
        hasPickedObject = (newName != null);
        view.setObjectMenuVisible(hasPickedObject);
    }

    public void onClickFabToggleModelPane() {
        isModelPaneVisible = !isModelPaneVisible;
        view.setModelPaneVisible(isModelPaneVisible);
    }

    public void onClickImageButtonAddModel() {
        view.showRegisterSceneObjectFragment(true);
//        view.showSelectImageMethodDialog(R.array.dialog_select_image_methods_items);
    }

    public void onTouchButtonTranslateObject() {
        objectEditMode = ObjectEditMode.TRANSLATE;
        renderer.setObjectEditMode(objectEditMode);

        view.setTranslateObjectSelected(true);
        view.setTranslateObjectMenuVisible(true);
        view.setRotateObjectSelected(false);
        view.setRotateObjectMenuVisible(false);
        view.setScaleObjectSelected(false);
    }

    public void onTouchButtonRotateObject() {
        objectEditMode = ObjectEditMode.ROTATE;
        renderer.setObjectEditMode(objectEditMode);

        view.setTranslateObjectSelected(false);
        view.setTranslateObjectMenuVisible(false);
        view.setRotateObjectSelected(true);
        view.setRotateObjectMenuVisible(true);
        view.setScaleObjectSelected(false);
    }

    public void onTouchButtonTranslateObjectAxis(Axis axis) {
        renderer.setTranslateObjectAxis(axis);

        view.setTranslateObjectAxisSelected(Axis.X, axis == Axis.X);
        view.setTranslateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        view.setTranslateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonRotateObjectAxis(Axis axis) {
        renderer.setRotateObjectAxis(axis);

        view.setRotateObjectAxisSelected(Axis.X, axis == Axis.X);
        view.setRotateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        view.setRotateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonScaleObject() {
        objectEditMode = ObjectEditMode.SCALE;
        renderer.setObjectEditMode(objectEditMode);

        view.setTranslateObjectSelected(false);
        view.setTranslateObjectMenuVisible(false);
        view.setRotateObjectSelected(false);
        view.setRotateObjectMenuVisible(false);
        view.setScaleObjectSelected(true);
    }

//    public void onSelectImageMethodSelected(int index) {
//        switch (index) {
//            case 0:
//                view.showImagePicker();
//                break;
//            case 1:
//                // TODO
//                break;
//        }
//    }

//    public void onImagePicked(Uri uri) {
//        LOG.d("Image picked: %s", uri);
//
//        Subscription subscription = documentBitmapLoader
//                .loadAsSingle(uri)
//                .flatMap(bitmap -> saveBitmap(uri, bitmap))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(bitmap -> {
//                    BitmapModel model = new BitmapModel(uri, bitmap);
//                    models.add(model);
//                    view.updateModels();
//                }, e -> {
//                    if (e instanceof FileNotFoundException) {
//                        view.showSnackbar(R.string.snackbar_image_file_not_found);
//                    } else if (e instanceof IOException) {
//                        // close に対する I/O エラーなのでログを出して無視する
//                        LOG.w("Closing file failed.", e);
//                    } else {
//                        view.showSnackbar(R.string.snackbar_unexpected_error_occured);
//                        LOG.e("Unexpected error occured.", e);
//                    }
//                });
//
//        compositeSubscription.add(subscription);
//    }

//    private Single<Bitmap> saveBitmap(Uri uri, Bitmap bitmap) {
//        ImageReference imageReference = new ImageReference(uri.toString());
//        return createImageReferenceUseCase.execute(imageReference)
//                                          .map(ir -> bitmap);
//    }

    public int getModelCount() {
        return models.size();
    }

    public void onCreateItemView(@NonNull ModelListItemView itemView) {
        ModelItemPresenter itemPresenter = new ModelItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public void onDropModel() {
        LOG.v("onDropModel");

        BitmapModel bitmapModel = models.get(lastSelectedPosition);
        renderer.addPlaneBitmap(bitmapModel.bitmap);
        lastSelectedPosition = -1;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        renderer.tryPickObject(e.getX(), e.getY());
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (hasPickedObject) {
            if (objectEditMode == ObjectEditMode.TRANSLATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    renderer.setTranslateObjectDistance(distanceX);
                } else {
                    renderer.setTranslateObjectDistance(distanceY);
                }
            } else if (objectEditMode == ObjectEditMode.ROTATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    renderer.setRotateObjectAngle(distanceX);
                } else {
                    renderer.setRotateObjectAngle(distanceY);
                }
            } else if (objectEditMode == ObjectEditMode.SCALE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    renderer.setScaleObjectSize(distanceX);
                } else {
                    renderer.setScaleObjectSize(distanceY);
                }
            }

            return true;
        }
        return false;
    }

    public final class ModelItemPresenter {

        private ModelListItemView mItemView;

        public void onCreateItemView(@NonNull ModelListItemView itemView) {
            mItemView = itemView;
        }

        public void onBind(int position) {
            BitmapModel model = models.get(position);
            mItemView.showModel(model);
        }

        public void onStartDrag(int position) {
            LOG.v("Starting drag: position = %d", position);

            lastSelectedPosition = position;
            mItemView.startDrag();
        }
    }
}