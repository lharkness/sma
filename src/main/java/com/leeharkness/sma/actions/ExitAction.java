package com.leeharkness.sma.actions;

import com.google.inject.Inject;
import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import lombok.NonNull;
import org.beryx.textio.TextIO;

import java.util.Set;

/**
 * Used to exit the application
 */
public class ExitAction extends BaseUnauthenticatedAction {

    /**
     * Initialization constructor
     * @param applicationContext the application context
     */
    @Inject
    public ExitAction(final @NonNull ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public SMAExitStatus execute(TextIO textIO, String[] args) {
        return SMAExitStatus.SUCCESS;
    }

    @Override
    public Set<String> invocationStrings() {
        return Set.of("exit", "e", "quit", "q");
    }

    @Override
    public boolean shouldTerminate() {
        return true;
    }
}
