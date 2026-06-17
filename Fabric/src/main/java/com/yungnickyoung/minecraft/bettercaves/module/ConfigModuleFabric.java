package com.yungnickyoung.minecraft.bettercaves.module;

import com.google.gson.Gson;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.config.BCConfigFabric;
import com.yungnickyoung.minecraft.bettercaves.json.IdentifierAdapter;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigModuleFabric {
    private static final String CUSTOM_CONFIG_PATH = BetterCavesCommon.MOD_ID;
    private static final String VERSION_PATH = "fabric-" + BetterCavesCommon.MC_VERSION_STRING;

    public static void init() {
        initCustomFiles();
//        AutoConfig.register(BCConfigFabric.class, Toml4jConfigSerializer::new);
//        AutoConfig.getConfigHolder(BCConfigFabric.class).registerSaveListener(ConfigModuleFabric::bakeConfig);
//        AutoConfig.getConfigHolder(BCConfigFabric.class).registerLoadListener(ConfigModuleFabric::bakeConfig);
//        bakeConfig(AutoConfig.getConfigHolder(BCConfigFabric.class).get());

        // Reload JSON files when server starts to ensure modded blocks and items load properly
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ConfigModuleFabric.reloadJSON());
    }

    private static InteractionResult bakeConfig(ConfigHolder<BCConfigFabric> configHolder, BCConfigFabric configFabric) {
        bakeConfig(configFabric);
        reloadJSON();
        return InteractionResult.SUCCESS;
    }

    private static void initCustomFiles() {
        createDirectory();
        createBaseReadMe();
        createJsonReadMe();
        reloadJSON();
    }

    private static void reloadJSON() {
        loadLiquidRegionsJSON();
    }

    private static void createDirectory() {
        File parentDir = new File(FabricLoader.getInstance().getConfigDir().toString(), CUSTOM_CONFIG_PATH);
        File customConfigDir = new File(parentDir, VERSION_PATH);
        try {
            String filePath = customConfigDir.getCanonicalPath();
            if (customConfigDir.mkdirs()) {
                BetterCavesCommon.LOGGER.info("Creating directory for additional Better Caves config at {}", filePath);
            }
        } catch (IOException e) {
            BetterCavesCommon.LOGGER.error("ERROR creating Better Caves config directory: {}", e.toString());
        }
    }

    private static void createBaseReadMe() {
        Path path = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), CUSTOM_CONFIG_PATH, "README.txt");
        File readme = new File(path.toString());
        if (!readme.exists()) {
            String readmeText =
                    """
                            This directory is for a few additional options for YUNG's Better Caves.
                            Options provided may vary by version.
                            This directory contains subdirectories for supported versions. The first time you run Better Caves, a version subdirectory will be created if that version supports advanced options.
                            For example, the first time you use Better Caves for MC 1.21.1 on Fabric, the 'fabric-1_21_1' subdirectory will be created in this folder.
                            If no subdirectory for your version is created, then that version probably does not support the additional options.""";
            try {
                Files.write(path, readmeText.getBytes());
            } catch (IOException e) {
                BetterCavesCommon.LOGGER.error("Unable to create README file!");
            }
        }
    }

    private static void createJsonReadMe() {
        Path path = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), CUSTOM_CONFIG_PATH, VERSION_PATH, "README.txt");
        File readme = new File(path.toString());
        if (!readme.exists()) {
            String readmeText =
                    """
                            ######################################
                            #         liquidregions.json         #
                            ######################################
                              This file contains the Liquid Regions settings for each dimension.
                            While the Better Caves carvers are added per-biome, the Liquid Regions
                            (i.e. the giant water and lava pools at the bottom of the world) are handled
                            on a per-dimension basis.
                            The liquidregions.json file is used to define the Liquid Regions for each dimension.
                            You can add or remove dimensions, or modify the settings for existing dimensions.
                            If a dimension is not defined in this file, then it will not have any Liquid Regions.
                            In that case, the normal aquifers will be used instead, as specified in the dimension's
                            respective noise_settings data pack json.
                            """;

            try {
                Files.write(path, readmeText.getBytes());
            } catch (IOException e) {
                BetterCavesCommon.LOGGER.error("Unable to create Liquid Regions README file!");
            }
        }
    }

    private static void loadLiquidRegionsJSON() {
        Path jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), CUSTOM_CONFIG_PATH, VERSION_PATH, "liquidregions.json");
        File jsonFile = new File(jsonPath.toString());

        if (!jsonFile.exists()) {
            // Create default file if JSON file doesn't already exist
            try {
                LiquidRegionsController.useDefaultSettings();
                JSON.createJsonFileFromObject(jsonPath, LiquidRegionsController.getInstance());
            } catch (IOException e) {
                BetterCavesCommon.LOGGER.error("Unable to create liquidregions.json file: {}", e.toString());
            }
        } else {
            // If file already exists, load data into OreChances singleton instance
            if (!jsonFile.canRead()) {
                BetterCavesCommon.LOGGER.error("Better Caves liquidregions.json file not readable! Using default configuration...");
                LiquidRegionsController.useDefaultSettings();
                return;
            }

            try {
                Gson gson = JSON.newGsonBuilder()
                        .registerTypeHierarchyAdapter(Identifier.class, new IdentifierAdapter())
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create();
                LiquidRegionsController.INSTANCE = JSON.loadObjectFromJsonFile(jsonPath, LiquidRegionsController.class, gson);
            } catch (IOException e) {
                BetterCavesCommon.LOGGER.error("Error loading Better Caves liquidregions.json file: {}", e.toString());
                BetterCavesCommon.LOGGER.error("Using default configuration...");
                LiquidRegionsController.useDefaultSettings();
            }
        }
    }

    private static void bakeConfig(BCConfigFabric configFabric) {
    }
}
