//? if oracle_index {
package com.denisjava.extended_interactions.compat.oracleindex;

import com.denisjava.extended_interactions.EICommon;
import net.minecraft.resources.ResourceLocation;
import rearth.oracle.OracleClient;

public class OracleIndexCompatClient {
    public static boolean hasPageFor(ResourceLocation id) {
        return OracleClient.ITEM_LINKS.containsKey(id);
    }

    public static void openPageFor(ResourceLocation id) {
        OracleClient.ItemArticleRef ref = OracleClient.ITEM_LINKS.get(id);
        if (ref == null) {
            EICommon.LOG.warn("Failed to open Oracle Index page for item {}: no page associated", id);
            return;
        }
        OracleClient.openScreen(ref.wikiId(), ref.linkTarget(), null);
    }
}
//? }