package com.yungnickyoung.minecraft.bettercaves.init;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.DefaultCarverConfig;

import java.util.Objects;

public class BCModCarver {
    public static final Carver<DefaultCarverConfig> BETTERCAVES_CARVER = new BetterCavesCarver();
    public static final ConfiguredCarver<?> CONFIGURED_BETTERCAVES_CARVER = new ConfiguredCarver<>(BETTERCAVES_CARVER, CarverConfig.DEFAULT);

    public static void init() {
        registerCarver();
        ServerWorldEvents.UNLOAD.register((unload, serverWorld) -> worldUnload(serverWorld));
        BetterCaves.CONFIG = AutoConfig.getConfigHolder(Configuration.class).getConfig();
    }

    private static void registerCarver() {
        Registry.register(Registry.CARVER, new Identifier(BCSettings.MOD_ID, "bettercaves"), BETTERCAVES_CARVER);
        Registry.register(BuiltinRegistries.CONFIGURED_CARVER, new Identifier(BCSettings.MOD_ID, "bettercaves"), CONFIGURED_BETTERCAVES_CARVER);
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    private static void worldUnload(ServerWorld event) {
        BetterCaves.LOGGER.debug("UNLOADING WORLD");
        try {
            String key = Objects.requireNonNull((World)event).getRegistryKey().getValue().toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }
}
