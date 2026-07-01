package com.denisjava.extended_interactions.api;

import dev.isxander.yacl3.api.OptionGroup;

public interface EIYACLConfigFactory {
    /**
     * Creates YACL {@link OptionGroup.Builder}.<br>
     * You can invoke this method only once per plugin!<br>
     * Do <b>NOT</b> build provided builder.
     */
    OptionGroup.Builder create();
}
