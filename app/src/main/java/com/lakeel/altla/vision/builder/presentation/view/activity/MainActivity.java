package com.lakeel.altla.vision.builder.presentation.view.activity;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.app.MyApplication;
import com.lakeel.altla.vision.builder.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.view.fragment.MainFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.SignInFragment;
import com.projecttango.tangosupport.TangoSupport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity
        implements ActivityScopeContext, SignInFragment.OnShowMainFragmentListener,
                   NavigationView.OnNavigationItemSelectedListener {

    private static final List<TangoCoordinateFramePair> FRAME_PAIRS;

    @Inject
    Tango mTango;

    @Inject
    TangoUx mTangoUx;

    @Inject
    TangoUpdateDispatcher mTangoUpdateDispatcher;

    @Inject
    TangoConfig mTangoConfig;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private ActivityComponent mActivityComponent;

    static {
        FRAME_PAIRS = new ArrayList<>();
        FRAME_PAIRS.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                     TangoPoseData.COORDINATE_FRAME_DEVICE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // NOTE:
        //
        // Any injection must be done before super.onCreate()
        // because fragments are already attached to an activity when they are resumed or instant-run.
        mActivityComponent = MyApplication.getApplicationComponent(this)
                                          .activityComponent(new ActivityModule(this));
        mActivityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        SignInFragment fragment = SignInFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // NOTE:
        //
        // 現状、TangoUX を用いるとデバッグ モードでは起動しなくなる。
        // これは、TangoUxLayout の配置の有無ではなく、TangoUx#start の実行により発生する。
        // このため、開発効率のために TangoUX を OFF にする場合には、TangoUx#start も止めなければならない。
//        mTangoUx.start(new TangoUx.StartParams());

        mTango.connectListener(FRAME_PAIRS, mTangoUpdateDispatcher);
        mTango.connect(mTangoConfig);

        TangoSupport.initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mTango.disconnect();
        mTangoUx.stop();
    }

    @Override
    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    @Override
    public void onShowMainFragment() {
        MainFragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
        }

//        if (id == R.id.nav_tango_space) {
//            mFragmentController.showTangoSpaceAdListFragment();
//        } else if (id == R.id.nav_app_space) {
//            mFragmentController.showAppSpaceAdListFragment();
//        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
