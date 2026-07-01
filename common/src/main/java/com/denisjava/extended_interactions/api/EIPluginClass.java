package com.denisjava.extended_interactions.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EIPluginClass {
    /**
     * List of mod ids required for this plugin to load.<br>
     * <code>*dev</code> is used to require IDE environment
     */
    String[] requiredMods() default {};
}
