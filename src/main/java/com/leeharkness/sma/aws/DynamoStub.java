package com.leeharkness.sma.aws;

import lombok.NonNull;

import java.util.Optional;

/**
 * Our interface to DynamoDB
 */
public class DynamoStub {

    /**
     * Queries for user name
     * @param userName the user name to query for
     * @return An Optional User containing the data in dynamo for this user name.  Optional.empty if none found
     */
    public Optional<Object> lookupUserName(final @NonNull String userName) {
        if (userName.equalsIgnoreCase("duplicate")) {
            return Optional.of(userName);
        }
        return Optional.empty();
    }

    /**
     * Queries for an email
     * @param email the user email to query for
     * @return An Optional User containing the data in dynamo for this email.  Optional.empty if none found
     */
    public Optional<Object> lookupUserEmail(final @NonNull String email) {
        if (email.equalsIgnoreCase("duplicate")) {
            return Optional.of(email);
        }
        return Optional.empty();
    }
}
