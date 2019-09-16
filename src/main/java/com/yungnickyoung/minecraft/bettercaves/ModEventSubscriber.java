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
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
//    static final WorldCarverBC BETTER_CAVE = new WorldCarverBC(ProbabilityConfig::deserialize, 256);
//    private static ConfiguredCarver<ProbabilityConfig> confCarver = Biome.createCarver(BETTER_CAVE, new ProbabilityConfig(0));

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();

        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHelper.bakeClient(config);
            LOGGER.debug("Baked client config");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommonSetupEvent(final FMLCommonSetupEvent event) {

//        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();
//        for (Map.Entry e : biomesList) {
//            Biome b = (Biome)e.getValue();
//            setCarvers(b, confCarver);
//        }
    }

//    private static void setCarvers(Biome biomeIn, ConfiguredCarver<ProbabilityConfig> carver) {
//        Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();
//
//        carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
//                Lists.newArrayList()
//        ).add(carver);
//
//        if (BetterCavesConfig.enableVanillaRavines) {
//            // Add regular ravines
//            carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
//                    Lists.newArrayList()
//            ).add(Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
//        }
//
//        if (BetterCavesConfig.enableVanillaUnderwaterRavines) {
//            // Add ravines under oceans (these spawn separately from normal ravines in 1.14)
//            carvers.computeIfAbsent(GenerationStage.Carving.LIQUID, (p_203604_0_) ->
//                    Lists.newArrayList()
//            ).add(Biome.createCarver(WorldCarver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
//        }
//
//        try {
//            final Field field = biomeIn.getClass().getDeclaredField("carvers");
//            field.setAccessible(true);
//            field.set(biomeIn, carvers);
//            LOGGER.error("SUCCESSFULLY GOT FIELD");
//        } catch (NoSuchFieldException e) {
//            Class superclass = biomeIn.getClass().getSuperclass();
//            if (superclass == null) {
//                LOGGER.error("ERROR GETTING FIELD " + e + " " + biomeIn.getClass().getName());
//            } else {
//                try {
//                    final Field field = superclass.getDeclaredField("carvers");
//                    field.setAccessible(true);
//                    field.set(biomeIn, carvers);
//                    LOGGER.error("SUCCESSFULLY GOT FIELD FROM SUPERCLASS");
//                } catch (Exception e2) {
//                    LOGGER.error("SECOND LAYER EXCEPTION?");
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("FALL THRU: " + e);
//        }
//
//    }
}
