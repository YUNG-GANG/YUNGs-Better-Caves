package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHelper;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.WorldCarverBC;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonProxy {
    WorldCarverBC betterCaveCarver;
    ConfiguredCarver<ProbabilityConfig> configuredCarver;
    boolean initFlag = false;

    public void start() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        betterCaveCarver = new WorldCarverBC(ProbabilityConfig::deserialize, 256);
        configuredCarver = Biome.createCarver(betterCaveCarver, new ProbabilityConfig(1));

        registerListeners(bus);
        MinecraftForge.EVENT_BUS.addListener(this::worldLoad);
    }

    public void registerListeners(IEventBus bus) {
        bus.addListener(this::configChanged);
//        if (EffectiveSide.get() == LogicalSide.SERVER) {
            bus.addListener(this::setup);
//        }
    }

    public void configChanged(ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();

        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHelper.bakeClient(config);
            BetterCaves.LOGGER.debug("Baked client config");
        }

        betterCaveCarver.initialize(0);
    }

    public void worldLoad(WorldEvent event) {

//        if (EffectiveSide.get() == LogicalSide.SERVER) {

        if (initFlag) return;
        initFlag = true;

        BetterCaves.LOGGER.info(event.getClass().getName());

        IWorld world = event.getWorld();
            long seed = world.getSeed();
//            long seed = event.getSeed();

            BetterCaves.LOGGER.info("--> Initializing Better Caves carver with seed: " + seed);

            // Initialize Better Caves with world seed
            betterCaveCarver.initialize(seed);
//        }
    }

    public void setup(FMLCommonSetupEvent event) {
        BetterCaves.LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Get all registered biomes
        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();

        // Replace biome carvers with Better Caves carvers
        for (Map.Entry e : biomesList) {
            Biome b = (Biome)e.getValue();

            // Exclude Nether and End biomes
            if (b == Biomes.NETHER
                    || b == Biomes.THE_END
                    || b == Biomes.END_BARRENS
                    || b == Biomes.END_HIGHLANDS
                    || b == Biomes.END_MIDLANDS
                    || b == Biomes.SMALL_END_ISLANDS)
                continue;

            setCarvers2(b, configuredCarver);
        }
//        betterCaveCarver.initialize(0);
    }

    private static void setCarvers2(Biome biomeIn, ConfiguredCarver<ProbabilityConfig> carver) {
        List<ConfiguredCarver<?>> airCarvers = biomeIn.getCarvers(GenerationStage.Carving.AIR);
        List<ConfiguredCarver<?>> liquidCarvers = biomeIn.getCarvers(GenerationStage.Carving.LIQUID);

        BetterCaves.LOGGER.debug(airCarvers + " " + liquidCarvers);

        airCarvers.clear();
        liquidCarvers.clear();
        airCarvers.add(carver);
    }

    /**
     * Helper method used to replace a biome's 'carvers' var with Better Caves carvers, thereby overriding
     * vanilla cave generation.
     * @param biomeIn Biome to override
     * @param carver The carver replacing the default carvers
     */
    private static void setCarvers(Biome biomeIn, ConfiguredCarver<ProbabilityConfig> carver) {
        Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();

        // Add Better Caves as regular carver
        carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
                Lists.newArrayList()
        ).add(carver);

        // Add Better Caves as liquid carver for ocean biomes
        carvers.computeIfAbsent(GenerationStage.Carving.LIQUID, (p_203604_0_) ->
                Lists.newArrayList()
        ).add(carver);

        // Generate ravines depending on user config option
        if (BetterCavesConfig.enableVanillaRavines) {
            // Add regular ravines
            carvers.computeIfAbsent(GenerationStage.Carving.AIR, (p_203604_0_) ->
                    Lists.newArrayList()
            ).add(Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
        }

        // Generate underwater ravines depending on user config option
        if (BetterCavesConfig.enableVanillaUnderwaterRavines) {
            // Add ravines under oceans (these spawn separately from normal ravines in 1.14)
            carvers.computeIfAbsent(GenerationStage.Carving.LIQUID, (p_203604_0_) ->
                    Lists.newArrayList()
            ).add(Biome.createCarver(WorldCarver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
        }

        // Attempt to replace biome's 'carvers' field with the list we've created, overriding vanilla cave gen
        try {
            final Field field = biomeIn.getClass().getDeclaredField("carvers");
            field.setAccessible(true);
            field.set(biomeIn, carvers);
//            LOGGER.info("Successfully updated 'carvers' field for Biome " + biomeIn.getDisplayName());
        } catch (NoSuchFieldException e) {
            Class superclass = biomeIn.getClass().getSuperclass();
            if (superclass == null) {
                BetterCaves.LOGGER.error("Error getting 'carvers' field for biome " + biomeIn.getClass().getName()+ ": " + e);
                Field[] allFields = biomeIn.getClass().getDeclaredFields();
                BetterCaves.LOGGER.error("    All fields: " + java.util.Arrays.toString(allFields));
            } else {
                try {
                    final Field field = superclass.getDeclaredField("carvers");
                    field.setAccessible(true);
                    field.set(biomeIn, carvers);
//                    LOGGER.info("Successfully updated 'carvers' field for Biome " + biomeIn.getDisplayName() + " from parent class (probably Biome)");
                } catch (Exception e2) {
                    BetterCaves.LOGGER.error("Error getting 'carvers' field for biome " + biomeIn.getClass().getName()+ ": " + e);
//                    Field[] allFields = biomeIn.getClass().getDeclaredFields();
                    Field[] allFields = superclass.getDeclaredFields();
                    for (int i = 0; i < allFields.length; i++)
                        BetterCaves.LOGGER.error("    " + allFields[i] + "\n");
                }
            }
        } catch (Exception e) {
            BetterCaves.LOGGER.error("Error getting 'carvers' field for biome " + biomeIn.getClass().getName()+ ": " + e);
        }
    }
}
