package com.leeharkness.sma.aws;

import com.amazonaws.services.cognitoidentity.model.Credentials;
import lombok.NonNull;

import java.util.Optional;

/**
 * Our interface to Cognito
 */
public class CognitoStub {

    /**
     * Used to sign up a new user
     * @param userName the username
     * @param password the user's password
     * @param email the user's email
     * @param publicKey the user's public key
     * @return true if the user was successfully registered, false if not
     */
    public boolean signUpUser(final @NonNull String userName, final @NonNull String password,
                              final @NonNull String email, final @NonNull String publicKey) {
        return true;
    }

    /**
     * Used to confirm a user's registration
     * @param userName the username
     * @param confirmationCode the confirmation code
     * @return true if the user was confirmed false if not
     */
    public boolean confirmUser(final @NonNull String userName, final @NonNull String confirmationCode) {
        return true;
    }

    /**
     * Obtains the AWS credentials for a given userName/passwordHash combo.
     * @param userName The userName to log in
     * @param passwordHash the password hash to use
     * @return an Optional Credentials reference - empty of not logged in
     */
    public Optional<Credentials> login(final @NonNull String userName, final @NonNull String passwordHash) {
        return Optional.empty();
    }
}
