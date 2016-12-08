package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.di.ActivityScope;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import dagger.Module;
import dagger.Provides;

@Module
public final class GoogleApiModule {

    private static final Log LOG = LogFactory.getLog(GoogleApiModule.class);

    @ActivityScope
    @Provides
    public GoogleApiClient provideGoogleApiClient(AppCompatActivity activity, GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, connectionResult -> {
                    LOG.e("Google API connection error occured: %s", connectionResult);
                    Toast.makeText(activity, R.string.toast_google_api_client_connection_failed, Toast.LENGTH_LONG)
                         .show();
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}
