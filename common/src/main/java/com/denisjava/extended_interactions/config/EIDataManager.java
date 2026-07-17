package com.denisjava.extended_interactions.config;

import com.denisjava.extended_interactions.EICommon;
import com.denisjava.extended_interactions.api.EIPlugin;
import com.denisjava.extended_interactions.api.EIPluginClass;
import com.denisjava.extended_interactions.api.InteractionRegistrar;
import com.denisjava.extended_interactions.api.ProviderRegistrar;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@EIPluginClass
public class EIDataManager implements EIPlugin {
    private final List<Pair<DataDrivenInteraction, List<DataDrivenProviders.ProviderRegistrar>>> interactions = new ArrayList<>();

    @Override
    public void init() {
        File custom = new File(EICommon.getPlatform().getConfigDir(), "custom");
        if (!custom.exists() && !custom.mkdirs())
            EICommon.LOG.error("Failed to create extended interactions config directory {}", custom);
        File[] files = custom.listFiles();
        if (files == null) return;

        RegistryAccess.ImmutableRegistryAccess access = new RegistryAccess.ImmutableRegistryAccess(BuiltInRegistries.REGISTRY.stream().toList());
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);

        for (File file : files) {
            try {
                if (!file.getName().endsWith(".json")) continue;
                parseFile(ops, file);
            } catch (IOException e) {
                EICommon.LOG.error("Failed to parse config file {}", file);
                EICommon.LOG.error("Caused by", e);
            }
        }

        if (new File(EICommon.getPlatform().getConfigDir(), "debug").exists()) {
            EICommon.LOG.info("=== BEGIN EIDATAMANAGER DEBUG ==");
            for (Pair<DataDrivenInteraction, List<DataDrivenProviders.ProviderRegistrar>> interaction : interactions) {
                EICommon.LOG.info("Interaction: {}", interaction.getFirst());
                for (DataDrivenProviders.ProviderRegistrar provider : interaction.getSecond()) {
                    EICommon.LOG.info("Provider: {}", provider);
                }
            }
            EICommon.LOG.info("=== END EIDATAMANAGER DEBUG ==");
        }
    }

    private void parseFile(RegistryOps<JsonElement> ops, File file) throws IOException {
        try (FileInputStream io = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(io)) {
            JsonElement element = JsonParser.parseReader(reader);
            try {
                DataDrivenInteraction.InteractionTemplate template = DataDrivenInteraction.INTERACTION_TEMPLATE_CODEC.decode(ops, element)
                        .getOrThrow(IOException::new).getFirst();
                String name = file.getName().substring(0, file.getName().length() - 5);
                interactions.add(template.build(name, this));
            } catch (Exception e) {
                throw new IOException("Failed to decode template", e);
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new IOException("Failed to parse JSON", e);
        }
    }

    @Override
    public void registerProviders(@NotNull ProviderRegistrar providerRegistrar) {
        for (Pair<DataDrivenInteraction, List<DataDrivenProviders.ProviderRegistrar>> interaction : interactions) {
            DataDrivenInteraction i = interaction.getFirst();
            for (DataDrivenProviders.ProviderRegistrar registrar : interaction.getSecond()) {
                registrar.registerProvider(i);
            }
        }
    }

    @Override
    public void registerInteractions(@NotNull InteractionRegistrar registrar) {
        for (Pair<DataDrivenInteraction, List<DataDrivenProviders.ProviderRegistrar>> interaction : interactions) {
            registrar.register(interaction.getFirst());
        }
    }

    @Override
    public @NotNull String getDeclaringModId() {
        return EICommon.MOD_ID;
    }

    @Override
    public @NotNull ResourceLocation getUID() {
        return EICommon.id("custom");
    }

    @Override
    public int loadingPriority() {
        // we need to load AFTER other plugins.
        // to let them register data driven actions
        return 0;
    }
}
