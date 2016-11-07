package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.domain.model.ImageReference;
import com.lakeel.altla.vision.builder.domain.usecase.CreateImageReferenceUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindAllImageReferencesUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindPointCloudPlane;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.subscriptions.CompositeSubscription;

public final class MainPresenter
        implements OnFrameAvailableListener, MainRenderer.OnPickedObjectChangedListener {

    private static final Log LOG = LogFactory.getLog(MainPresenter.class);

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context mContext;

    @Inject
    Tango mTango;

    @Inject
    TangoUx mTangoUx;

    @Inject
    TangoUpdateDispatcher mTangoUpdateDispatcher;

    @Inject
    FindAllImageReferencesUseCase mFindAllImageReferencesUseCase;

    @Inject
    CreateImageReferenceUseCase mCreateImageReferenceUseCase;

    @Inject
    FindPointCloudPlane mFindPointCloudPlane;

    @Inject
    DocumentBitmapLoader mDocumentBitmapLoader;

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private final List<BitmapModel> mModels = new ArrayList<>();

    private MainView mView;

    private MainRenderer mRenderer;

    private int mLastSelectedPosition = -1;

    private boolean mIsModelPaneVisible;

    private boolean mHasPickedObject;

    private ObjectEditMode mObjectEditMode = ObjectEditMode.NONE;

    @Inject
    public MainPresenter() {
    }

    public void onCreateView(@NonNull MainView view) {
        mView = view;

        mView.setTangoUxLayout(mTangoUx);

        mRenderer = new MainRenderer(mContext);
        mRenderer.setOnPickedObjectChangedListener(this);
        mView.setSurfaceRenderer(mRenderer);

        mView.setModelPaneVisible(false);
        mView.setObjectMenuVisible(false);
        mView.setTranslateObjectSelected(false);
        mView.setRotateObjectSelected(false);
        mView.setTranslateObjectMenuVisible(false);
        mView.setRotateObjectMenuVisible(false);
        mView.setTranslateObjectAxisSelected(Axis.X, true);
        mView.setRotateObjectAxisSelected(Axis.Y, true);
    }

    public void onStart() {
        mModels.clear();

        Subscription subscription = mFindAllImageReferencesUseCase
                .execute()
                .map(this::loadBitmapModel)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageModels -> {
                    LOG.v("Image models exist: count = %d", imageModels.size());
                    mModels.addAll(imageModels);
                    mView.updateModels();
                });
        mCompositeSubscription.add(subscription);
    }

    public void onResume() {
        mRenderer.connectToTangoCamera(mTango);
        mTangoUpdateDispatcher.getOnFrameAvailableListeners().add(this);
    }

    private BitmapModel loadBitmapModel(ImageReference imageReference) {
        Uri uri = Uri.parse(imageReference.uri);

        try {
            Bitmap bitmap = mDocumentBitmapLoader.load(uri);
            return new BitmapModel(uri, bitmap);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    public void onPause() {
        mTangoUpdateDispatcher.getOnFrameAvailableListeners().remove(this);
        mRenderer.disconnectFromTangoCamera();
    }

    public void onStop() {
        mCompositeSubscription.clear();
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        if (cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
            mRenderer.onFrameAvailable();
            mView.requestRender();
        }
    }

    @Override
    public void onPickedObjectChanged(String oldName, String newName) {
        mHasPickedObject = (newName != null);
        mView.setObjectMenuVisible(mHasPickedObject);
    }

    public void onClickFabToggleModelPane() {
        mIsModelPaneVisible = !mIsModelPaneVisible;
        mView.setModelPaneVisible(mIsModelPaneVisible);
    }

    public void onClickImageButtonAddModel() {
        mView.showSelectImageMethodDialog(R.array.dialog_select_image_methods_items);
    }

    public void onTouchButtonTranslateObject() {
        mObjectEditMode = ObjectEditMode.TRANSLATE;
        mRenderer.setObjectEditMode(mObjectEditMode);

        mView.setTranslateObjectSelected(true);
        mView.setTranslateObjectMenuVisible(true);
        mView.setRotateObjectSelected(false);
        mView.setRotateObjectMenuVisible(false);
        mView.setScaleObjectSelected(false);
    }

    public void onTouchButtonRotateObject() {
        mObjectEditMode = ObjectEditMode.ROTATE;
        mRenderer.setObjectEditMode(mObjectEditMode);

        mView.setTranslateObjectSelected(false);
        mView.setTranslateObjectMenuVisible(false);
        mView.setRotateObjectSelected(true);
        mView.setRotateObjectMenuVisible(true);
        mView.setScaleObjectSelected(false);
    }

    public void onTouchButtonTranslateObjectAxis(Axis axis) {
        mRenderer.setTranslateObjectAxis(axis);

        mView.setTranslateObjectAxisSelected(Axis.X, axis == Axis.X);
        mView.setTranslateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        mView.setTranslateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonRotateObjectAxis(Axis axis) {
        mRenderer.setRotateObjectAxis(axis);

        mView.setRotateObjectAxisSelected(Axis.X, axis == Axis.X);
        mView.setRotateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        mView.setRotateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonScaleObject() {
        mObjectEditMode = ObjectEditMode.SCALE;
        mRenderer.setObjectEditMode(mObjectEditMode);

        mView.setTranslateObjectSelected(false);
        mView.setTranslateObjectMenuVisible(false);
        mView.setRotateObjectSelected(false);
        mView.setRotateObjectMenuVisible(false);
        mView.setScaleObjectSelected(true);
    }

    public void onSelectImageMethodSelected(int index) {
        switch (index) {
            case 0:
                mView.showImagePicker();
                break;
            case 1:
                // TODO
                break;
        }
    }

    public void onImagePicked(Uri uri) {
        LOG.d("Image picked: %s", uri);

        Subscription subscription = mDocumentBitmapLoader
                .loadAsSingle(uri)
                .flatMap(bitmap -> saveBitmap(uri, bitmap))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    BitmapModel model = new BitmapModel(uri, bitmap);
                    mModels.add(model);
                    mView.updateModels();
                }, e -> {
                    if (e instanceof FileNotFoundException) {
                        mView.showSnackbar(R.string.snackbar_image_file_not_found);
                    } else if (e instanceof IOException) {
                        // close に対する I/O エラーなのでログを出して無視する
                        LOG.w("Closing file failed.", e);
                    } else {
                        mView.showSnackbar(R.string.snackbar_unexpected_error_occured);
                        LOG.e("Unexpected error occured.", e);
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    private Single<Bitmap> saveBitmap(Uri uri, Bitmap bitmap) {
        ImageReference imageReference = new ImageReference(uri.toString());
        return mCreateImageReferenceUseCase.execute(imageReference)
                                           .map(ir -> bitmap);
    }

    public int getModelCount() {
        return mModels.size();
    }

    public void onCreateItemView(@NonNull ModelListItemView itemView) {
        ModelItemPresenter itemPresenter = new ModelItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public void onDropModel() {
        LOG.v("onDropModel");

        BitmapModel bitmapModel = mModels.get(mLastSelectedPosition);
        mRenderer.addPlaneBitmap(bitmapModel.bitmap);
        mLastSelectedPosition = -1;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        mRenderer.tryPickObject(e.getX(), e.getY());
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mHasPickedObject) {
            if (mObjectEditMode == ObjectEditMode.TRANSLATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    mRenderer.setTranslateObjectDistance(distanceX);
                } else {
                    mRenderer.setTranslateObjectDistance(distanceY);
                }
            } else if (mObjectEditMode == ObjectEditMode.ROTATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    mRenderer.setRotateObjectAngle(distanceX);
                } else {
                    mRenderer.setRotateObjectAngle(distanceY);
                }
            } else if (mObjectEditMode == ObjectEditMode.SCALE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    mRenderer.setScaleObjectSize(distanceX);
                } else {
                    mRenderer.setScaleObjectSize(distanceY);
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
            BitmapModel model = mModels.get(position);
            mItemView.showModel(model);
        }

        public void onStartDrag(int position) {
            LOG.v("Starting drag: position = %d", position);

            mLastSelectedPosition = position;
            mItemView.startDrag();
        }
    }
}
