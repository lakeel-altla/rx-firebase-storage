package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;

import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.tango.TangoUxListener;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class ActivityModule {

    private final AppCompatActivity activity;

    public ActivityModule(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public AppCompatActivity provideActivity() {
        return activity;
    }

    @Named(Names.ACTIVITY_CONTEXT)
    @ActivityScope
    @Provides
    public Context provideContext() {
        return activity;
    }

    @ActivityScope
    @Provides
    public ContentResolver provideContentResolver() {
        return activity.getContentResolver();
    }

    @ActivityScope
    @Provides
    public Tango provideTango() {
        return new Tango(activity);
    }

    @ActivityScope
    @Provides
    public TangoUx provideTangoUx() {
        return new TangoUx(activity);
    }

    @ActivityScope
    @Provides
    public TangoUxListener provideTangoUxListener(TangoUx tangoUx) {
        return new TangoUxListener(tangoUx);
    }

    @ActivityScope
    @Provides
    public TangoUpdateDispatcher provideTangoUpdateDispatcher(TangoUxListener tangoUxListener) {
        TangoUpdateDispatcher dispatcher = new TangoUpdateDispatcher();
        // TangoUX の表示制御のためには Tango#connect 前にリスナを登録する必要がある。
//        dispatcher.getOnPoseAvailableListeners().add(tangoUxListener);
//        dispatcher.getOnTangoEventListeners().add(tangoUxListener);
        return dispatcher;
    }

    @ActivityScope
    @Provides
    public TangoConfig provideTangoConfig(Tango tango) {
        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);

        // NOTE: Low latency integration is necessary to achieve a precise alignment of
        // virtual objects with the RBG image and produce a good AR effect.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
        // Depth Perseption を有効化。
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
        config.putInt(TangoConfig.KEY_INT_DEPTH_MODE, TangoConfig.TANGO_DEPTH_MODE_POINT_CLOUD);
        // カラー カメラを有効化。
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
        // NOTE:
        //
        // トラッキング ロストからの復旧を検知するにはドリフト コレクションを有効にする。
        // ドリフトを正したポーズ データは、ベース フレーム TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION から
        // 任意のターゲット フレームに対してのフレーム ペアで利用可能。
        // 公式サンプルの java_plane_fitting_example では、
        // コメント文でターゲット フレームが TangoPoseData.COORDINATE_FRAME_DEVICE であるものとしているが、
        // 同サンプルにもあるように TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR としても利用可能。
        //
        // なお、ドリフト コレクションを有効にしなければ、COORDINATE_FRAME_AREA_DESCRIPTION をベースにしたフレーム ペアは
        // 機能しない模様。
        // モーション トラッキングを有効にすれば機能しそうなものだが、
        // KEY_BOOLEAN_MOTIONTRACKING を true に設定しただけでは機能しない。
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DRIFT_CORRECTION, true);

        return config;
    }
}
