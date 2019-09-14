package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHelper;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.world.CaveWorldCarverBC;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Subscribe to events from the MOD EventBus that should be handled on both PHYSICAL sides in this class
 *
 * @author Cadiboo
 * @author YUNGNICKYOUNG
 */
@Mod.EventBusSubscriber(modid = Settings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEventSubscriber {

    private static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID + " Mod Event Subscriber");

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        LOGGER.error("MOD CONFIG EVENT");
        final ModConfig config = event.getConfig();

        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHelper.bakeClient(config);
            LOGGER.debug("Baked client config");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterWorldCarvers(final RegistryEvent.Register<WorldCarver<?>> event) {
//        LOGGER.error("WORLD CARVER EVENT");
//
//        WorldCarver testwc = new CaveWorldCarverBC(ProbabilityConfig::deserialize, 256);
////        testwc.setRegistryName(Settings.MOD_ID, "testwc");
////        event.getRegistry().register(testwc);
//
//        Registry.<WorldCarver<?>>register(Registry.CARVER, "cave", testwc);
//        Registry.<WorldCarver<?>>register(Registry.CARVER, "hell_cave", testwc);
//        Registry.<WorldCarver<?>>register(Registry.CARVER, "canyon", new CaveWorldCarverBC(ProbabilityConfig::deserialize, 256));
//        Registry.<WorldCarver<?>>register(Registry.CARVER, "underwater_canyon", new CaveWorldCarverBC(ProbabilityConfig::deserialize, 256));
//        Registry.<WorldCarver<?>>register(Registry.CARVER, "underwater_cave", new CaveWorldCarverBC(ProbabilityConfig::deserialize, 256));
//
//        WorldCarver.CAVE = testwc;
    }
}
