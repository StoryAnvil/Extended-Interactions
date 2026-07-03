package com.denisjava.extended_interactions.util;

import net.minecraft.client.KeyMapping;

import java.util.Map;

public interface EIKeyMapping {
    Map<String, KeyMapping> ei$getAll();
    void ei$addClick();
}
