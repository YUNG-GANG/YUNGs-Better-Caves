package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.module.CarverModuleFabric;
import com.yungnickyoung.minecraft.bettercaves.module.ConfigModuleFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

public class BetterCavesFabric implements ModInitializer {

    static final ResourceKey<ConfiguredWorldCarver<?>> BETTER_CAVE_CARVER = ResourceKey.create(Registries.CONFIGURED_CARVER,
            BetterCavesCommon.id("better_cave"));

    static final ResourceKey<ConfiguredWorldCarver<?>> SURFACE_CAVE_CARVER = ResourceKey.create(Registries.CONFIGURED_CARVER,
            BetterCavesCommon.id("surface_cave"));

    @Override
    public void onInitialize() {
        BetterCavesCommon.init();
        ConfigModuleFabric.init();
        CarverModuleFabric.init();
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
                                .addCarver(GenerationStep.Carving.AIR, BETTER_CAVE_CARVER));
        BiomeModifications.create(BetterCavesCommon.id("add_surface_cave_carver"))
                .add(ModificationPhase.ADDITIONS,
                        biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_OVERWORLD),
                        modificationContext -> modificationContext.getGenerationSettings()
                                .addCarver(GenerationStep.Carving.AIR, SURFACE_CAVE_CARVER));
    }
}
