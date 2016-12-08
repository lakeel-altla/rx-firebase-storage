package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.TangoPermissionPresenter;
import com.lakeel.altla.vision.builder.presentation.view.TangoPermissionView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TangoPermissionFragment extends Fragment implements TangoPermissionView {

    @Inject
    TangoPermissionPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    private InteractionListener interactionListener;

    public static TangoPermissionFragment newInstance() {
        return new TangoPermissionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        interactionListener = InteractionListener.class.cast(context);

        ActivityScopeContext.class.cast(getContext()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tango_permission, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean isCanceled = (Activity.RESULT_CANCELED == resultCode);
        presenter.onTangoPermissionResult(isCanceled);
    }

    @Override
    public void showMainFragment() {
        interactionListener.onShowMainFragment();
    }

    @Override
    public void showAreaLearningPermissionRequiredSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_area_learning_permission_required, Snackbar.LENGTH_SHORT)
                .setAction(R.string.snackbar_action_request_permission, view -> presenter.onConfirmPermission())
                .show();
    }

    @Override
    public void startTangoPermissionActivity() {
        startActivityForResult(TangoIntents.createAdfLoadSaveRequestPermissionIntent(), 0);
    }

    public interface InteractionListener {

        void onShowMainFragment();
    }
}
