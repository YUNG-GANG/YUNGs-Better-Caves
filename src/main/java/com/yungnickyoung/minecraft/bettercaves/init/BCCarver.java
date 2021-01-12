package com.yungnickyoung.minecraft.bettercaves.init;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.EmptyCarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BCCarver {
    public static final WorldCarver<EmptyCarverConfig> BETTERCAVES_CARVER = new BetterCavesCarver();
    public static final ConfiguredCarver<EmptyCarverConfig> CONFIGURED_BETTERCAVES_CARVER = new ConfiguredCarver<EmptyCarverConfig>(BETTERCAVES_CARVER, EmptyCarverConfig.field_236238_c_);

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(WorldCarver.class, BCCarver::registerCarver);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, BCCarver::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(BCCarver::worldUnload);
    }

    public static void registerCarver(final RegistryEvent.Register<WorldCarver<?>> event) {
        event.getRegistry().register(BETTERCAVES_CARVER.setRegistryName(new ResourceLocation(BCSettings.MOD_ID, "bettercaves")));
        Registry.register(WorldGenRegistries.CONFIGURED_CARVER, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), CONFIGURED_BETTERCAVES_CARVER);
    }

    /**
     * Replaces biome's carvers with Better Caves carver.
     * Any existing carvers are stored for later use in dimensions where BC is disabled.
     */
    private static void onBiomeLoad(BiomeLoadingEvent event) {
        // Save all pre-existing carvers for biome.
        // These will be used in dimensions where Better Caves is not whitelisted.
        List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = new ArrayList<>(event.getGeneration().getCarvers(GenerationStage.Carving.AIR));
        List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = new ArrayList<>(event.getGeneration().getCarvers(GenerationStage.Carving.LIQUID));
        if (event.getName() != null) {
            String biomeName = event.getName().toString();
            BetterCaves.defaultBiomeAirCarvers.put(biomeName, defaultAirCarvers);
            BetterCaves.defaultBiomeLiquidCarvers.put(biomeName, defaultLiquidCarvers);
        } else {
            BetterCaves.LOGGER.error("Found missing name when loading biome. This shouldn't happen! Aborting...");
            return;
        }

        // Clear the biome's carvers
        event.getGeneration().getCarvers(GenerationStage.Carving.AIR).clear();
        event.getGeneration().getCarvers(GenerationStage.Carving.LIQUID).clear();

        // Add Better Caves carver
        event.getGeneration().getCarvers(GenerationStage.Carving.AIR).add(() -> CONFIGURED_BETTERCAVES_CARVER); }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    public static void worldUnload(WorldEvent.Unload event) {
        BetterCaves.LOGGER.debug("UNLOADING WORLD");
        try {
            String key = Objects.requireNonNull(((World) event.getWorld()).getDimensionKey().getLocation()).toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }
}
