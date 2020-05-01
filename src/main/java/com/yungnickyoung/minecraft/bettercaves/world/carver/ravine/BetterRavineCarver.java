//package com.yungnickyoung.minecraft.bettercaves.world.carver.ravine;
//
//import com.mojang.datafixers.Dynamic;
//import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
//import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
//import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
//import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
//import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.chunk.IChunk;
//import net.minecraft.world.gen.carver.CanyonWorldCarver;
//import net.minecraft.world.gen.feature.ProbabilityConfig;
//
//import java.util.BitSet;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.function.Function;
//
///**
// * Overrides MapGenRavine, tweaking it to work with config options.
// */
//public class BetterRavineCarver extends CanyonWorldCarver {
//    private BetterCavesCarver carver;
//    private int liquidAltitude;
//    private boolean isReplaceFloatingGravel;
//    private boolean isFloodedRavinesEnabled;
//
//    BlockState[][] currChunkLiquidBlocks;
//    int currChunkX, currChunkZ;
//
//    public BetterRavineCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49930_1_) {
//        super(p_i49930_1_);
//    }
//
//    @Override
//    protected boolean carveBlock(IChunk chunkIn, BitSet carvingMask, Random rand, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos p_222703_5_, BlockPos.MutableBlockPos p_222703_6_, int p_222703_7_, int p_222703_8_, int p_222703_9_, int p_222703_10_, int p_222703_11_, int p_222703_12_, int p_222703_13_, int p_222703_14_, AtomicBoolean p_222703_15_) {
//        BlockState liquidBlockState;
//        int chunkX = pos.getX() / 16;
//        int chunkZ = pos.getZ() / 16;
//
//        if (currChunkLiquidBlocks == null || chunkX != currChunkX || chunkZ != currChunkZ) {
//            try {
//                currChunkLiquidBlocks = carver.waterRegionController.getLiquidBlocksForChunk(chunkX , chunkZ);
//                liquidBlockState = currChunkLiquidBlocks[BetterCavesUtil.getLocal(pos.getX())][BetterCavesUtil.getLocal(pos.getZ())];
//            } catch (Exception e) {
//                liquidBlockState = Blocks.LAVA.getDefaultState();
//            }
//        }
//        else {
//            try {
//                liquidBlockState = currChunkLiquidBlocks[BetterCavesUtil.getLocal(pos.getX())][BetterCavesUtil.getLocal(pos.getZ())];
//            } catch (Exception e) {
//                liquidBlockState = Blocks.LAVA.getDefaultState();
//            }
//        }
//
//        // Don't dig boundaries between flooded and unflooded openings.
//        boolean flooded = isFloodedRavinesEnabled && chunkIn.getBiome(pos).getCategory() == Biome.Category.OCEAN;
////        if (flooded) {
////            float smoothAmpFactor = WaterRegionController.getDistFactor(world, pos, 2, b -> b != Biome.TempCategory.OCEAN);
////            if (smoothAmpFactor <= .25f) { // Wall between flooded and normal caves.
////                return;
////            }
////        }
//
//        BlockState airBlockState = flooded ? Blocks.WATER.getDefaultState() : AIR;
//        CarverUtils.digBlock(chunkIn, pos, airBlockState, liquidBlockState, liquidAltitude, isReplaceFloatingGravel);
//        return true;
//    }
//
//    private void initialize(int dimensionId, int dimensionName) {
//        this.carver = BetterCaves.activeCarversMap.get(dimensionId);
//        // Check for error loading carver
//        if (carver == null) {
//            BetterCaves.LOGGER.error(String.format("Failed to find Better Caves carver in dimension %s during ravine generation!", dimensionName));
//            BetterCaves.LOGGER.error("Is another cave mod installed?");
//        }
//        this.liquidAltitude = BetterCavesConfig.liquidAltitude;
//        this.isReplaceFloatingGravel = BetterCavesConfig.replaceFloatingGravel;
//        this.isFloodedRavinesEnabled = BetterCavesConfig.enableFloodedRavines;
//    }
//}
