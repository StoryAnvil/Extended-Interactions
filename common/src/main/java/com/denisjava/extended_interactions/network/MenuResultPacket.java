package com.denisjava.extended_interactions.network;

import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.api.providers.FailedResult;
import com.denisjava.extended_interactions.api.providers.SuccessfulResult;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.denisjava.extended_interactions.EICommon.id;

public record MenuResultPacket(List<SuccessfulResult> good, List<FailedResult> bad) implements CustomPacketPayload {
    public static MenuResultPacket create(List<EIResult> results) {
        Pair<List<SuccessfulResult>, List<FailedResult>> sorted = ExtendedInteractionsImpl.sort(results);
        return new MenuResultPacket(sorted.getFirst(), sorted.getSecond());
    }

    public static final Type<MenuResultPacket> TYPE = new Type<>(id("menu_response"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MenuResultPacket> STREAM_CODEC = StreamCodec.composite(
            ExtendedInteractionsImpl.SUCCESSFUL_STREAM_CODEC.apply(ByteBufCodecs.list()), MenuResultPacket::good,
            ExtendedInteractionsImpl.FAILED_STREAM_CODEC.apply(ByteBufCodecs.list()), MenuResultPacket::bad,
            MenuResultPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
