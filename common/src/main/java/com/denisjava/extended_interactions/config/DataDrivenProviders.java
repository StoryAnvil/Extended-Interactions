package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.api.providers.EIBlockProvider;
import com.denisjava.extended_interactions.api.providers.EIEntityProvider;
import com.denisjava.extended_interactions.api.providers.EIResult;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.util.EIUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if <1.21.11 {
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
//? } else {
/*import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
*///? }
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class DataDrivenProviders {
    public static final Codec<ResourceLocation> LITERAL_OR_TAG_CODEC = Codec.STRING.comapFlatMap(DataDrivenProviders::readLOR, DataDrivenProviders::writeLOR);
    public static final Codec<BlockStatePredicate> BLOCK_STATE_PREDICATE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(BlockStatePredicate::properties),
            NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(BlockStatePredicate::nbt),
            ComponentSerialization.CODEC.optionalFieldOf("message").forGetter(BlockStatePredicate::message)
    ).apply(inst, BlockStatePredicate::new));
    public static final Codec<BlockProvider> BLOCK_PROVIDER_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LITERAL_OR_TAG_CODEC.fieldOf("subject").forGetter(BlockProvider::subjectId),
            BLOCK_STATE_PREDICATE_CODEC.listOf().fieldOf("if").forGetter(BlockProvider::predicate)
    ).apply(inst, BlockProvider::new));
    public static final Codec<EntityPredicate> ENTITY_PREDICATE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            NbtPredicate.CODEC.fieldOf("nbt").forGetter(EntityPredicate::nbt),
            ComponentSerialization.CODEC.optionalFieldOf("message").forGetter(EntityPredicate::message)
    ).apply(inst, EntityPredicate::new));
    public static final Codec<EntityProvider> ENTITY_PROVIDER_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LITERAL_OR_TAG_CODEC.fieldOf("subject").forGetter(EntityProvider::subjectId),
            ENTITY_PREDICATE_CODEC.listOf().fieldOf("if").forGetter(EntityProvider::predicate)
    ).apply(inst, EntityProvider::new));

    public record BlockStatePredicate(Optional<StatePropertiesPredicate> properties, Optional<NbtPredicate> nbt, Optional<Component> message) {
        public boolean test(Level level, BlockPos pos, BlockState state) {
            if (properties.isPresent() && !properties.get().matches(state)) return false;
            if (nbt.isPresent()) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity == null) return false;
                return nbt.get().matches(entity.saveWithFullMetadata(level.registryAccess()));
            }
            return true;
        }
    }

    public record BlockProvider(ResourceLocation subjectId, List<BlockStatePredicate> predicate) implements ProviderRegistrar {
        public EIBlockProvider build(final DataDrivenInteraction interaction) {
            if (predicate().isEmpty()) {
                return (collector, level, user, pos, state) -> {
                    interaction.test(user);
                    collector.add(EIResult.success(interaction));
                };
            }
            return (collector, level, user, pos, state) -> {
                for (BlockStatePredicate predicate : predicate) {
                    if (!predicate.test(level, pos, state)) {
                        collector.add(EIResult.fail(interaction).addCodelessReason(predicate.message.orElse(null)));
                        return;
                    }
                }
                interaction.test(user);
                collector.add(EIResult.success(interaction));
            };
        }

        @Override
        public void registerProvider(DataDrivenInteraction interaction) {
            ExtendedInteractionsImpl.BLOCK_PROVIDERS.register(subjectId, build(interaction));
        }
    }

    public record EntityPredicate(NbtPredicate nbt, Optional<Component> message) { }

    public record EntityProvider(ResourceLocation subjectId, List<EntityPredicate> predicate) implements ProviderRegistrar {
        public EIEntityProvider build(final DataDrivenInteraction interaction) {
            if (predicate.isEmpty())
                return (collector, level, user, entity) -> {
                    interaction.test(user);
                    collector.add(EIResult.success(interaction));
                };
            return (collector, level, user, entity) -> {
                Tag tag = EIUtils.save(entity);
                for (EntityPredicate predicate : predicate) {
                    if (!predicate.nbt.matches(tag)) {
                        collector.add(EIResult.fail(interaction).addCodelessReason(predicate.message.orElse(null)));
                        return;
                    }
                }
                interaction.test(user);
                collector.add(EIResult.success(interaction));
            };
        }

        @Override
        public void registerProvider(DataDrivenInteraction interaction) {
            ExtendedInteractionsImpl.ENTITY_PROVIDERS.register(subjectId, build(interaction));
        }
    }

    private static String writeLOR(ResourceLocation rl) {
        if (rl.getNamespace().equals("tags")) {
            int d = rl.getPath().indexOf('/');
            if (d == -1) return "#minecraft:" + rl.getPath();
            String namespace = rl.getPath().substring(0, d);
            String path = rl.getPath().substring(d + 1);
            return '#' + namespace + ':' + path;
        }
        return rl.toString();
    }
    private static DataResult<ResourceLocation> readLOR(String s) {
        if (s.startsWith("#")) {
            DataResult<ResourceLocation> rl = ResourceLocation.read(s.substring(1));
            if (rl.isError()) return rl;
            ResourceLocation r = rl.getOrThrow();
            return DataResult.success(ResourceLocation.fromNamespaceAndPath("tags", r.getNamespace() + '/' + r.getPath()));
        }
        return ResourceLocation.read(s);
    }
    public interface ProviderRegistrar {
        void registerProvider(DataDrivenInteraction interaction);
    }
}
