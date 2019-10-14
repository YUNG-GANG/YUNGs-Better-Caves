package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.proxy.ClientProxy;
import com.yungnickyoung.minecraft.bettercaves.proxy.CommonProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for Better Caves
 */
@Mod(Settings.MOD_ID)
public class BetterCaves {

    public static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID);
    public static CommonProxy proxy;

    public BetterCaves() {
        LOGGER.debug("Better Caves entry point");

        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.CLIENT_SPEC);

        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.start();
    }
}