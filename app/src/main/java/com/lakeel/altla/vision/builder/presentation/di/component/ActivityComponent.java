package com.lakeel.altla.vision.builder.presentation.di.component;

import com.lakeel.altla.vision.builder.presentation.di.ActivityScope;
import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.di.module.PresenterModule;
import com.lakeel.altla.vision.builder.presentation.di.module.RepositoryModule;
import com.lakeel.altla.vision.builder.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.builder.presentation.view.fragment.MainFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.SignInFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = { ActivityModule.class, PresenterModule.class, RepositoryModule.class })
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(MainFragment fragment);

    void inject(SignInFragment fragment);
}
