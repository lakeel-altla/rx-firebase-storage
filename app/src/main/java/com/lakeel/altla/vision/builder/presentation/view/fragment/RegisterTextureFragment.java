package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.EditTextureModel;
import com.lakeel.altla.vision.builder.presentation.presenter.RegisterTexturePresenter;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;
import com.lakeel.altla.vision.builder.presentation.view.activity.ActivityScopeContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public final class RegisterTextureFragment extends Fragment implements RegisterTextureView {

    private static final int REQUEST_CODE_ACTION_OPEN_DOCUMENT = 0;

    private static final String PARAM_ID = "id";

    @Inject
    RegisterTexturePresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.image_view_texture)
    ImageView imageViewTexture;

    @BindView(R.id.progress_bar_load_texture)
    ProgressBar progressBarLoadTexture;

    @BindView(R.id.text_input_edit_text_name)
    TextInputEditText textInputEditTextName;

    private ProgressDialog progressDialog;

    @NonNull
    public static RegisterTextureFragment newInstance(@Nullable String id) {
        RegisterTextureFragment fragment = new RegisterTextureFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = null;

        final Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getString(PARAM_ID);
        }

        presenter.onCreate(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_texture, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // TODO
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
    public void showModel(EditTextureModel model) {
        imageViewTexture.setImageBitmap(model.bitmap);
        textInputEditTextName.setText(model.name);
    }

    @Override
    public void showUploadProgressDialog() {
        // When displaying the progress rate, it is impossible to reset the progress rate,
        // so the instance can not be cached.
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progress_dialog_upload));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(0);
        progressDialog.show();
    }

    @Override
    public void setUploadProgressDialogProgress(long max, long diff) {
        if (progressDialog != null) {
            progressDialog.setMax((int) max);
            progressDialog.incrementProgressBy((int) diff);
        }
    }

    @Override
    public void hideUploadProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void showTexture(boolean visible) {
        imageViewTexture.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showLoadTextureProgress(int max, int progress) {
        progressBarLoadTexture.setMax(max);
        progressBarLoadTexture.setProgress(progress);
    }

    @Override
    public void hideLoadTextureProgress() {
        progressBarLoadTexture.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_select_document)
    void onClickButtonSelectDocument() {
        presenter.onClickButtonSelectDocument();
    }

    @OnClick(R.id.button_register)
    void onClickButtonRegister() {
        presenter.onClickButtonRegister();
    }

    @OnTextChanged(value = R.id.text_input_edit_text_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterNameChanged(Editable editable) {
        presenter.afterNameChanged(editable.toString());
    }
}
