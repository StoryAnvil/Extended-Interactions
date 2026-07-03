package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.impl.EIResultImpl;

public class ThrowableEIResult extends Exception {
    private final EIResultImpl.Result result;

    public ThrowableEIResult(EIResultImpl.Result result) {
        super("");
        this.result = result;
    }

    public EIResultImpl.Result getResult() {
        return result;
    }
}
