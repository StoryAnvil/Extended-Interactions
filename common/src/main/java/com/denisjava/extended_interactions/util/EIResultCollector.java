package com.denisjava.extended_interactions.util;

import com.denisjava.extended_interactions.impl.EIResultImpl;

import java.util.ArrayList;

public class EIResultCollector extends ArrayList<EIResultImpl.Result> {
    public void offer(EIResultImpl.Result result) {
        if (result instanceof EIResultImpl.Empty) return;
        add(result);
    }
}
