package com.leeharkness.sma.localstores;

import com.google.inject.Inject;
import com.leeharkness.sma.ApplicationContext;
import lombok.NonNull;

/**
 * Our window to our local message store
 */
public class MessageStore {
    private ApplicationContext applicationContext;

    /**
     * Initialization ctor
     * @param applicationContext our application context
     */
    @Inject
    public MessageStore(final @NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Syncs the local message store with the SQS queue.
     */
    public void sync() {

    }

}
