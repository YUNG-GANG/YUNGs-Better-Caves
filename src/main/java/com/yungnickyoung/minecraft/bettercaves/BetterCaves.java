package com.yungnickyoung.minecraft.bettercaves;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.world.WorldCarverBC;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Entry point for Better Caves
 */
@Mod(Settings.MOD_ID)
public class BetterCaves {

    private static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID + " Entry Point");

    public BetterCaves() {
        LOGGER.error("CONSTRUCTOR");
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("COMMON SETUP EVENT");
        WorldCarver<ProbabilityConfig> wc = new WorldCarverBC(ProbabilityConfig::deserialize, 256);
        ConfiguredCarver<ProbabilityConfig> confCarver = Biome.createCarver(wc, new ProbabilityConfig(0));

        ForgeRegistries.BIOMES.getEntries()

        for (Field biomeField : Biomes.class.getFields()) {
            try {
                Biome biome = (Biome)biomeField.get(null);
                setCarvers(biome, confCarver);
            } catch (IllegalAccessException e) {
                LOGGER.error("NO ACCESS FOR BIOME RETRIEVAL");
            }
        }
    }

    public static void setCarvers(Biome biomeIn, ConfiguredCarver<ProbabilityConfig> carver) {
        Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();
        carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
                Lists.newArrayList()
        ).add(carver);

        try {
            final Field field = biomeIn.getClass().getDeclaredField("carvers");
            field.setAccessible(true);
            field.set(biomeIn, carvers);
            LOGGER.error("SUCCESSFULLY GOT FIELD");
        } catch (NoSuchFieldException e) {
            Class superclass = biomeIn.getClass().getSuperclass();
            if (superclass == null) {
                LOGGER.error("ERROR GETTING FIELD " + e + " " + biomeIn.getClass().getName());
            } else {
                try {
                    final Field field = superclass.getDeclaredField("carvers");
                    field.setAccessible(true);
                    field.set(biomeIn, carvers);
                    LOGGER.error("SUCCESSFULLY GOT FIELD FROM SUPERCLASS");
                } catch (Exception e2) {
                    LOGGER.error("SECOND LAYER EXCEPTION?");
                }
            }
        } catch (Exception e) {
            LOGGER.error("FALL THRU: " + e);
        }

    }
}