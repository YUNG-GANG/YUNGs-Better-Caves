package com.yungnickyoung.minecraft.bettercaves.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

import java.util.List;

public class BetterCavesWorldCarverConfig extends CarverConfiguration {
    public static final Codec<BetterCavesWorldCarverConfig> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    CaveSettings.CODEC.fieldOf("caves").forGetter(config -> config.caves),
                    CavernSettings.CODEC.fieldOf("caverns").forGetter(config -> config.caverns),
                    LiquidRegionSettings.CODEC.fieldOf("liquid_regions").forGetter(config -> config.liquidRegions),
                    MiscSettings.CODEC.fieldOf("misc").forGetter(config -> config.misc),
                    DebugSettings.CODEC.optionalFieldOf("debug_settings", DebugSettings.DEFAULT).forGetter(config -> config.debugSettings)
            ).apply(builder, BetterCavesWorldCarverConfig::new));

    public final CaveSettings caves;
    public final CavernSettings caverns;
    public final LiquidRegionSettings liquidRegions;
    public final MiscSettings misc;
    public final DebugSettings debugSettings;

    public BetterCavesWorldCarverConfig(CaveSettings caves, CavernSettings caverns, LiquidRegionSettings liquidRegions,
                                        MiscSettings misc, DebugSettings debugSettings) {
        // Call the superclass constructor with default values.
        // These values aren't actually used in the Better Caves carver.
        super(
                1.0F,
                UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                UniformFloat.of(0.1F, 0.9F),
                VerticalAnchor.aboveBottom(8),
                CarverDebugSettings.DEFAULT,
                HolderSet.direct(Holder.direct(Blocks.STONE)));
        this.caves = caves;
        this.caverns = caverns;
        this.liquidRegions = liquidRegions;
        this.misc = misc;
        this.debugSettings = debugSettings;
    }

    public record CaveSettings(List<CaveSubCarverSettings> carvers, double caveSpawnChance, double caveRegionSizeFrequency) {
        public static final Codec<CaveSettings> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        CaveSubCarverSettings.CODEC.listOf().fieldOf("carvers").forGetter(config -> config.carvers),
                        Codec.DOUBLE.fieldOf("cave_spawn_chance").forGetter(config -> config.caveSpawnChance),
                        Codec.DOUBLE.fieldOf("cave_region_size_frequency").forGetter(config -> config.caveRegionSizeFrequency)
                ).apply(builder, CaveSettings::new));

        public record CaveSubCarverSettings(
                int caveBottom,
                int caveTop,
                int caveSurfaceCutoff,
                double yCompression,
                double xzCompression,
                int cavePriority,
                BlockState debugCarveState,
                Advanced advanced
        ) {
            public static final Codec<CaveSubCarverSettings> CODEC = RecordCodecBuilder.create(
                    builder -> builder.group(
                            Codec.INT.fieldOf("cave_bottom").forGetter(CaveSubCarverSettings::caveBottom),
                            Codec.INT.fieldOf("cave_top").forGetter(CaveSubCarverSettings::caveTop),
                            Codec.INT.fieldOf("cave_surface_cutoff").forGetter(CaveSubCarverSettings::caveSurfaceCutoff),
                            Codec.DOUBLE.fieldOf("y_compression").forGetter(CaveSubCarverSettings::yCompression),
                            Codec.DOUBLE.fieldOf("xz_compression").forGetter(CaveSubCarverSettings::xzCompression),
                            Codec.INT.fieldOf("cave_priority").forGetter(CaveSubCarverSettings::cavePriority),
                            BlockState.CODEC.optionalFieldOf("debug_carve_state", Blocks.OAK_PLANKS.defaultBlockState()).forGetter(CaveSubCarverSettings::debugCarveState),
                            Advanced.CODEC.fieldOf("advanced").forGetter(CaveSubCarverSettings::advanced)
                    ).apply(builder, CaveSubCarverSettings::new));

            public record Advanced(
                    double noiseThreshold,
                    int fractalOctaves,
                    double fractalGain,
                    double fractalFrequency,
                    int numGenerators,
                    boolean yAdjust,
                    double yAdjustF1,
                    double yAdjustF2,
                    String noiseType,
                    boolean isFastNoise
            ) {
                public static final Codec<Advanced> CODEC = RecordCodecBuilder.create(
                        builder -> builder.group(
                                Codec.DOUBLE.fieldOf("noise_threshold").forGetter(Advanced::noiseThreshold),
                                Codec.INT.fieldOf("fractal_octaves").forGetter(Advanced::fractalOctaves),
                                Codec.DOUBLE.fieldOf("fractal_gain").forGetter(Advanced::fractalGain),
                                Codec.DOUBLE.fieldOf("fractal_frequency").forGetter(Advanced::fractalFrequency),
                                Codec.INT.fieldOf("num_generators").forGetter(Advanced::numGenerators),
                                Codec.BOOL.fieldOf("y_adjust").forGetter(Advanced::yAdjust),
                                Codec.DOUBLE.fieldOf("y_adjust_f1").forGetter(Advanced::yAdjustF1),
                                Codec.DOUBLE.fieldOf("y_adjust_f2").forGetter(Advanced::yAdjustF2),
                                Codec.STRING.fieldOf("noise_type").forGetter(Advanced::noiseType),
                                Codec.BOOL.fieldOf("is_fast_noise").forGetter(Advanced::isFastNoise)
                        ).apply(builder, Advanced::new));
            }
        }
    }

    public record CavernSettings(List<CavernSubCarverSettings> carvers, double cavernSpawnChance, double cavernRegionSizeFrequency) {
        public static final Codec<CavernSettings> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        CavernSubCarverSettings.CODEC.listOf().fieldOf("carvers").forGetter(CavernSettings::carvers),
                        Codec.DOUBLE.fieldOf("cavern_spawn_chance").forGetter(CavernSettings::cavernSpawnChance),
                        Codec.DOUBLE.fieldOf("cavern_region_size_frequency").forGetter(CavernSettings::cavernRegionSizeFrequency)
                ).apply(builder, CavernSettings::new));

        public record CavernSubCarverSettings(
                int cavernBottom,
                int cavernTop,
                double yCompression,
                double xzCompression,
                int cavePriority,
                boolean isFloored,
                BlockState debugCarveState,
                Advanced advanced
        ) {
            public static final Codec<CavernSubCarverSettings> CODEC = RecordCodecBuilder.create(
                    builder -> builder.group(
                            Codec.INT.fieldOf("cavern_bottom").forGetter(CavernSubCarverSettings::cavernBottom),
                            Codec.INT.fieldOf("cavern_top").forGetter(CavernSubCarverSettings::cavernTop),
                            Codec.DOUBLE.fieldOf("y_compression").forGetter(CavernSubCarverSettings::yCompression),
                            Codec.DOUBLE.fieldOf("xz_compression").forGetter(CavernSubCarverSettings::xzCompression),
                            Codec.INT.fieldOf("cave_priority").forGetter(CavernSubCarverSettings::cavePriority),
                            Codec.BOOL.fieldOf("is_floored").forGetter(CavernSubCarverSettings::isFloored),
                            BlockState.CODEC.optionalFieldOf("debug_carve_state", Blocks.OAK_PLANKS.defaultBlockState()).forGetter(CavernSubCarverSettings::debugCarveState),
                            Advanced.CODEC.fieldOf("advanced").forGetter(CavernSubCarverSettings::advanced)
                    ).apply(builder, CavernSubCarverSettings::new));

            public record Advanced(
                    double noiseThreshold,
                    int fractalOctaves,
                    double fractalGain,
                    double fractalFrequency,
                    int numGenerators,
                    String noiseType,
                    boolean isFastNoise
            ) {
                public static final Codec<Advanced> CODEC = RecordCodecBuilder.create(
                        builder -> builder.group(
                                Codec.DOUBLE.fieldOf("noise_threshold").forGetter(Advanced::noiseThreshold),
                                Codec.INT.fieldOf("fractal_octaves").forGetter(Advanced::fractalOctaves),
                                Codec.DOUBLE.fieldOf("fractal_gain").forGetter(Advanced::fractalGain),
                                Codec.DOUBLE.fieldOf("fractal_frequency").forGetter(Advanced::fractalFrequency),
                                Codec.INT.fieldOf("num_generators").forGetter(Advanced::numGenerators),
                                Codec.STRING.fieldOf("noise_type").forGetter(Advanced::noiseType),
                                Codec.BOOL.fieldOf("is_fast_noise").forGetter(Advanced::isFastNoise)
                        ).apply(builder, Advanced::new));
            }
        }
    }

    public record LiquidRegionSettings(double liquidRegionSize, double waterRegionSpawnChance, int liquidAltitude,
                                       BlockState waterBlockState, BlockState lavaBlockState) {
        public static final Codec<LiquidRegionSettings> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        Codec.DOUBLE.fieldOf("liquid_region_size").forGetter(LiquidRegionSettings::liquidRegionSize),
                        Codec.DOUBLE.fieldOf("water_region_spawn_chance").forGetter(LiquidRegionSettings::waterRegionSpawnChance),
                        Codec.INT.fieldOf("liquid_altitude").forGetter(LiquidRegionSettings::liquidAltitude),
                        BlockState.CODEC.fieldOf("water_block_state").forGetter(LiquidRegionSettings::waterBlockState),
                        BlockState.CODEC.fieldOf("lava_block_state").forGetter(LiquidRegionSettings::lavaBlockState)
                ).apply(builder, LiquidRegionSettings::new));
    }

    public record MiscSettings(HolderSet<Block> replaceable, boolean overrideSurfaceDetection) {
        public static final Codec<MiscSettings> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter(MiscSettings::replaceable),
                        Codec.BOOL.optionalFieldOf("override_surface_detection", false).forGetter(MiscSettings::overrideSurfaceDetection)
                ).apply(builder, MiscSettings::new));
    }

    public record DebugSettings(boolean enabled, int topY) {
        public static final DebugSettings DEFAULT = new DebugSettings(false, 128);

        public static final Codec<DebugSettings> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        Codec.BOOL.optionalFieldOf("enabled", false).forGetter(DebugSettings::enabled),
                        Codec.INT.fieldOf("top_y").forGetter(DebugSettings::topY)
                ).apply(builder, DebugSettings::new));
    }
}
