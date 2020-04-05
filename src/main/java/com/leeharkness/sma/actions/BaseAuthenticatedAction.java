package com.leeharkness.sma.actions;

import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.google.inject.Inject;
import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import com.leeharkness.sma.SMAUnauthenticatedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.beryx.textio.TextIO;

import java.util.Date;

/**
 * The base action for all actions which require Authentication
 */
@Slf4j
public abstract class BaseAuthenticatedAction extends BaseSMAAction {

    /**
     * Initialization constructor
     * @param applicationContext the application context
     */
    @Inject
    public BaseAuthenticatedAction(final @NonNull ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public SMAExitStatus execute(final @NonNull TextIO textIO, final @NonNull String[] args) {
        try {
            authenticationCheck();
            return authenticatedExecute(textIO, args);
        }
        catch (SMAUnauthenticatedException sue) {
            log.error("Authentication error", sue);
            return SMAExitStatus.UNAUTHENTICATED;
        }
    }

    /**
     * Subclasses provide their work here
     * @param textIO the IO facility
     * @param args any arguments
     * @return the SMAExitStatus
     */
    public abstract SMAExitStatus authenticatedExecute(final @NonNull TextIO textIO, final @NonNull String[] args);

    /**
     * Does the authentication check
     * @throws SMAUnauthenticatedException if there is no authenticated user
     */
    private void authenticationCheck() throws SMAUnauthenticatedException {
        final Credentials credentials = getApplicationContext().getCredentials();
        if (credentials == null || credentials.getExpiration().after(new Date())) {
            throw new SMAUnauthenticatedException(getApplicationContext().getUserName());
        }
    }
}
