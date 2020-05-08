package com.yungnickyoung.minecraft.bettercaves.world.carver.ravine;

import com.mojang.datafixers.Dynamic;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.controller.WaterRegionController;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * Re-implements vanilla ravine carver, but with a few modifications for Better Caves config options.
 * Variables have been renamed to be much more readable, making the algorithm a lot more understandable.
 */
public class RavineCarver extends CanyonWorldCarver {
    private IWorld world;

    // Vars from config
    private boolean isFloodedRavinesEnabled;
    private boolean isReplaceGravelEnabled;
    private int liquidAltitude;

    // Vanilla logic
    private final float[] heightToHorizontalStretchFactor = new float[1024];

    public RavineCarver(IWorld worldIn, ConfigHolder config, Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49930_1_) {
        super(p_i49930_1_);
        this.world = worldIn;
        this.isFloodedRavinesEnabled = config.enableFloodedRavines.get();
        this.isReplaceGravelEnabled = config.replaceFloatingGravel.get();
        this.liquidAltitude = config.liquidAltitude.get();
    }

    public void carve(IChunk chunkIn, Random rand, int seaLevel, int chunkX, int chunkZ, int originChunkX, int originChunkZ, BlockState[][] liquidBlocks, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        int i = (this.func_222704_c() * 2 - 1) * 16;
        double startX = chunkX * 16 + rand.nextInt(16);
        double startY = rand.nextInt(rand.nextInt(40) + 8) + 20;
        double startZ = chunkZ * 16 + rand.nextInt(16);

        float yaw = rand.nextFloat() * ((float)Math.PI * 2F);
        float pitch = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;

        float width = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
        double heightModifier = 3.0D;

        int startCounter = 0;
        int endCounter = i - rand.nextInt(i / 4);

        this.carveRavine(chunkIn, rand.nextLong(), seaLevel, originChunkX, originChunkZ, startX, startY, startZ, width, yaw, pitch, startCounter, endCounter, heightModifier, liquidBlocks, airCarvingMask, liquidCarvingMask);
    }

    private void carveRavine(IChunk chunkIn, long seed, int seaLevel, int originChunkX, int originChunkZ, double ravineStartX, double ravineStartY, double ravineStartZ, float width, float yaw, float pitch, int startCounter, int endCounter, double heightModifier, BlockState[][] liquidBlocks, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        Random random = new Random(seed);
        float f = 1.0F;

        for(int i = 0; i < 256; ++i) {
            if (i == 0 || random.nextInt(3) == 0) {
                f = 1.0F + random.nextFloat() * random.nextFloat();
            }

            this.heightToHorizontalStretchFactor[i] = f * f;
        }

        // Variables to slightly change the yaw/pitch for each iteration in the while loop below.
        float yawModifier = 0.0F;
        float pitchModifier = 0.0F;

        // Loops one block at a time to the endCounter.
        while (startCounter < endCounter) {
            // Appears to change how wide ravines are. Value will be between 1.5 and 1.5 + width.
            // Note that ravines will become wider toward the middle, and close off on the ends.
            double xzOffset = 1.5D + (double)(MathHelper.sin((float) startCounter * (float) Math.PI / (float) endCounter) * width);

            // Appears to multiply a linear modifier for the ravine height
            double yOffset = xzOffset * heightModifier;

            xzOffset = xzOffset * ((double)random.nextFloat() * 0.25D + 0.75D);
            yOffset = yOffset * ((double)random.nextFloat() * 0.25D + 0.75D);

            float pitchXZ = MathHelper.cos(pitch);
            float pitchY = MathHelper.sin(pitch);

            ravineStartX += MathHelper.cos(yaw) * pitchXZ;
            ravineStartY += pitchY;
            ravineStartZ += MathHelper.sin(yaw) * pitchXZ;

            pitch = pitch * 0.7F;
            pitch = pitch + pitchModifier * 0.05F;
            yaw += yawModifier * 0.05F;

            pitchModifier = pitchModifier * 0.8F;
            yawModifier = yawModifier * 0.5F;

            pitchModifier = pitchModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawModifier = yawModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (random.nextInt(4) != 0) {
                if (!this.canCarveBranch(originChunkX, originChunkZ, ravineStartX, ravineStartZ, startCounter, endCounter, width)) {
                    return;
                }

                this.carveRegion(chunkIn, seed, seaLevel, originChunkX, originChunkZ, ravineStartX, ravineStartY, ravineStartZ, xzOffset, yOffset, liquidBlocks, airCarvingMask, liquidCarvingMask);
            }
            startCounter++;
        }
    }

    protected boolean canCarveBranch(int originChunkX, int originChunkZ, double ravineStartX, double ravineStartZ, int startCounter, int endCounter, float width) {
        double originBlockX = originChunkX * 16 + 8;
        double originBlockZ = originChunkZ * 16 + 8;
        double ravineStartXOffsetFromCenter = ravineStartX - originBlockX;
        double ravineStartZOffsetFromCenter = ravineStartZ - originBlockZ;
        double distanceToEnd = endCounter - startCounter;
        double d5 = width + 2.0F + 16.0F;
        return ravineStartXOffsetFromCenter * ravineStartXOffsetFromCenter + ravineStartZOffsetFromCenter * ravineStartZOffsetFromCenter - distanceToEnd * distanceToEnd <= d5 * d5;
    }

    protected void carveRegion(IChunk chunkIn, long seed, int seaLevel, int originChunkX, int originChunkZ, double ravineStartX, double ravineStartY, double ravineStartZ, double xzOffset, double yOffset, BlockState[][] liquidBlocks, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        BlockState liquidBlock;
        Random rand = new Random(seed + (long)originChunkX + (long)originChunkZ);
        double originBlockX = originChunkX * 16 + 8;
        double originBlockZ = originChunkZ * 16 + 8;

        // Only continue if ravine is close enough to origin
        if (!(ravineStartX < originBlockX - 16.0D - xzOffset * 2.0D) && !(ravineStartZ < originBlockZ - 16.0D - xzOffset * 2.0D) && !(ravineStartX > originBlockX + 16.0D + xzOffset * 2.0D) && !(ravineStartZ > originBlockZ + 16.0D + xzOffset * 2.0D)) {
            // Determine the bounds of the region we are carving, ensuring it is within the chunk
            int minLocalX = Math.max(MathHelper.floor(ravineStartX - xzOffset) - originChunkX * 16 - 1, 0);
            int maxLocalX = Math.min(MathHelper.floor(ravineStartX + xzOffset) - originChunkX * 16 + 1, 16);
            int minY = Math.max(MathHelper.floor(ravineStartY - yOffset) - 1, 1);
            int maxY = Math.min(MathHelper.floor(ravineStartY + yOffset) + 1, this.maxHeight - 8);
            int minLocalZ = Math.max(MathHelper.floor(ravineStartZ - xzOffset) - originChunkZ * 16 - 1, 0);
            int maxLocalZ = Math.min(MathHelper.floor(ravineStartZ + xzOffset) - originChunkZ * 16 + 1, 16);

            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int currLocalX = minLocalX; currLocalX < maxLocalX; ++currLocalX) {
                int realX = currLocalX + originChunkX * 16;

                // Distance along the x-axis from the center (ravineStart) of this ellipsoid.
                // You can think of this value as (x/a), where a is the length of the ellipsoid's semi-axis in the x direction.
                double xAxisDist = ((double) realX + 0.5D - ravineStartX) / xzOffset;

                for (int currLocalZ = minLocalZ; currLocalZ < maxLocalZ; ++currLocalZ) {
                    int realZ = currLocalZ + originChunkZ * 16;

                    // Distance along the z-axis from the center (ravineStart) of this ellipsoid.
                    // You can think of this value as (z/b), where b is the length of the ellipsoid's semi-axis in the z direction (same as a in this case).
                    double zAxisDist = ((double) realZ + 0.5D - ravineStartZ) / xzOffset;

                    // Only operate on points within ellipse on XZ axis. Avoids unnecessary computation along y axis
                    if (!(xAxisDist * xAxisDist + zAxisDist * zAxisDist >= 1.0D)) {
                        for (int currY = maxY; currY > minY; --currY) {
                            // Distance along the y-axis from the center (ravineStart) of this ellipsoid.
                            // You can think of this value as (y/c), where c is the length of the ellipsoid's semi-axis in the y direction.
                            double yAxisDist = ((double) currY - 0.5D - ravineStartY) / yOffset;

                            // Only operate on points within the ellipsoid.
                            // This conditional is validating the current coordinate against the equation of the ellipsoid, that is,
                            // (x/a)^2 + (z/b)^2 + (y/c)^2 <= 1.
                            if (!this.isPositionExcluded(xAxisDist, yAxisDist, zAxisDist, currY)) {
                                mutableBlockPos.setPos(realX, currY, realZ);
                                liquidBlock = liquidBlocks[currLocalX][currLocalZ];
                                this.carveBlock(chunkIn, rand, seaLevel, mutableBlockPos, liquidBlock, airCarvingMask, liquidCarvingMask);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isPositionExcluded(double xAxisDist, double yAxisDist, double zAxisDist, int currY) {
        return (xAxisDist * xAxisDist + zAxisDist * zAxisDist) * (double)this.heightToHorizontalStretchFactor[currY - 1] + yAxisDist * yAxisDist / 6.0D >= 1.0D;
    }

    private void carveBlock(IChunk chunkIn, Random rand, int seaLevel, BlockPos.MutableBlockPos blockPos, BlockState liquidBlockState, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Check if already carved
        int bitIndex = (blockPos.getX() & 0xF) | ((blockPos.getZ() & 0xF) << 4) | (blockPos.getY() << 8);
        if (airCarvingMask.get(bitIndex) || liquidCarvingMask.get(bitIndex)) {
            return;
        }

        // Determine if ravine is flooded at this location
        boolean flooded = isFloodedRavinesEnabled && chunkIn.getBiome(blockPos).getCategory() == Biome.Category.OCEAN;
        if (flooded) {
            // Cannot go above sea level
            if (blockPos.getY() >= seaLevel) {
                return;
            }

            // Don't dig in boundaries between flooded and unflooded openings.
            float smoothAmpFactor = WaterRegionController.getDistFactor(world, blockPos, 2, b -> b != Biome.Category.OCEAN);
            if (smoothAmpFactor <= .25f) { // Wall between flooded and normal caves.
                return;
            }
        }

        // Carve block
        if (flooded) {
            CarverUtils.carveFloodedBlock(chunkIn, rand, blockPos, liquidBlockState, liquidAltitude, liquidCarvingMask);
        }
        else {
            CarverUtils.carveBlock(chunkIn, blockPos, liquidBlockState, this.liquidAltitude, this.isReplaceGravelEnabled, airCarvingMask);
        }
    }

    public void setWorld(IWorld worldIn) {
        this.world = worldIn;
    }
}
