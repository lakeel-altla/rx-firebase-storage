package com.lakeel.altla.vision.builder.presentation.app;

import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.BuildConfig;
import com.lakeel.altla.vision.builder.presentation.di.component.ApplicationComponent;
import com.lakeel.altla.vision.builder.presentation.di.component.DaggerApplicationComponent;
import com.lakeel.altla.vision.builder.presentation.di.module.ApplicationModule;
import com.squareup.leakcanary.LeakCanary;

import org.rajawali3d.util.RajLog;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class MyApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // LeakCanary
        LeakCanary.install(this);

        // Dagger 2
        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        // Rajawali
        RajLog.setDebugEnabled(true);

        // Altla Log
        LogFactory.setDebug(BuildConfig.DEBUG);

        // Realm
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        // Delete Realm between app restarts.
//        Realm.deleteRealm(realmConfiguration);
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static ApplicationComponent getApplicationComponent(@NonNull Activity activity) {
        return ((MyApplication) activity.getApplication()).mApplicationComponent;
    }
}
