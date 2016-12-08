package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaDescriptionListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.AreaDescriptionModelAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class AreaDescriptionListFragment extends Fragment implements AreaDescriptionListView {

    private static final Log LOG = LogFactory.getLog(AreaDescriptionListFragment.class);

    @Inject
    AreaDescriptionListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    public static AreaDescriptionListFragment newInstance() {
        return new AreaDescriptionListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_description_list, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setAdapter(new AreaDescriptionModelAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOG.d("onActivityResult: requestCode = %d, resultCode = %d, inrent = %s", requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            presenter.onExported();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItem(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void updateItemRemoved(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void showExportActivity(@NonNull String id, @NonNull File destinationDirectory) {
        Intent intent = TangoIntents.createAdfExportIntent(id, destinationDirectory.getPath());
        startActivityForResult(intent, 0);
    }

    @Override
    public void showUploadProgressDialog() {
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
            progressDialog = null;
        }
    }

    @Override
    public void showDesyncProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progress_dialog_desync));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideDesyncProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }
}
