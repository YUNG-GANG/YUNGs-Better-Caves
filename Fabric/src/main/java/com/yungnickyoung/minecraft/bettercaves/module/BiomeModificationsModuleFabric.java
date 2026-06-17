package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

public class BiomeModificationsModuleFabric {
    private static final ResourceKey<ConfiguredWorldCarver<?>> BETTER_CAVE_CARVER_KEY = ResourceKey.create(Registries.CONFIGURED_CARVER,
            BetterCavesCommon.id("better_cave"));

    private static final ResourceKey<ConfiguredWorldCarver<?>> SURFACE_CAVE_CARVER_KEY = ResourceKey.create(Registries.CONFIGURED_CARVER,
            BetterCavesCommon.id("surface_cave"));

    public static void init() {
        BiomeModifications.create(BetterCavesCommon.id("remove_vanilla_carvers"))
                .add(ModificationPhase.REMOVALS,
                        biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_OVERWORLD),
                        modificationContext -> {
                            modificationContext.getGenerationSettings().removeCarver(Carvers.CAVE);
                            modificationContext.getGenerationSettings().removeCarver(Carvers.CAVE_EXTRA_UNDERGROUND);
                        });
        BiomeModifications.create(BetterCavesCommon.id("add_better_carver"))
                .add(ModificationPhase.ADDITIONS,
                        biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_OVERWORLD),
                        modificationContext -> modificationContext.getGenerationSettings()
                                .addCarver(BETTER_CAVE_CARVER_KEY));
        BiomeModifications.create(BetterCavesCommon.id("add_surface_cave_carver"))
                .add(ModificationPhase.ADDITIONS,
                        biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_OVERWORLD),
                        modificationContext -> modificationContext.getGenerationSettings()
                                .addCarver(SURFACE_CAVE_CARVER_KEY));
    }
}
