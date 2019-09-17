package com.yungnickyoung.minecraft.bettercaves;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHelper;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.world.WorldCarverBC;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Subscribe to events from the MOD EventBus that should be handled on both PHYSICAL sides in this class
 *
 * @author Cadiboo
 * @author YUNGNICKYOUNG
 */
@Mod.EventBusSubscriber(modid = Settings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEventSubscriber {

    public static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID + " Mod Event Subscriber");
    static final WorldCarverBC BETTER_CAVE = new WorldCarverBC(ProbabilityConfig::deserialize, 256);
    private static ConfiguredCarver<ProbabilityConfig> confCarver = Biome.createCarver(BETTER_CAVE, new ProbabilityConfig(1));

    /**
     * Rebakes config changes for Better Caves
     * @param event mod config event
     */
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();

        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHelper.bakeClient(config);
            LOGGER.debug("Baked client config");
        }
    }

    /**
     * Replaces all biomes' carvers with a new variable containing Better Caves carvers.
     * Does not include nether and end biomes.
     * @param event common setup event
     */
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Get all registered biomes
        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();

        // Replace biome carvers with Better Caves carvers
        for (Map.Entry e : biomesList) {
            Biome b = (Biome)e.getValue();

            // Exclude Nether and End biomes
            if (b == Biomes.NETHER
                    || b == Biomes.THE_END
                    || b == Biomes.END_BARRENS
                    || b == Biomes.END_HIGHLANDS
                    || b == Biomes.END_MIDLANDS
                    || b == Biomes.SMALL_END_ISLANDS)
                continue;

            setCarvers(b, confCarver);
        }
    }

    /**
     * Helper method used to replace a biome's 'carvers' var with Better Caves carvers, thereby overriding
     * vanilla cave generation.
     * @param biomeIn Biome to override
     * @param carver The carver replacing the default carvers
     */
    private static void setCarvers(Biome biomeIn, ConfiguredCarver<ProbabilityConfig> carver) {
        Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();

        // Add Better Caves as regular carver
        carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
                Lists.newArrayList()
        ).add(carver);

        // Add Better Caves as liquid carver for ocean biomes
        carvers.computeIfAbsent(GenerationStage.Carving.LIQUID, (p_203604_0_) ->
                Lists.newArrayList()
        ).add(carver);

        // Generate ravines depending on user config option
        if (BetterCavesConfig.enableVanillaRavines) {
            // Add regular ravines
            carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
                    Lists.newArrayList()
            ).add(Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
        }

        // Generate underwater ravines depending on user config option
        if (BetterCavesConfig.enableVanillaUnderwaterRavines) {
            // Add ravines under oceans (these spawn separately from normal ravines in 1.14)
            carvers.computeIfAbsent(GenerationStage.Carving.LIQUID, (p_203604_0_) ->
                    Lists.newArrayList()
            ).add(Biome.createCarver(WorldCarver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
        }

        // Attempt to replace biome's 'carvers' field with the list we've created, overriding vanilla cave gen
        try {
            final Field field = biomeIn.getClass().getDeclaredField("carvers");
            field.setAccessible(true);
            field.set(biomeIn, carvers);
            LOGGER.error("Successfully updated 'carvers' field for Biome " + biomeIn.getDisplayName());
        } catch (NoSuchFieldException e) {
            Class superclass = biomeIn.getClass().getSuperclass();
            if (superclass == null) {
                LOGGER.error("Error getting 'carvers' field for biome " + biomeIn.getClass().getName()+ ": " + e);
            } else {
                try {
                    final Field field = superclass.getDeclaredField("carvers");
                    field.setAccessible(true);
                    field.set(biomeIn, carvers);
                    LOGGER.error("Successfully updated 'carvers' field for Biome " + biomeIn.getDisplayName() + " from parent class (probably Biome)");
                } catch (Exception e2) {
                    LOGGER.error("Error getting 'carvers' field for biome " + biomeIn.getClass().getName()+ ": " + e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error getting 'carvers' field for biome " + biomeIn.getClass().getName()+ ": " + e);
        }
    }
}
