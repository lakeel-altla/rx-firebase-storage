package com.lakeel.altla.vision.builder.presentation.view.fragment;


import com.google.atap.tango.ux.TangoUx;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.presenter.MainPresenter;
import com.lakeel.altla.vision.builder.presentation.view.MainView;
import com.lakeel.altla.vision.builder.presentation.view.activity.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.view.adapter.ModelAdapter;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.TextureView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public final class MainFragment extends Fragment implements MainView {

    private static final Log LOG = LogFactory.getLog(MainFragment.class);

    private static final int REQUEST_CODE_ACTION_OPEN_DOCUMENT = 0;

    @Inject
    MainPresenter mPresenter;

    @BindView(R.id.view_top)
    View mViewTop;

//    @BindView(R.id.layout_tango_ux)
//    TangoUxLayout mTangoUxLayout;

    @BindView(R.id.texture_view)
    TextureView mTextureView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.view_group_model_pane)
    ViewGroup mViewGroupModelPane;

    @BindView(R.id.fab_toggle_model_pane)
    FloatingActionButton mFabToggleModelPane;

    @BindView(R.id.view_group_object_menu)
    ViewGroup mViewGroupObjectMenu;

    @BindView(R.id.view_group_translate_object_menu)
    ViewGroup mViewGroupTranslateObjectMenu;

    @BindView(R.id.view_group_rotate_object_menu)
    ViewGroup mViewGroupRotateObjectMenu;

    @BindView(R.id.button_translate_object)
    Button mButtonTranslateObject;

    @BindViews({ R.id.button_translate_object_in_x_axis, R.id.button_translate_object_in_y_axis,
                 R.id.button_translate_object_in_z_axis })
    Button[] mButtonsTranslateObjectAxes;

    @BindView(R.id.button_rotate_object)
    Button mButtonRotateObject;

    @BindViews({ R.id.button_rotate_object_in_x_axis, R.id.button_rotate_object_in_y_axis,
                 R.id.button_rotate_object_in_z_axis })
    Button[] mButtonsRotateObjectAxes;

    @BindView(R.id.button_scale_object)
    Button mButtonScaleObject;

    private GestureDetectorCompat mGestureDetector;

    private AlertDialog mDialog;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);

        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                LOG.d("onDown");
                // Must return true to receive motion events on onScroll.
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                LOG.d("onScroll");
                return mPresenter.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                LOG.d("onSingleTapUp");
                return mPresenter.onSingleTapUp(e);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new ModelAdapter(mPresenter));

        mTextureView.setFrameRate(60d);
        mTextureView.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);
        mTextureView.setOnDragListener((v, dragEvent) -> {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // returns true to accept a drag event.
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    mPresenter.onDropModel();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
            }

            return false;
        });
        mTextureView.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        mPresenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.onResume();
        mTextureView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mPresenter.onPause();
        mTextureView.onPause();
    }

    @Override
    public void setTangoUxLayout(TangoUx tangoUx) {
//        tangoUx.setLayout(mTangoUxLayout);
    }

    @Override
    public void setSurfaceRenderer(ISurfaceRenderer renderer) {
        mTextureView.setSurfaceRenderer(renderer);
    }

    @Override
    public void requestRender() {
        mTextureView.requestRenderUpdate();
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(mViewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setModelPaneVisible(boolean visible) {
        if (visible) {
            mViewGroupModelPane.setVisibility(View.VISIBLE);
            mFabToggleModelPane.setImageResource(R.drawable.ic_expand_more_black_24dp);
        } else {
            mViewGroupModelPane.setVisibility(View.GONE);
            mFabToggleModelPane.setImageResource(R.drawable.ic_expand_less_black_24dp);
        }
    }

    @Override
    public void showSelectImageMethodDialog(@ArrayRes int itemsId) {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.dialog_select_image_methods_title)
                    .setItems(itemsId, (dialogInterface, i) -> {
                        mPresenter.onSelectImageMethodSelected(i);
                    })
                    .create();
        }
        mDialog.show();
    }

    @Override
    public void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*");

        startActivityForResult(intent, REQUEST_CODE_ACTION_OPEN_DOCUMENT);
    }

    @Override
    public void updateModels() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setObjectMenuVisible(boolean visible) {
        if (visible) {
            mViewGroupObjectMenu.setVisibility(View.VISIBLE);
        } else {
            mViewGroupObjectMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTranslateObjectSelected(boolean selected) {
        mButtonTranslateObject.setPressed(selected);
    }

    @Override
    public void setTranslateObjectMenuVisible(boolean visible) {
        mViewGroupTranslateObjectMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTranslateObjectAxisSelected(Axis axis, boolean selected) {
        mButtonsTranslateObjectAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void setRotateObjectSelected(boolean selected) {
        mButtonRotateObject.setPressed(selected);
    }

    @Override
    public void setRotateObjectMenuVisible(boolean visible) {
        mViewGroupRotateObjectMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setRotateObjectAxisSelected(Axis axis, boolean selected) {
        mButtonsRotateObjectAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void setScaleObjectSelected(boolean selected) {
        mButtonScaleObject.setPressed(selected);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_ACTION_OPEN_DOCUMENT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Uri uri = (data != null) ? data.getData() : null;
                if (uri != null) {
                    mPresenter.onImagePicked(uri);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.image_button_add_model)
    void onClickImageButtonAddModel() {
        mPresenter.onClickImageButtonAddModel();
    }

    @OnClick(R.id.fab_toggle_model_pane)
    void onClickFabToggleModelPane() {
        mPresenter.onClickFabToggleModelPane();
    }

    //
    // NOTE:
    //
    // To keep a button pressed, call setPressed(true) and return true in onTouch event handlers
    // instead of an onClick ones.
    //

    @OnTouch(R.id.button_translate_object)
    boolean onTouchButtonTranslateObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonTranslateObject.setPressed(true);
            mPresenter.onTouchButtonTranslateObject();
        }
        return true;
    }

    @OnTouch(R.id.button_translate_object_in_x_axis)
    boolean onTouchButtonTranslateObjectInXAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonsTranslateObjectAxes[Axis.X.getValue()].setPressed(true);
            mPresenter.onTouchButtonTranslateObjectAxis(Axis.X);
        }
        return true;
    }

    @OnTouch(R.id.button_translate_object_in_y_axis)
    boolean onTouchButtonTranslateObjectInYAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonsTranslateObjectAxes[Axis.Y.getValue()].setPressed(true);
            mPresenter.onTouchButtonTranslateObjectAxis(Axis.Y);
        }
        return true;
    }

    @OnTouch(R.id.button_translate_object_in_z_axis)
    boolean onTouchButtonTranslateObjectInZAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonsTranslateObjectAxes[Axis.Z.getValue()].setPressed(true);
            mPresenter.onTouchButtonTranslateObjectAxis(Axis.Z);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object)
    boolean onTouchButtonRotateObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonRotateObject.setPressed(true);
            mPresenter.onTouchButtonRotateObject();
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_x_axis)
    boolean onTouchButtonRotateObjectInXAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonsRotateObjectAxes[Axis.X.getValue()].setPressed(true);
            mPresenter.onTouchButtonRotateObjectAxis(Axis.X);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_y_axis)
    boolean onTouchkButtonRotateObjectInYAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonsRotateObjectAxes[Axis.Y.getValue()].setPressed(true);
            mPresenter.onTouchButtonRotateObjectAxis(Axis.Y);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_z_axis)
    boolean onTouchButtonRotateObjectInZAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonsRotateObjectAxes[Axis.Z.getValue()].setPressed(true);
            mPresenter.onTouchButtonRotateObjectAxis(Axis.Z);
        }
        return true;
    }

    @OnTouch(R.id.button_scale_object)
    boolean onTouchButtonScaleObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mButtonScaleObject.setPressed(true);
            mPresenter.onTouchButtonScaleObject();
        }
        return true;
    }
}
