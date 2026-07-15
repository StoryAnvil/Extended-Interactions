package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.api.providers.EIResult;
import org.jetbrains.annotations.ApiStatus;

public class ThrowableEIResult extends Exception {
    private final EIResult result;

    @ApiStatus.Internal
    public ThrowableEIResult(EIResult result) {
        super("");
        this.result = result;
    }

    public EIResult getResult() {
        return result;
    }
}
