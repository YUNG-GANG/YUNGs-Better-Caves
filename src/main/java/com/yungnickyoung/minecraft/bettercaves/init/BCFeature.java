package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BCFeature {
    private static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, BCSettings.MOD_ID);
    public static final CarverFeature BETTERCAVES_CARVER = new CarverFeature(NoFeatureConfig::deserialize);
    public static final RegistryObject<Feature<?>> BETTERCAVES_CARVER_FEATURE = FEATURES.register("bettercave", () -> BETTERCAVES_CARVER);

    /**
     * Register
     */
    public static void init() {
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::configChanged);
        MinecraftForge.EVENT_BUS.addListener(BCFeature::worldUnload);
    }

    /**
     * Iterates over all biomes, removing their carvers and adding the Better Caves feature
     * (which wraps all the pre-existing carvers) to the front of their feature lists
     */
    public static void commonSetup(FMLCommonSetupEvent event) {
        BetterCaves.LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Get all registered biomes
        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();

        // Replace biome carvers with Better Caves carvers
        for (Map.Entry<ResourceLocation, Biome> e : biomesList) {
            Biome biome = e.getValue();

            // Save all pre-existing carvers for biome.
            // These will be used in dimensions where Better Caves is not whitelisted.
            List<ConfiguredCarver<?>> defaultAirCarvers = biome.getCarvers(GenerationStage.Carving.AIR);
            List<ConfiguredCarver<?>> defaultLiquidCarvers = biome.getCarvers(GenerationStage.Carving.LIQUID);
            BetterCaves.defaultBiomeAirCarvers.put(biome.getClass(), defaultAirCarvers);
            BetterCaves.defaultBiomeLiquidCarvers.put(biome.getClass(), defaultLiquidCarvers);

            // Remove carvers now that they have been saved.
            biome.getCarvers(GenerationStage.Carving.AIR).clear();
            biome.getCarvers(GenerationStage.Carving.LIQUID).clear();

            // Use Access Transformer to make features field public so we can put our carver
            // at the front of the list to give it guaranteed priority.
            biome.features.get(GenerationStage.Decoration.RAW_GENERATION).add(0, new ConfiguredFeature<>(new CarverFeature(NoFeatureConfig::deserialize), new NoFeatureConfig()));
        }
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    public static void worldUnload(WorldEvent.Unload event) {
        BetterCaves.LOGGER.debug(String.format("Unloading world: %s | %s", event.getWorld().getSeed(), event.getWorld().getDimension().getType().getId()));
        BetterCaves.activeCarversMap.remove(event.getWorld().getDimension().getType().getId());
    }

    public static void configChanged(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        // Bake global values when they change
        if (config.getSpec() == Configuration.SPEC) {
            String whitelistedIDsString = Configuration.whitelistedDimensionIDs.get();
            int strLen = whitelistedIDsString.length();
            if (strLen < 2 || whitelistedIDsString.charAt(0) != '[' || whitelistedIDsString.charAt(strLen - 1) != ']') {
                BetterCaves.LOGGER.error("INVALID VALUE FOR SETTING 'Whitelisted Dimension IDs'. Using empty list instead...");
                BetterCaves.whitelistedDimensions = Lists.newArrayList();
                return;
            }

            String[] idStringArray = whitelistedIDsString.substring(1, strLen - 1).split(",\\s*");
            List<Integer> whitelistedIDs = Lists.newArrayList();
            for (String s : idStringArray) {
                try {
                    int dimensionId = Integer.parseInt(s);
                    whitelistedIDs.add(dimensionId);

                } catch (NumberFormatException e) {
                    BetterCaves.LOGGER.error(String.format("INVALID DIMENSION ID: %s - Skipping...", s));
                }
            }
            BetterCaves.whitelistedDimensions = whitelistedIDs;
        }
    }
}
