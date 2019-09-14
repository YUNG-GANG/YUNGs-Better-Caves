package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Entry point for Better Caves
 */
@Mod(Settings.MOD_ID)
public class BetterCaves {

    public BetterCaves() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
    }
}