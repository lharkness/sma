package com.leeharkness.sma.actions;

import com.google.inject.Inject;
import com.leeharkness.sma.ApplicationContext;
import lombok.NonNull;

/**
 * Represents all SMAActions which do not need to be authenticated.
 */
public abstract class BaseUnauthenticatedAction extends BaseSMAAction {

    /**
     * Initialization constructor
     * @param applicationContext the application context
     */
    @Inject
    public BaseUnauthenticatedAction(final @NonNull ApplicationContext applicationContext) {
        super(applicationContext);
    }

}
