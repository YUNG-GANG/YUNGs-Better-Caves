package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;

public class BCFeature {
    public static final CarverFeature BETTERCAVES_FEATURE = new CarverFeature(NoFeatureConfig.field_236558_a_);
    public static final ConfiguredFeature<?, ?> CONFIGURED_BETTERCAVES_FEATURE = new ConfiguredFeature<>(BETTERCAVES_FEATURE, new NoFeatureConfig());

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::configChanged);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, BCFeature::registerFeature);
        MinecraftForge.EVENT_BUS.addListener(BCFeature::worldUnload);
    }

    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
        Registry.register(Registry.FEATURE, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), BETTERCAVES_FEATURE);
        Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), CONFIGURED_BETTERCAVES_FEATURE);
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    public static void worldUnload(WorldEvent.Unload event) {
        BetterCaves.LOGGER.debug("UNLOADING WORLD");
        try {
            String key = Objects.requireNonNull(((World) event.getWorld()).getDimensionKey().func_240901_a_()).toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }

    /**
     * Parses the whitelisted dimensions string and updates the stored values.
     */
    public static void configChanged(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();

        if (config.getSpec() == Configuration.SPEC) {
            String rawStringofList = Configuration.whitelistedDimensions.get();
            int strLen = rawStringofList.length();

            // Validate the string's format
            if (strLen < 2 || rawStringofList.charAt(0) != '[' || rawStringofList.charAt(strLen - 1) != ']') {
                BetterCaves.LOGGER.error("INVALID VALUE FOR SETTING 'Whitelisted Dimension IDs'. Using empty list instead...");
                BetterCaves.whitelistedDimensions = Lists.newArrayList();
                return;
            }

            // Parse string to list
            List<String> inputListOfDimensionStrings = Lists.newArrayList(rawStringofList.substring(1, strLen - 1).split(",\\s*"));

            // Parse list of strings, removing any entries that don't match existing dimension names
            List<String> whitelistedDimensions = Lists.newArrayList();
            whitelistedDimensions.addAll(inputListOfDimensionStrings);

            BetterCaves.whitelistedDimensions = whitelistedDimensions;
        }
    }
}
