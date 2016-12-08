package com.lakeel.altla.vision.builder.presentation.di.component;

import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.builder.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.builder.presentation.di.module.GoogleApiModule;
import com.lakeel.altla.vision.builder.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.builder.presentation.view.fragment.MainFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.RegisterTextureFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.di.ActivityScope;

import dagger.Subcomponent;

/**
 * Defines the dagger component that manages objects per activity.
 */
@ActivityScope
@Subcomponent(modules = { ActivityModule.class,
                          GoogleApiModule.class,
                          AndroidRepositoryModule.class,
                          FirebaseRepositoryModule.class })
public interface ActivityComponent {

    /**
     * Injects objects into the specified activity.
     *
     * @param activity The activity.
     */
    void inject(MainActivity activity);

    /**
     * Injects objects into the specified fragment.
     *
     * @param fragment The fragment.
     */
    void inject(MainFragment fragment);

    /**
     * Injects objects into the specified fragment.
     *
     * @param fragment The fragment.
     */
    void inject(SignInFragment fragment);

    /**
     * Injects objects into the specified fragment.
     *
     * @param fragment The fragment.
     */
    void inject(RegisterTextureFragment fragment);
}
