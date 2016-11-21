package com.lakeel.altla.vision.builder.presentation.view.activity;

import com.lakeel.altla.vision.builder.presentation.di.component.ActivityComponent;

/**
 * Defines the context for objects per activity.
 */
public interface ActivityScopeContext {

    /**
     * Gets the {@link ActivityComponent} instance.
     *
     * @return The {@link ActivityComponent} instance.
     */
    ActivityComponent getActivityComponent();
}
