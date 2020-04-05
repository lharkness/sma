package com.leeharkness.sma;

import lombok.Builder;
import lombok.Value;

/**
 * Represents an exit status for the SMA application
 */
@Value
@Builder
public class SMAExitStatus {
    int value;

    public static final SMAExitStatus SUCCESS = SMAExitStatus.builder().value(0).build();
    public static final SMAExitStatus CANCELLED = SMAExitStatus.builder().value(1).build();
    public static final SMAExitStatus FAILURE = SMAExitStatus.builder().value(-1).build();
    public static final SMAExitStatus UNAUTHENTICATED = SMAExitStatus.builder().value(-2).build();
    public static final SMAExitStatus LOGIN_FAILURE = SMAExitStatus.builder().value(-3).build();
}
