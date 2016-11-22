package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.vision.builder.domain.model.TextureEntry;
import com.lakeel.altla.vision.builder.domain.usecase.DownloadTextureFileUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindAllTextureEntriesUseCase;
import com.lakeel.altla.vision.builder.domain.usecase.FindFileBitmapUseCase;
import com.lakeel.altla.vision.builder.presentation.di.module.Names;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.ObjectEditMode;
import com.lakeel.altla.vision.builder.presentation.model.TextureModel;
import com.lakeel.altla.vision.builder.presentation.view.MainView;
import com.lakeel.altla.vision.builder.presentation.view.ModelListItemView;
import com.lakeel.altla.vision.builder.presentation.view.renderer.MainRenderer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
    FindAllTextureEntriesUseCase findAllTextureEntriesUseCase;

    @Inject
    DownloadTextureFileUseCase downloadTextureFileUseCase;

    @Inject
    FindFileBitmapUseCase findFileBitmapUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final List<TextureModel> models = new ArrayList<>();

    private final SingleSelection selection = new SingleSelection();

    private MainView view;

    private MainRenderer renderer;

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

        LOG.d("Find all texture entries.");

        Subscription subscription = findAllTextureEntriesUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entry -> {
                    LOG.d("Found the entry: entry = %s", entry);

                    TextureModel model = new TextureModel(entry.id, entry.name);
                    models.add(model);
                    view.updateModels();
                }, e -> {
                    LOG.e("Failed to find all entries.", e);
                }, () -> {
                    LOG.d("Found all entries.");
                });
        compositeSubscription.add(subscription);
    }

    private void downloadTexture(TextureEntry entry) {
        LOG.d("Downloading the texture: entry = %s", entry);

        Subscription subscription = downloadTextureFileUseCase
                .execute(entry.id, (totalBytes, bytesTransferred) -> {
                    // TODO
                    LOG.v("The progress status: totalBytes = %d, bytesTransferred = %d", totalBytes, bytesTransferred);
                })
                .flatMap(findFileBitmapUseCase::execute)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    LOG.d("Downloaded the texture.");

                }, e -> {
                    // TODO: How to recover.
                    LOG.w(String.format("Failed to download the texture: entry = %s", entry), e);
                });
        compositeSubscription.add(subscription);
    }

    public void onResume() {
        renderer.connectToTangoCamera(tango);
        tangoUpdateDispatcher.getOnFrameAvailableListeners().add(this);
        active = true;
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
        view.showRegisterSceneObjectFragment();
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

    public int getModelCount() {
        return models.size();
    }

    public void onCreateItemView(@NonNull ModelListItemView itemView) {
        ModelItemPresenter itemPresenter = new ModelItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
        selection.addItemPresenter(itemPresenter);
    }

    public void onDropModel() {
        if (0 <= selection.selectedPosition) {
            TextureModel model = models.get(selection.selectedPosition);
            renderer.addPlaneBitmap(model.bitmap);
        }
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
            TextureModel model = models.get(position);
            mItemView.showModel(model);
        }

        public void onLoadBitmap(int position) {
            TextureModel model = models.get(position);

            LOG.d("Downloading the texture: id = %s", model.id);

            Subscription subscription = downloadTextureFileUseCase
                    .execute(model.id, (totalBytes, bytesTransferred) -> {
                        // Update the progress bar.
                        mItemView.showProgress((int) totalBytes, (int) bytesTransferred);
                    })
                    .flatMap(findFileBitmapUseCase::execute)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bitmap -> {
                        LOG.d("Downloaded the texture.");
                        // Set the bitmap into the model.
                        model.bitmap = bitmap;
                        // Hide the progress bar.
                        mItemView.hideProgress();
                        // Redraw.
                        mItemView.showModel(model);
                    }, e -> {
                        // TODO: How to recover.
                        LOG.w(String.format("Failed to download the texture: id = %s", model.id), e);
                    });
            compositeSubscription.add(subscription);
        }

        public void onClickViewTop(int position) {
            selection.setSelectedPosition(position);
        }

        public void onLongClickViewTop(int position) {
            selection.setSelectedPosition(position);
            mItemView.startDrag();
        }

        void setSelected(int selectedPosition, boolean selected) {
            mItemView.setSelected(selectedPosition, selected);
        }
    }

    private final class SingleSelection {

        int selectedPosition = -1;

        Set<ModelItemPresenter> itemPresenters = new HashSet<>();

        void addItemPresenter(ModelItemPresenter itemPresenter) {
            itemPresenters.add(itemPresenter);
        }

        void setSelectedPosition(int selectedPosition) {
            if (0 <= this.selectedPosition) {
                // Deselect the previous selection.
                for (ModelItemPresenter itemPresenter : itemPresenters) {
                    itemPresenter.setSelected(this.selectedPosition, false);
                }
            }

            if (this.selectedPosition == selectedPosition) {
                // Deselect only.
                this.selectedPosition = -1;
            } else if (0 <= selectedPosition) {
                // Select the new position.
                this.selectedPosition = selectedPosition;
                for (ModelItemPresenter itemPresenter : itemPresenters) {
                    itemPresenter.setSelected(this.selectedPosition, true);
                }
            }
        }
    }
}
