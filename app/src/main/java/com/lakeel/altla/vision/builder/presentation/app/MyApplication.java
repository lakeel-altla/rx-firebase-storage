package com.lakeel.altla.vision.builder.presentation.app;

import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.BuildConfig;
import com.lakeel.altla.vision.builder.presentation.di.component.ApplicationComponent;
import com.lakeel.altla.vision.builder.presentation.di.component.DaggerApplicationComponent;
import com.lakeel.altla.vision.builder.presentation.di.module.ApplicationModule;
import com.squareup.leakcanary.LeakCanary;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Defines our application class.
 */
public final class MyApplication extends MultiDexApplication {

    private ApplicationComponent applicationComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // LeakCanary
        LeakCanary.install(this);

        // Dagger 2
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        // Enable the offline feature of Firebase Database.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Rajawali
//        RajLog.setDebugEnabled(true);

        // Altla Log
        LogFactory.setDebug(BuildConfig.DEBUG);
    }

    /**
     * Gets the {@link ApplicationComponent} instance that is managed in the specified activity.
     *
     * @param activity The activity that manages the {@link ApplicationComponent} instance.
     * @return The {@link ApplicationComponent} instance that is managed in the specified activity.
     */
    public static ApplicationComponent getApplicationComponent(@NonNull Activity activity) {
        return ((MyApplication) activity.getApplication()).applicationComponent;
    }
}
