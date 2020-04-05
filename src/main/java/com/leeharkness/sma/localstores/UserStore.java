package com.leeharkness.sma.localstores;

import com.google.inject.Inject;
import com.leeharkness.sma.ApplicationContext;
import lombok.NonNull;

/**
 * Our window into the local User Store (user's contacts)
 */
public class UserStore {

    private ApplicationContext applicationContext;

    /**
     * Initialization ctor
     * @param applicationContext our application context
     */
    @Inject
    public UserStore(final @NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Syncs the local contact DB with the remote contacts
     */
    public void sync() {

    }

}
