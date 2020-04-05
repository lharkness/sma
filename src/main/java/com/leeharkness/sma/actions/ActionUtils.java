package com.leeharkness.sma.actions;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

/**
 * Collection of methods common to multiple Actions
 */
@Slf4j
public class ActionUtils {

    /**
     * To prevent instantiation
     */
    private ActionUtils() {}

    /**
     * generates an MD5 hash of a given string
     * @param password the string to generate the hash for
     * @return the Optional hash.  Optional.empty if unable to generate
     */
    static public Optional<String> generatePasswordHash(final @NonNull String password) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            final byte[] passwordHashBytes = messageDigest.digest(password.getBytes());
            final Base64.Encoder base64Encoder = Base64.getEncoder();
            return Optional.of(base64Encoder.encodeToString(passwordHashBytes));
        }
        catch (final NoSuchAlgorithmException nsae) {
            log.error("No MD5 algorithm found", nsae);
            return Optional.empty();
        }
    }
}
