package com.denisjava.extended_interactions.impl;

import com.denisjava.extended_interactions.api.EIYACLConfigFactory;
import dev.isxander.yacl3.api.OptionGroup;

public class EIYACLConfigFactoryImpl implements EIYACLConfigFactory {
    public OptionGroup.Builder currentBuilder = null;

    @Override
    public OptionGroup.Builder create() {
        if (currentBuilder != null) throw new IllegalStateException("Your plugin already created OptionGroup builder");
        currentBuilder = OptionGroup.createBuilder();
        return currentBuilder;
    }
}
