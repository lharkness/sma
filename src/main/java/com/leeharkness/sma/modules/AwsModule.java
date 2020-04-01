package com.leeharkness.sma.modules;

import com.google.inject.AbstractModule;
import com.leeharkness.sma.aws.CognitoStub;
import com.leeharkness.sma.aws.DynamoStub;

/**
 * Configures all AWS assets
 */
public class AwsModule extends AbstractModule {
    protected void configure() {
        bind(DynamoStub.class).to(DynamoStub.class);
        bind(CognitoStub.class).to(CognitoStub.class);
    }
}
