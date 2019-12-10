package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHelper;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.WorldCarverBC;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonProxy {
    private WorldCarverBC betterCaveCarver;
    private ConfiguredCarver<ProbabilityConfig> configuredCarver;
    private long activeWorldSeed = 0;
    private boolean initFlag = false;

    public void start() {
        IEventBus fmlBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        betterCaveCarver = new WorldCarverBC(ProbabilityConfig::deserialize, 256);
        configuredCarver = Biome.createCarver(betterCaveCarver, new ProbabilityConfig(1));

        registerListeners(fmlBus, forgeBus);
    }

    public void registerListeners(IEventBus fmlBus, IEventBus forgeBus) {
        fmlBus.addListener(this::configChanged);
        fmlBus.addListener(this::commonSetup);

        forgeBus.addListener(this::worldLoad);
        forgeBus.addListener(this::worldCreateSpawn);
        forgeBus.addListener(this::worldUnload);
    }

    public void configChanged(ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();

        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHelper.bakeClient(config);
            BetterCaves.LOGGER.debug("Baked client config");
        }

        betterCaveCarver.initialize(activeWorldSeed);
    }

    public void worldLoad(WorldEvent.Load event) {
        setSeedOnWorldLoad(event);
    }

    public void worldCreateSpawn(WorldEvent.CreateSpawnPosition event) {
        setSeedOnWorldLoad(event);
    }

    public void worldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().getDimension().isSurfaceWorld()) return;
        BetterCaves.LOGGER.debug("--> Resetting Better Caves seed flag");
        initFlag = false; // Reset init flag in case we switch worlds
        betterCaveCarver.coordList.clear();
        WorldCarverBC.counter = 0;
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        BetterCaves.LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Get all registered biomes
        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();

        // Replace biome carvers with Better Caves carvers
        for (Map.Entry e : biomesList) {
            Biome b = (Biome)e.getValue();

            // Exclude Nether and End biomes
            if (b.getCategory() == Biome.Category.NETHER
                    || b.getCategory() == Biome.Category.THEEND
                    || b.getCategory() == Biome.Category.NONE)
                continue;


            setCarvers(b, configuredCarver);
        }
    }

    private void setSeedOnWorldLoad(WorldEvent event) {
        if (initFlag) return;
        initFlag = true;

        IWorld world = event.getWorld();
        long seed = world.getSeed();

        if (seed == 0) {
            initFlag = false;
            return;
        }

        activeWorldSeed = seed;

        BetterCaves.LOGGER.info("--> Initializing Better Caves carver with seed: " + seed + " " + event);

        // Initialize Better Caves with world seed
        betterCaveCarver.initialize(seed);
    }

    private static void setCarvers(Biome biomeIn, ConfiguredCarver<ProbabilityConfig> carver) {
        List<ConfiguredCarver<?>> airCarvers = biomeIn.getCarvers(GenerationStage.Carving.AIR);
        List<ConfiguredCarver<?>> liquidCarvers = biomeIn.getCarvers(GenerationStage.Carving.LIQUID);

        BetterCaves.LOGGER.debug("Found '" + biomeIn.getDisplayName().getString() + "' biome default carvers: AIR: " + airCarvers + " LIQUID: " + liquidCarvers);

        // Remove default carvers
        airCarvers.clear();
        liquidCarvers.clear();

        // Add Better Caves carver
        airCarvers.add(carver);

        // If enabled, add normal ravines
        if (BetterCavesConfig.enableRavines)
            biomeIn.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));

        // If enabled, add underwater ravines to ocean biomes
        if (BetterCavesConfig.enableUnderwaterRavines && biomeIn.getCategory() == Biome.Category.OCEAN)
            biomeIn.addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(WorldCarver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
    }
}
