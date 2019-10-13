package com.yungnickyoung.minecraft.bettercaves;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.proxy.ClientProxy;
import com.yungnickyoung.minecraft.bettercaves.proxy.CommonProxy;
import com.yungnickyoung.minecraft.bettercaves.world.WorldCarverBC;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entry point for Better Caves
 */
@Mod(Settings.MOD_ID)
public class BetterCaves {

    public static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID);
    public static CommonProxy proxy;

    public BetterCaves() {
        LOGGER.info("Better Caves entry point");
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.CLIENT_SPEC);
//        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.CLIENT_SPEC);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

//        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy = new CommonProxy();
        proxy.start();


    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.getMinecraftSupplier().get().reloadResources();
    }
}