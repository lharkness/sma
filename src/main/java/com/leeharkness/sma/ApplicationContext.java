package com.leeharkness.sma;

import lombok.Data;

import java.security.PublicKey;

/**
 * Application Context data
 */
@Data
public class ApplicationContext {

    private PublicKey publicKey;
    private String token;
    private String userName;

}
