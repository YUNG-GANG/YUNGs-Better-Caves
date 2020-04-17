package com.yungnickyoung.minecraft.bettercaves.world.carver.surface;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.WaterRegionController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Random;

/**
 * Generates vanilla caves, with the ability to modify some parameters.
 */
public class VanillaCaveCarver extends MapGenCaves implements ICarver {
    private int
        bottomY,
        topY,
        density,
        priority,
        liquidAltitude;
    private IBlockState debugBlock;
    private boolean
        isDebugVisualizerEnabled,
        isReplaceGravel;

    public VanillaCaveCarver(final VanillaCaveCarverBuilder builder) {
        this.bottomY = builder.getBottomY();
        this.topY = builder.getTopY();
        this.density = builder.getDensity();
        this.priority = builder.getPriority();
        this.liquidAltitude = builder.getLiquidAltitude();
        this.debugBlock = builder.getDebugBlock();
        this.isDebugVisualizerEnabled = builder.isDebugVisualizerEnabled();
        this.isReplaceGravel = builder.isReplaceGravel();
        if (bottomY > topY) {
            Settings.LOGGER.warn("Warning: Min altitude for vanilla caves should not be greater than max altitude.");
            Settings.LOGGER.warn("Using default values...");
            this.bottomY = 40;
            this.topY = 128;
        }
    }

    public void generate(World worldIn, int x, int z, ChunkPrimer primer, boolean addRooms, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
        int i = this.range;
        this.world = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();

        for (int chunkX = x - i; chunkX <= x + i; ++chunkX) {
            for (int chunkZ = z - i; chunkZ <= z + i; ++chunkZ) {
                long j1 = (long) chunkX * j;
                long k1 = (long) chunkZ * k;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(chunkX, chunkZ, x, z, primer, addRooms, liquidBlocks, carvingMask);
            }
        }
    }

    public void generate(World worldIn, int x, int z, ChunkPrimer primer, boolean addRooms, IBlockState[][] liquidBlocks) {
        boolean[][] carvingMask = new boolean[16][16];
        for (boolean[] row : carvingMask)
            Arrays.fill(row, true);
        generate(worldIn, x, z, primer, addRooms, liquidBlocks, carvingMask);
    }

    protected void recursiveGenerate(int chunkX, int chunkZ, int originalX, int originalZ, @Nonnull ChunkPrimer chunkPrimerIn, boolean addRooms, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
        int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);

        if (this.rand.nextInt(100) > this.density) {
            i = 0;
        }

        for (int j = 0; j < i; ++j) {
            double d0 = chunkX * 16 + this.rand.nextInt(16);
            double d1 = this.rand.nextInt(this.topY - this.bottomY) + this.bottomY;
            double d2 = chunkZ * 16 + this.rand.nextInt(16);
            int k = 1;

            if (addRooms && this.rand.nextInt(4) == 0) {
                this.addRoom(this.rand.nextLong(), chunkX, chunkZ, originalX, originalZ, chunkPrimerIn, d0, d1, d2, liquidBlocks, carvingMask);
                k += this.rand.nextInt(4);
            }

            for (int l = 0; l < k; ++l) {
                float yaw = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float width = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (addRooms && this.rand.nextInt(10) == 0) {
                    width *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.addTunnel(this.rand.nextLong(), chunkX, chunkZ, originalX, originalZ, chunkPrimerIn, d0, d1, d2, width, yaw, pitch, 0, 0, 1.0D, liquidBlocks, carvingMask);
            }
        }
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public int getTopY() {
        return this.topY;
    }

    protected void addRoom(long p_180703_1_, int chunkX, int chunkZ, int p_180703_3_, int p_180703_4_, ChunkPrimer p_180703_5_, double p_180703_6_, double p_180703_8_, double p_180703_10_, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
        this.addTunnel(p_180703_1_, chunkX, chunkZ, p_180703_3_, p_180703_4_, p_180703_5_, p_180703_6_, p_180703_8_, p_180703_10_, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D, liquidBlocks, carvingMask);
    }

    protected void addTunnel(long seed, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer primer, double startX, double startY, double startZ, float width, float yaw, float pitch, int p_180702_15_, int p_180702_16_, double p_180702_17_, IBlockState[][] liquidBlocks, boolean[][] carvingMask) {
        IBlockState liquidBlock;
        double d0 = (originalX * 16 + 8);
        double d1 = (originalZ * 16 + 8);
        float f = 0.0F;
        float f1 = 0.0F;
        Random random = new Random(seed);

        if (p_180702_16_ <= 0) {
            int i = this.range * 16 - 16;
            p_180702_16_ = i - random.nextInt(i / 4);
        }

        boolean flag2 = false;

        if (p_180702_15_ == -1) {
            p_180702_15_ = p_180702_16_ / 2;
            flag2 = true;
        }

        int j = random.nextInt(p_180702_16_ / 2) + p_180702_16_ / 4;

        for (boolean flag = random.nextInt(6) == 0; p_180702_15_ < p_180702_16_; ++p_180702_15_) {
            double xzOffset = 1.5D + (double) (MathHelper.sin((float) p_180702_15_ * (float) Math.PI / (float) p_180702_16_) * width);
            double yOffset = xzOffset * p_180702_17_;
            float pitchXZ = MathHelper.cos(pitch);
            float pitchY = MathHelper.sin(pitch);
            startX += MathHelper.cos(yaw) * pitchXZ;
            startY += pitchY;
            startZ += MathHelper.sin(yaw) * pitchXZ;

            if (flag) {
                pitch = pitch * 0.92F;
            } else {
                pitch = pitch * 0.7F;
            }

            pitch = pitch + f1 * 0.1F;
            yaw += f * 0.1F;
            f1 = f1 * 0.9F;
            f = f * 0.75F;
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag2 && p_180702_15_ == j && width > 1.0F && p_180702_16_ > 0) {
                this.addTunnel(random.nextLong(), chunkX, chunkZ, originalX, originalZ, primer, startX, startY, startZ, random.nextFloat() * 0.5F + 0.5F, yaw - ((float) Math.PI / 2F), pitch / 3.0F, p_180702_15_, p_180702_16_, 1.0D, liquidBlocks, carvingMask);
                this.addTunnel(random.nextLong(), chunkX, chunkZ, originalX, originalZ, primer, startX, startY, startZ, random.nextFloat() * 0.5F + 0.5F, yaw + ((float) Math.PI / 2F), pitch / 3.0F, p_180702_15_, p_180702_16_, 1.0D, liquidBlocks, carvingMask);
                return;
            }

            if (flag2 || random.nextInt(4) != 0) {
                double d4 = startX - d0;
                double d5 = startZ - d1;
                double d6 = p_180702_16_ - p_180702_15_;
                double d7 = width + 2.0F + 16.0F;

                if (d4 * d4 + d5 * d5 - d6 * d6 > d7 * d7) {
                    return;
                }

                if (startX >= d0 - 16.0D - xzOffset * 2.0D && startZ >= d1 - 16.0D - xzOffset * 2.0D && startX <= d0 + 16.0D + xzOffset * 2.0D && startZ <= d1 + 16.0D + xzOffset * 2.0D) {
                    int k2 = MathHelper.floor(startX - xzOffset) - originalX * 16 - 1;
                    int k = MathHelper.floor(startX + xzOffset) - originalX * 16 + 1;
                    int l2 = MathHelper.floor(startY - yOffset) - 1;
                    int l = MathHelper.floor(startY + yOffset) + 1;
                    int i3 = MathHelper.floor(startZ - xzOffset) - originalZ * 16 - 1;
                    int i1 = MathHelper.floor(startZ + xzOffset) - originalZ * 16 + 1;

                    if (k2 < 0) {
                        k2 = 0;
                    }

                    if (k > 16) {
                        k = 16;
                    }

                    if (l2 < 1) {
                        l2 = 1;
                    }

                    if (l > 248) {
                        l = 248;
                    }

                    if (i3 < 0) {
                        i3 = 0;
                    }

                    if (i1 > 16) {
                        i1 = 16;
                    }

                    boolean flag3 = false;

                    for (int j1 = k2; !flag3 && j1 < k; ++j1) {
                        for (int k1 = i3; !flag3 && k1 < i1; ++k1) {
                            for (int l1 = l + 1; !flag3 && l1 >= l2 - 1; --l1) {
                                if (l1 >= 0 && l1 < 256) {
                                    if (isOceanBlock(primer, j1, l1, k1, originalX, originalZ)) {
                                        flag3 = true;
                                    }

                                    if (l1 != l2 - 1 && j1 != k2 && j1 != k - 1 && k1 != i3 && k1 != i1 - 1) {
                                        l1 = l2;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag3) {
                        for (int currX = k2; currX < k; ++currX) {
                            double d10 = ((double) (currX + originalX * 16) + 0.5D - startX) / xzOffset;

                            for (int currZ = i3; currZ < i1; ++currZ) {
                                double d8 = ((double) (currZ + originalZ * 16) + 0.5D - startZ) / xzOffset;

                                // Skip column if carving mask not set
                                if (!carvingMask[BetterCavesUtils.getLocal(currX)][BetterCavesUtils.getLocal(currZ)])
                                    continue;

                                if (d10 * d10 + d8 * d8 < 1.0D) {
                                    for (int currY = l; currY > l2; --currY) {
                                        double d9 = ((double) (currY - 1) + 0.5D - startY) / yOffset;

                                        if (d9 > -0.7D && d10 * d10 + d9 * d9 + d8 * d8 < 1.0D) {
                                            liquidBlock = liquidBlocks[BetterCavesUtils.getLocal(currX)][BetterCavesUtils.getLocal(currZ)];
                                            if (this.isDebugVisualizerEnabled)
                                                CarverUtils.debugDigBlock(primer, currX, currY, currZ, debugBlock, true);
                                            else
                                                digBlock(world, primer, chunkX, chunkZ, currX, currY, currZ, liquidBlock, this.liquidAltitude, this.isReplaceGravel);
                                        } else {
                                            if (this.isDebugVisualizerEnabled)
                                                CarverUtils.debugDigBlock(primer, currX, currY, currZ, debugBlock, false);
                                        }
                                    }
                                }
                            }
                        }

                        if (flag2) {
                            break;
                        }
                    }
                }
            }
        }
    }

//    @Override
//    protected void digBlock(ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ, boolean foundtop, IBlockState blockState, IBlockState blockStateAbove) {
//        IBlockState airBlockState = Blocks.AIR.getDefaultState();
//        if (world.getBiome(new BlockPos(x, y, z)).getTempCategory() == Biome.TempCategory.OCEAN) {
//            airBlockState = Blocks.WATER.getDefaultState();
//        }
//        CarverUtils.digBlock(world, primer, x, y, z, airBlockState, liquidBlockState, liquidAltitude, replaceGravel);
//    }
    private void digBlock(World world, ChunkPrimer primer, int chunkX, int chunkZ, int x, int y, int z, IBlockState liquidBlockState, int liquidAltitude, boolean replaceGravel) {
        // TODO - support for flooding in ocean biomes?
        // The below code for some reason splits the caves apart in the ocean.
//        // Don't dig boundaries between flooded and unflooded openings.
//        boolean flooded = world.getBiome(pos).getTempCategory() == Biome.TempCategory.OCEAN;
////        if (flooded) {
////            float smoothAmpFactor = WaterRegionController.getDistFactor(world, pos, 2, b -> b != Biome.TempCategory.OCEAN);
////            if (smoothAmpFactor <= .25f) { // Wall between flooded and normal caves.
////                return;
////            }
////        }
//        IBlockState airBlockState = flooded ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
//        IBlockState airBlockState = Blocks.GOLD_BLOCK.getDefaultState();
        // getBiome here doesnt seem to work properly???
//        if (world.getBiome(new BlockPos(chunkX * 16 + x, y, chunkZ * 16 + z)).getTempCategory() == Biome.TempCategory.OCEAN) {
//            airBlockState = Blocks.BRICK_BLOCK.getDefaultState();
//        }
//
//
//        if (airBlockState == Blocks.AIR.getDefaultState() && y <= liquidAltitude) { // Replace any block below the liquid altitude with the liquid block passed in
//            if (liquidBlockState != null) {
//                primer.setBlockState(x, y, z, liquidBlockState);
//            }
//        }
//        else {
//            // Replace this block with air, effectively "digging" it out
//            primer.setBlockState(x, y, z, airBlockState);
//        }

        CarverUtils.digBlock(world, primer, x, y, z, liquidBlockState, liquidAltitude, replaceGravel);
    }
}
