package com.leeharkness.sma.actions;

import com.google.inject.Inject;
import com.leeharkness.sma.ApplicationContext;
import lombok.NonNull;

/**
 * The base action that all actions derive from.
 */
public abstract class BaseSMAAction implements SMAAction {

    private ApplicationContext applicationContext;

    /**
     * Initialization constructor
     * @param applicationContext the Application context
     */
    @Inject
    public BaseSMAAction(final @NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Determines whether this action should terminate the application.
     * @return true if this action should terminate the application, false if not.
     */
    @Override
    public boolean shouldTerminate() {
        return false;
    }

    /**
     * Returns a reference to the application context
     * @return a reference to the application context
     */
    ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
