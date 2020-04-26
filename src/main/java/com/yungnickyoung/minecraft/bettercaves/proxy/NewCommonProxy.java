package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHelper;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.CarverFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewCommonProxy {
    private static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, BCSettings.MOD_ID);
    public static final CarverFeature BETTERCAVES_CARVER = new CarverFeature(NoFeatureConfig::deserialize);
    public static final RegistryObject<Feature<?>> BETTERCAVES_CARVER_FEATURE = FEATURES.register("bettercave", () -> BETTERCAVES_CARVER);

    public static void init() {
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NewCommonProxy::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NewCommonProxy::configChanged);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        BetterCaves.LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Get all registered biomes
        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();

        // Replace biome carvers with Better Caves carvers
        for (Map.Entry e : biomesList) {
            Biome biome = (Biome)e.getValue();

            List<ConfiguredCarver<?>> defaultAirCarvers = biome.getCarvers(GenerationStage.Carving.AIR);
            List<ConfiguredCarver<?>> defaultLiquidCarvers = biome.getCarvers(GenerationStage.Carving.LIQUID);
            BetterCaves.defaultBiomeAirCarvers.put(biome.getClass(), defaultAirCarvers);
            BetterCaves.defaultBiomeLiquidCarvers.put(biome.getClass(), defaultLiquidCarvers);

            // use AT to make features field public so we can put our carver at the front of the list to give it priority.
            biome.features.get(GenerationStage.Decoration.RAW_GENERATION).add(0, new ConfiguredFeature<>(new CarverFeature(NoFeatureConfig::deserialize), new NoFeatureConfig()));
        }
    }

    public static void configChanged(ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();

        // Rebake the config settings when they change
        if (config.getSpec() == Configuration.SPEC) {
            ConfigHelper.bakeClient(config);
            BetterCaves.LOGGER.debug("Baked client config");
        }
    }
}
