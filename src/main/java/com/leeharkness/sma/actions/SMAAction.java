package com.leeharkness.sma.actions;

import com.leeharkness.sma.SMAExitStatus;
import lombok.NonNull;
import org.beryx.textio.TextIO;

import java.util.Set;

/**
 * The contract for all SMA Actions
 */
public interface SMAAction {
    /**
     * Used to execute the action
     * @param textIO the IO facility
     * @param args any arguments
     * @return the SMA Exit Status for the action
     */
    SMAExitStatus execute(final @NonNull TextIO textIO, final @NonNull String[] args);

    /**
     * Gets the strings which can invoke this action
     * @return the string which can invoke this action
     */
    Set<String> invocationStrings();

    /**
     * Determines if this action should terminate the application
     * @return true if this action should terminate the application
     */
    boolean shouldTerminate();
}
