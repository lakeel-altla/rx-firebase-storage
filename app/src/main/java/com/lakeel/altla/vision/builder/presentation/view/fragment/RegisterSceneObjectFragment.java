package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.presenter.RegisterSceneObjectPresenter;
import com.lakeel.altla.vision.builder.presentation.view.RegisterSceneObjectView;
import com.lakeel.altla.vision.builder.presentation.view.activity.ActivityScopeContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class RegisterSceneObjectFragment extends Fragment implements RegisterSceneObjectView {

    private static final String ARG_EDIT_MODE = "editMode";

    private static final int REQUEST_CODE_ACTION_OPEN_DOCUMENT = 0;

    @Inject
    RegisterSceneObjectPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.image_view)
    ImageView imageView;

    private InteractionListener interactionListener;

    public static RegisterSceneObjectFragment newInstance(boolean editMode) {
        RegisterSceneObjectFragment fragment = new RegisterSceneObjectFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_EDIT_MODE, editMode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interactionListener = InteractionListener.class.cast(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean editMode = false;
        if (getArguments() != null) {
            editMode = getArguments().getBoolean(ARG_EDIT_MODE);
        }

        presenter.onCreate(editMode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_scene_object, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_ACTION_OPEN_DOCUMENT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Uri uri = (data != null) ? data.getData() : null;
                if (uri != null) {
                    presenter.onImagePicked(uri);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*");

        startActivityForResult(intent, REQUEST_CODE_ACTION_OPEN_DOCUMENT);
    }

    @Override
    public void showPickedImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void closeRegisterSceneObjectFragment() {
        interactionListener.onCloseRegisterSceneObjectFragment();
    }

    @OnClick(R.id.button_select_document)
    void onClickButtonSelectDocument() {
        presenter.onClickButtonSelectDocument();
    }

    @OnClick(R.id.button_register)
    void onClickButtonRegister() {
        presenter.onClickButtonRegister();
    }

    public interface InteractionListener {

        void onCloseRegisterSceneObjectFragment();
    }
}
