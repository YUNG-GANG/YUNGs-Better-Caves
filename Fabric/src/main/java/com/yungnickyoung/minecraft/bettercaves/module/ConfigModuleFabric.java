package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.config.BCConfigFabric;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.world.InteractionResult;

public class ConfigModuleFabric {
    public static void init() {
        AutoConfig.register(BCConfigFabric.class, Toml4jConfigSerializer::new);
        AutoConfig.getConfigHolder(BCConfigFabric.class).registerSaveListener(ConfigModuleFabric::bakeConfig);
        AutoConfig.getConfigHolder(BCConfigFabric.class).registerLoadListener(ConfigModuleFabric::bakeConfig);
        bakeConfig(AutoConfig.getConfigHolder(BCConfigFabric.class).get());
    }

    private static InteractionResult bakeConfig(ConfigHolder<BCConfigFabric> configHolder, BCConfigFabric configFabric) {
        bakeConfig(configFabric);
        return InteractionResult.SUCCESS;
    }

    private static void bakeConfig(BCConfigFabric configFabric) {
        BetterCavesCommon.CONFIG.liquidRegions.liquidAltitude = configFabric.general.liquidRegions.liquidAltitude;
        BetterCavesCommon.CONFIG.liquidRegions.liquidRegionSize = configFabric.general.liquidRegions.liquidRegionSize;
        BetterCavesCommon.CONFIG.liquidRegions.waterRegionSpawnChance = configFabric.general.liquidRegions.waterRegionSpawnChance;
    }
}
