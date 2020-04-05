package com.leeharkness.sma;

import com.amazonaws.services.cognitoidentity.model.Credentials;
import lombok.Data;

import java.security.PublicKey;

/**
 * Application Context data
 */
@Data
public class ApplicationContext {
    private PublicKey publicKey;
    private String userName;
    private Credentials credentials;
}
