package com.lakeel.altla.vision.builder.presentation.view.activity;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.app.MyApplication;
import com.lakeel.altla.vision.builder.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.view.fragment.MainFragment;
import com.projecttango.tangosupport.TangoSupport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class MainActivity extends AppCompatActivity implements ActivityScopeContext {

    private static final List<TangoCoordinateFramePair> FRAME_PAIRS;

    @Inject
    Tango mTango;

    @Inject
    TangoUx mTangoUx;

    @Inject
    TangoUpdateDispatcher mTangoUpdateDispatcher;

    @Inject
    TangoConfig mTangoConfig;

    private ActivityComponent mActivityComponent;

    static {
        FRAME_PAIRS = new ArrayList<>();
        FRAME_PAIRS.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                     TangoPoseData.COORDINATE_FRAME_DEVICE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Dagger
        //
        // Instant run や復帰時には既に Fragment が Activity にアタッチされているため、
        // Fragment から利用するオブジェクトは super.onCreate よりも前に初期化する必要がある。
        mActivityComponent = MyApplication.getApplicationComponent(this)
                                          .activityComponent(new ActivityModule(this));
        mActivityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment fragment = MainFragment.newInstance();
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
}
