package com.denisjava.extended_interactions.api;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record InteractionArgument(String id, Optional<String> iconOverride) {
    public static final StreamCodec<RegistryFriendlyByteBuf, InteractionArgument> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, InteractionArgument::id,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), InteractionArgument::iconOverride,
            InteractionArgument::new
    );
}
