package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.api.EIBlockProvider;
import com.denisjava.extended_interactions.api.EIEntityProvider;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIResults;
import com.denisjava.extended_interactions.impl.ExtInteractionIcon;
import com.denisjava.extended_interactions.impl.ExtendedInteractionsImpl;
import com.denisjava.extended_interactions.util.EIUtils;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    public static final Codec<InteractionTemplate> INTERACTION_TEMPLATE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.fieldOf("icon").forGetter(InteractionTemplate::icon),
            BLOCK_PROVIDER_CODEC.listOf().fieldOf("blocks").forGetter(InteractionTemplate::blockProviders),
            ENTITY_PROVIDER_CODEC.listOf().fieldOf("entities").forGetter(InteractionTemplate::entityProviders)
    ).apply(inst, InteractionTemplate::new));

    public record InteractionTemplate(
            ItemStack icon,
            List<BlockProvider> blockProviders,
            List<EntityProvider> entityProviders
    ) {
        public Pair<DataDrivenInteraction, List<ProviderRegistrar>> build(String name, EIPlugin declaringPlugin) {
            DataDrivenInteraction interaction = new DataDrivenInteraction(
                    ResourceLocation.fromNamespaceAndPath("custom", name),
                    new ExtInteractionIcon.ItemStackIcon(icon),
                    declaringPlugin
            );
            List<ProviderRegistrar> registrars = Stream.concat(blockProviders.stream().map(e -> (ProviderRegistrar) e),
                    entityProviders.stream().map(e -> (ProviderRegistrar) e)).toList();
            return new Pair<>(interaction, registrars);
        }
    }

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
                return (level, user, pos, state) -> EIResults.success(interaction);
            }
            return (level, user, pos, state) -> {
                for (BlockStatePredicate predicate : predicate) {
                    if (!predicate.test(level, pos, state))
                        return EIResults.optionalFailure(interaction, predicate.message);
                }
                return EIResults.success(interaction);
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
                return (level, user, entity) -> EIResults.success(interaction);
            return (level, user, entity) -> {
                Tag tag = EIUtils.save(entity);
                for (EntityPredicate predicate : predicate) {
                    if (!predicate.nbt.matches(tag)) return EIResults.optionalFailure(interaction, predicate.message);
                }
                return EIResults.success(interaction);
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
        DataResult<ResourceLocation> read = ResourceLocation.read(s);
        if (read.isError()) return read;
        ResourceLocation rl = read.getOrThrow(); // should be fine
        if (rl.getNamespace().equals("tags")) {
            int d = rl.getPath().indexOf('/');
            if (d == -1) return DataResult.success(ResourceLocation.withDefaultNamespace(rl.getPath()));
            String namespace = rl.getPath().substring(0, d);
            String path = rl.getPath().substring(d + 1);
            return DataResult.success(ResourceLocation.fromNamespaceAndPath(namespace, path));
        }
        return DataResult.success(rl);
    }
    public interface ProviderRegistrar {
        void registerProvider(DataDrivenInteraction interaction);
    }
}
