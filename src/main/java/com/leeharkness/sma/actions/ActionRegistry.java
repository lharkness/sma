package com.leeharkness.sma.actions;

import com.google.inject.Inject;
import lombok.NonNull;

import java.util.Optional;
import java.util.Set;

/**
 * Responsible for looking up Actions given an invocation string
  */
public class ActionRegistry {

    private Set<SMAAction> actions;

    /**
     * Initialization ctor.  Accepts a Set of SMAActions
     * @param actions The list of supported Actions
     */
    @Inject
    public ActionRegistry(final @NonNull Set<SMAAction> actions) {
        this.actions = actions;
    }

    /**
     * Returns the first Action that can handle the given requestString
     * @param requestString The user input that is asking for an action to be performed.  The format is
     *                      <action invocation string> parameter_1 parameter_2
     * @return An Optional SMAAction that handles the given request string.  Optional.empty if there are no
     * actions that handle the given request string.
     */
    public Optional<SMAAction> getActionFor(final @NonNull String requestString) {
        return actions.stream()
                .filter(a -> a.invocationStrings().contains(requestString.toLowerCase()))
                .findFirst();
    }
}
