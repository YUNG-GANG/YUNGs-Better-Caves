package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class BubbleCarverBC extends CaveWorldCarver
{

    public BubbleCarverBC(Function<Dynamic<?>, ? extends ProbabilityConfig> deserialize, int maxCarveHeight)
    {
        super(deserialize, maxCarveHeight);

        /*
         * Remove water from carvable fluids because the name is actually wrong.
         * What this does is it actually prevents the carver from carving into
         * any chunk that contains any of the listed fluids and makes the carver
         * look terrible with uncarved chunks sticking out.
         */
        carvableFluids = ImmutableSet.of();
    }


    /**
     * Main height of the caves (Spheres in this case).
     * Can be randomized with random parameter
     */
    @Override
    protected int generateCaveStartY(Random random)
    {
        return random.nextInt(3) + 40;
    }


    /**
     * This is what typically calls carveCave (func_222723_a) and carveTunnel.
     * Here, we are doing just carveCave as we do not need tunnels. Just the cave room.
     */
    public boolean carve(IChunk chunk, Random random, int seaLevel,
                         int cX, int cZ, int chunkX, int chunkZ,
                         BitSet caveMask, ProbabilityConfig config)
    {
        int numberOfRooms = 3;

        for (int roomCount = 0; roomCount < numberOfRooms; ++roomCount)
        {
            double x = (double) (cX * 16); // + random.nextInt(16));   // Randomizes spot of each room
            double y = (double) this.generateCaveStartY(random) - roomCount * 13;  // Lowers each room by 11 blocks so they are stacked
            double z = (double) (cZ * 16); //+ random.nextInt(16));   // Randomizes spot of each room

            /* How big the sphere carved is.
             * It also shrinks each spheres' radius by 1 as each gets generated downward.
             */
            float caveRadius = 20.0F + random.nextFloat() * 10.0F - roomCount;

            /* This is called carveCave in my yarn over mcp mapping and is used to carve a sphere.
             * The 0.4D is how squished the radius is in the y direction. This is now 40% of the original radius for y.
             */
            this.func_222723_a(chunk, random.nextLong(), seaLevel, chunkX, chunkZ, x, y, z, caveRadius, 0.4D, caveMask);

            /* If you don't what perfect spheres to be carved, you can override func_222705_a and
             * specify your own algorithm for other shapes as well.
             */
        }
        return true;
    }


    /**
     * Does the actual carving. Replacing any valid stone with cave air.
     * Though the carver could be customized to place blocks instead which would be interesting.
     */
    @Override
    protected boolean carveBlock(IChunk chunk, BitSet carvingMask, Random random,
                                   BlockPos.MutableBlockPos posHere, BlockPos.MutableBlockPos posAbove, BlockPos.MutableBlockPos posBelow,
                                   int seaLevel, int chunkX, int chunkZ, int globalX, int globalZ,
                                   int x, int y, int z, AtomicBoolean foundSurface)
    {
        /*
         * Not sure what this specific section is doing.
         * I know this mask is used so other features can find caves space.
         * Used by SeaGrass to generate at cave openings underwater
         */
        int index = x | z << 4 | y << 8;
        if (carvingMask.get(index))
        {
            return false;
        }
        carvingMask.set(index);

        posHere.setPos(globalX, y, globalZ);
        BlockState blockState = chunk.getBlockState(posHere);
        BlockState blockStateAbove = chunk.getBlockState(posAbove.setPos(posHere).move(Direction.UP));

        // Makes sure we aren't carving a non terrain or liquid space
        if (!this.canCarveBlock(blockState, blockStateAbove) || isBorderingLiquid(chunk, posHere))
        {
            return false;
        }

        /*
         * Here's an idea! You can attach a perlin/octave noise generator to this class and
         * in this exact spot, you can do a check for if the noise generator's value for this
         * spot is valid or not. This can let you do all kinds of cool things.
         *
         * In my own mod (Ultra Amplified Mod), I used two octaveNoiseGenerators for my cave cavern carver.
         * One noise generator was to make stalactites and the other was to make giant pillars/rounded stalagmites.
         * However, it took a lot of time to tweak the noise generators and modify the values it spits out till
         * I got it to look good. Also, if you do use octaveNoiseGenerator, I recommend using an octave of 1 so
         * it is OctavesNoiseGenerator(new SharedSeedRandom(seed), 1, 0) because higher octaves will slow
         * world generation a bit.
         */

        // carves air when above lava level
        if (y > 10)
        {
            chunk.setBlockState(posHere, CAVE_AIR, false);
        }
        // sets lava below lava level
        else
        {
            chunk.setBlockState(posHere, LAVA.getBlockState(), false);
        }

        return true;
    }


    /**
     * Used to determine what blocks the carver can carve through.
     * Can be highly customized.
     */
    @Override
    protected boolean canCarveBlock(BlockState blockState, BlockState aboveBlockState)
    {
        if (blockState.getBlock() == Blocks.BEDROCK)
            return false;

        Material material = blockState.getMaterial();

        //the current block must be rock, dirt, organic and the above block must not be liquid, sand, or gravel
        return (material == Material.ROCK || material == Material.EARTH || material == Material.ORGANIC) &&
                (aboveBlockState.getFluidState().isEmpty() && aboveBlockState != Blocks.SAND.getDefaultState() && aboveBlockState != Blocks.GRAVEL.getDefaultState());
    }

    /**
     * Used to determine if the position is bordering a fluid.
     *
     * Works like 99% of the time in ocean biomes except for when the
     * ocean floor changes height sharply and the fluid is on the edge of a chunk.
     * Read the large comment inside this method for more details.
     */
    protected boolean isBorderingLiquid(IChunk chunk, BlockPos.MutableBlockPos position)
    {
        BlockPos.MutableBlockPos borderingPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            borderingPos.setPos(position).move(direction);

            /* Checks if there is liquid horizontally.
             *
             * We cannot check beyond chunk edges as it will actually loop back around due to the
             * mod operation that chunk.getBlockState does internally.
             *
             * So this means that we could accidentally carve and expose liquids but only if the liquid is bordering
             * just outside the current chunk that the carver is carving in. But for the most part, this will work
             * most of the time and help prevent exposing water in the ocean biomes.
             *
             * The best solution would be to create a Feature that scans the chunk for water that is bordering air and
             * places Stone so the water is contained. Pick GenerationStage.Decoration.LOCAL_MODIFICATIONS so the
             * feature runs immediately after the carvers and before any other feature can add water to the chunk.
             *
             * But for right now, this is just a proof-of-concept carver to play around with.
             */
            if((int)(borderingPos.getX() / 16) == chunk.getPos().x &&
               (int)(borderingPos.getZ() / 16)  == chunk.getPos().z &&
               chunk.getBlockState(position.offset(direction)).getMaterial() == Material.WATER)
            {
                return true;
            }
        }

        //extra check for above. We don't need to check below as fluids cant flow upward
        if(chunk.getBlockState(position.up()).getMaterial() == Material.WATER)
        {
            return true;
        }

        return false;
    }
}