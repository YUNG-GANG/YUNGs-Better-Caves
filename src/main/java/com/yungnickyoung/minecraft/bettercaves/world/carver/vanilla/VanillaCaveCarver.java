package com.yungnickyoung.minecraft.bettercaves.world.carver.vanilla;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.util.ColPos;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Random;

/**
 * Re-implements vanilla world carver, but with a few modifications for Better Caves config options.
 * Variables have been renamed to be much more readable, making the algorithm a lot more understandable.
 *
 * Note that the method names and organization are based on code from 1.12.2, and thus won't be a 1:1 with
 * CaveWorldCarver. However, the algorithm is still very similar, if not identical.
 */
public class VanillaCaveCarver implements ICarver {
    private int
        bottomY,
        topY,
        density,
        priority,
        liquidAltitude;
    private BlockState debugBlock;
    private boolean
        isDebugVisualizerEnabled,
        isReplaceGravelEnabled,
        isFloodedUndergroundEnabled;
    private Random rand = new Random();
    private WorldGenLevel world;

    private int range = 8;

    public VanillaCaveCarver(final VanillaCaveCarverBuilder builder) {
        this.bottomY = builder.getBottomY();
        this.topY = builder.getTopY();
        this.density = builder.getDensity();
        this.priority = builder.getPriority();
        this.liquidAltitude = builder.getLiquidAltitude();
        this.debugBlock = builder.getDebugBlock();
        this.isDebugVisualizerEnabled = builder.isDebugVisualizerEnabled();
        this.isReplaceGravelEnabled = builder.isReplaceGravel();
        this.isFloodedUndergroundEnabled = builder.isFloodedUndergroundEnabled();
        if (bottomY > topY) {
            BetterCaves.LOGGER.warn("Warning: Min altitude for vanilla caves should not be greater than max altitude.");
            BetterCaves.LOGGER.warn("Using default values...");
            this.bottomY = 40;
            this.topY = 128;
        }
    }

    /**
     * Calls recursiveGenerate() on all chunks within a certain square range (default 8) of this chunk.
     */
    public void generate(WorldGenLevel worldIn, int chunkX, int chunkZ, ChunkAccess primer, boolean addRooms, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        int chunkRadius = this.range;
        this.world = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();
        for (int currChunkX = chunkX - chunkRadius; currChunkX <= chunkX + chunkRadius; ++currChunkX) {
            for (int currChunkZ = chunkZ - chunkRadius; currChunkZ <= chunkZ + chunkRadius; ++currChunkZ) {
                long j1 = (long) currChunkX * j;
                long k1 = (long) currChunkZ * k;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, currChunkX, currChunkZ, chunkX, chunkZ, primer, addRooms, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
            }
        }
    }

    public void generate(WorldGenLevel worldIn, int x, int z, ChunkAccess primer, boolean addRooms, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        boolean[][] validPositions = new boolean[16][16];
        for (boolean[] row : validPositions)
            Arrays.fill(row, true);
        generate(worldIn, x, z, primer, addRooms, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
    }

    /**
     * Calls addTunnel and addRoom (wrapper for addTunnel) for this chunk.
     * Note that each call to this function (and subsequently addTunnel) will be done with the same rand seed.
     * This means that when a chunk is checked multiple times by different neighbor chunks, each time it will be processed
     * the same way, ensuring the tunnels are always consistent and connecting.
     */
    private void recursiveGenerate(WorldGenLevel worldIn, int chunkX, int chunkZ, int originalChunkX, int originalChunkZ, ChunkAccess primer, boolean addRooms, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        int numAttempts = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);

        if (this.rand.nextInt(100) > this.density) {
            numAttempts = 0;
        }

        for (int i = 0; i < numAttempts; ++i) {
            double caveStartX = chunkX * 16 + this.rand.nextInt(16);
            double caveStartY = this.rand.nextInt(this.topY - this.bottomY) + this.bottomY;
            double caveStartZ = chunkZ * 16 + this.rand.nextInt(16);

            int numAddTunnelCalls = 1;

            if (addRooms && this.rand.nextInt(4) == 0) {
                this.addRoom(worldIn, this.rand.nextLong(), originalChunkX, originalChunkZ, primer, caveStartX, caveStartY, caveStartZ, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
                numAddTunnelCalls += this.rand.nextInt(4);
            }

            for (int j = 0; j < numAddTunnelCalls; ++j) {
                float yaw = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float width = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                // Chance of wider caves.
                // Although not actually related to adding rooms, I perform an addRoom check here
                // to avoid the chance of really large caves when generating surface caves.
                if (addRooms && this.rand.nextInt(10) == 0) {
                    width *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.addTunnel(worldIn, this.rand.nextLong(), originalChunkX, originalChunkZ, primer, caveStartX, caveStartY, caveStartZ, width, yaw, pitch, 0, 0, 1.0D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
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


    private void addRoom(WorldGenLevel worldIn, long seed, int originChunkX, int originChunkZ, ChunkAccess primer, double caveStartX, double caveStartY, double caveStartZ, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        this.addTunnel(worldIn, seed, originChunkX, originChunkZ, primer, caveStartX, caveStartY, caveStartZ, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
    }

    protected void addTunnel(WorldGenLevel worldIn, long seed, int originChunkX, int originChunkZ, ChunkAccess chunk, double caveStartX, double caveStartY, double caveStartZ, float width, float yaw, float pitch, int startCounter, int endCounter, double heightModifier, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        BlockState liquidBlock;
        Random random = new Random(seed);

        // Center block of the origin chunk
        double originBlockX = (originChunkX * 16 + 8);
        double originBlockZ = (originChunkZ * 16 + 8);

        // Variables to slightly change the yaw/pitch for each iteration in the while loop below.
        float yawModifier = 0.0F;
        float pitchModifier = 0.0F;

        // Raw calls to addTunnel from recursiveGenerate give startCounter and endCounter a value of 0.
        // Calls from addRoom make them both -1.

        // This appears to be called regardless of where addTunnel was called from.
        if (endCounter <= 0) {
            int i = this.range * 16 - 16;
            endCounter = i - random.nextInt(i / 4);
        }

        // Whether or not this function call was made from addRoom.
        boolean comesFromRoom = false;

        // Only called if the function call came from addRoom.
        // If this call came from addRoom, startCounter is set to halfway to endCounter.
        // If this is a raw call from recursiveGenerate, startCounter will be zero.
        if (startCounter == -1) {
            startCounter = endCounter / 2;
            comesFromRoom = true;
        }

        int randomCounterValue = random.nextInt(endCounter / 2) + endCounter / 4;

        // Loops one block at a time to the endCounter (about 6-7 chunks away on average).
        // startCounter starts at either zero or endCounter / 2.
        while (startCounter < endCounter) {
            // Appears to change how wide caves are. Value will be between 1.5 and 1.5 + width.
            // Note that caves will become wider toward the middle, and close off on the ends.
            double xzOffset = 1.5D + (double) (Mth.sin((float) startCounter * (float) Math.PI / (float) endCounter) * width);

            // Appears to just be a linear modifier for the cave height
            double yOffset = xzOffset * heightModifier;

            float pitchXZ = Mth.cos(pitch);
            float pitchY = Mth.sin(pitch);
            caveStartX += Mth.cos(yaw) * pitchXZ;
            caveStartY += pitchY;
            caveStartZ += Mth.sin(yaw) * pitchXZ;

            boolean flag = random.nextInt(6) == 0;
            if (flag) {
                pitch = pitch * 0.92F;
            } else {
                pitch = pitch * 0.7F;
            }

            pitch = pitch + pitchModifier * 0.1F;
            yaw += yawModifier * 0.1F;

            pitchModifier = pitchModifier * 0.9F;
            yawModifier = yawModifier * 0.75F;

            pitchModifier = pitchModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawModifier = yawModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!comesFromRoom && startCounter == randomCounterValue && width > 1.0F && endCounter > 0) {
                this.addTunnel(worldIn, random.nextLong(), originChunkX, originChunkZ, chunk, caveStartX, caveStartY, caveStartZ, random.nextFloat() * 0.5F + 0.5F, yaw - ((float) Math.PI / 2F), pitch / 3.0F, startCounter, endCounter, 1.0D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
                this.addTunnel(worldIn, random.nextLong(), originChunkX, originChunkZ, chunk, caveStartX, caveStartY, caveStartZ, random.nextFloat() * 0.5F + 0.5F, yaw + ((float) Math.PI / 2F), pitch / 3.0F, startCounter, endCounter, 1.0D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
                return;
            }

            if (comesFromRoom || random.nextInt(4) != 0) {
                double caveStartXOffsetFromCenter = caveStartX - originBlockX; // Number of blocks from current caveStartX to center of origin chunk
                double caveStartZOffsetFromCenter = caveStartZ - originBlockZ; // Number of blocks from current caveStartZ to center of origin chunk
                double distanceToEnd = endCounter - startCounter;
                double d7 = width + 2.0F + 16.0F;

                // I think this prevents caves from generating too far from the origin chunk
                if (caveStartXOffsetFromCenter * caveStartXOffsetFromCenter + caveStartZOffsetFromCenter * caveStartZOffsetFromCenter - distanceToEnd * distanceToEnd > d7 * d7) {
                    return;
                }

                // Only continue if cave start is close enough to origin
                if (caveStartX >= originBlockX - 16.0D - xzOffset * 2.0D && caveStartZ >= originBlockZ - 16.0D - xzOffset * 2.0D && caveStartX <= originBlockX + 16.0D + xzOffset * 2.0D && caveStartZ <= originBlockZ + 16.0D + xzOffset * 2.0D) {
                    int minX = Mth.floor(caveStartX - xzOffset) - originChunkX * 16 - 1;
                    int minY = Mth.floor(caveStartY - yOffset) - 1;
                    int minZ = Mth.floor(caveStartZ - xzOffset) - originChunkZ * 16 - 1;
                    int maxX = Mth.floor(caveStartX + xzOffset) - originChunkX * 16 + 1;
                    int maxY = Mth.floor(caveStartY + yOffset) + 1;
                    int maxZ = Mth.floor(caveStartZ + xzOffset) - originChunkZ * 16 + 1;

                    if (minX < 0) {
                        minX = 0;
                    }

                    if (maxX > 16) {
                        maxX = 16;
                    }

                    if (minY < 1) {
                        minY = 1;
                    }

                    if (maxY > 248) {
                        maxY = 248;
                    }

                    if (minZ < 0) {
                        minZ = 0;
                    }

                    if (maxZ > 16) {
                        maxZ = 16;
                    }

                    for (int currX = minX; currX < maxX; ++currX) {
                        // Distance along the x-axis from the center (caveStart) of this ellipsoid.
                        // You can think of this value as (x/a), where a is the length of the ellipsoid's semi-axis in the x direction.
                        double xAxisDist = ((double) (currX + originChunkX * 16) + 0.5D - caveStartX) / xzOffset;

                        for (int currZ = minZ; currZ < maxZ; ++currZ) {
                            // Distance along the z-axis from the center (caveStart) of this ellipsoid.
                            // You can think of this value as (z/b), where b is the length of the ellipsoid's semi-axis in the z direction (same as a in this case).
                            double zAxisDist = ((double) (currZ + originChunkZ * 16) + 0.5D - caveStartZ) / xzOffset;

                            // Skip column if position not marked as valid
                            if (!validPositions[currX][currZ])
                                continue;

                            // Only operate on points within ellipse on XZ axis. Avoids unnecessary computation along y axis
                            if (xAxisDist * xAxisDist + zAxisDist * zAxisDist < 1.0D) {
                                for (int currY = maxY; currY > minY; --currY) {
                                    // Distance along the y-axis from the center (caveStart) of this ellipsoid.
                                    // You can think of this value as (y/c), where c is the length of the ellipsoid's semi-axis in the y direction.
                                    double yAxisDist = ((double) (currY - 1) + 0.5D - caveStartY) / yOffset;

                                    // Only operate on points within the ellipsoid.
                                    // This conditional is validating the current coordinate against the equation of the ellipsoid, that is,
                                    // (x/a)^2 + (z/b)^2 + (y/c)^2 <= 1.
                                    if (yAxisDist > -0.7D && xAxisDist * xAxisDist + yAxisDist * yAxisDist + zAxisDist * zAxisDist < 1.0D) {
                                        liquidBlock = liquidBlocks[BetterCavesUtils.getLocal(currX)][BetterCavesUtils.getLocal(currZ)];
                                        if (this.isDebugVisualizerEnabled)
                                            CarverUtils.debugCarveBlock(chunk, currX, currY, currZ, debugBlock, true);
                                        else
                                            digBlock(worldIn, chunk, originChunkX, originChunkZ, currX, currY, currZ, liquidBlock, biomeMap, airCarvingMask, liquidCarvingMask);
                                    } else {
                                        if (this.isDebugVisualizerEnabled)
                                            CarverUtils.debugCarveBlock(chunk, currX, currY, currZ, debugBlock, false);
                                    }
                                }
                            }
                        }
                    }

                    if (comesFromRoom) {
                        break;
                    }
                }
            }
            startCounter++;
        }
    }

    private void digBlock(WorldGenLevel worldIn, ChunkAccess chunkIn, int chunkX, int chunkZ, int localX, int y, int localZ, BlockState liquidBlockState, Map<Long, Biome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Check if already carved
        int bitIndex = localX | localZ << 4 | y << 8;
        if (airCarvingMask.get(bitIndex) || liquidCarvingMask.get(bitIndex)) {
            return;
        }

        BlockPos blockPos = new BlockPos(chunkX * 16 + localX, y, chunkZ * 16 + localZ);
        ColPos.Mutable mutableColPos = new ColPos.Mutable(blockPos);

        // Determine if cave is flooded at this location
        boolean flooded = isFloodedUndergroundEnabled && !isDebugVisualizerEnabled && biomeMap.get(mutableColPos.toLong()).getBiomeCategory() == Biome.BiomeCategory.OCEAN;
        if (flooded) {
            // Cannot go above sea level
            if (y >= worldIn.getSeaLevel()) {
                return;
            }

            // Don't dig boundaries between flooded and unflooded openings.
            if (
                (BetterCavesUtils.isPosInWorld(mutableColPos.set(blockPos).move(Direction.EAST), world) && biomeMap.get(mutableColPos.set(blockPos).move(Direction.EAST).toLong()).getBiomeCategory() != Biome.BiomeCategory.OCEAN) ||
                (BetterCavesUtils.isPosInWorld(mutableColPos.set(blockPos).move(Direction.WEST), world) && biomeMap.get(mutableColPos.set(blockPos).move(Direction.WEST).toLong()).getBiomeCategory() != Biome.BiomeCategory.OCEAN) ||
                (BetterCavesUtils.isPosInWorld(mutableColPos.set(blockPos).move(Direction.NORTH), world) && biomeMap.get(mutableColPos.set(blockPos).move(Direction.NORTH).toLong()).getBiomeCategory() != Biome.BiomeCategory.OCEAN) ||
                (BetterCavesUtils.isPosInWorld(mutableColPos.set(blockPos).move(Direction.SOUTH), world) && biomeMap.get(mutableColPos.set(blockPos).move(Direction.SOUTH).toLong()).getBiomeCategory() != Biome.BiomeCategory.OCEAN)
            ) {
                return;
            }
        }

        // Carve block
        if (flooded) {
            CarverUtils.carveFloodedBlock(chunkIn, rand, blockPos.mutable(), liquidBlockState, liquidAltitude, liquidCarvingMask);
        }
        else {
            CarverUtils.carveBlock(chunkIn, blockPos, liquidBlockState, this.liquidAltitude, this.isReplaceGravelEnabled, airCarvingMask);
        }
    }

    public void setWorld(WorldGenLevel worldIn) {
        this.world = worldIn;
    }
}
