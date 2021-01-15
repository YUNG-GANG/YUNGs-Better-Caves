package com.yungnickyoung.minecraft.bettercaves.world.carver;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.BitSet;

public class CarvingContext {

    private static final ThreadLocal<CarvingContext> CONTEXT = new ThreadLocal<>();

    private final ChunkPrimer chunkPrimer;
    private final WeakReference<ServerWorld> world;

    public CarvingContext(ServerWorld world, ChunkPrimer chunkPrimer) {
        this.world = new WeakReference<>(world);
        this.chunkPrimer = chunkPrimer;
    }

    public ServerWorld getWorld() {
        return world.get();
    }

    public BitSet getMask(GenerationStage.Carving stage) {
        return chunkPrimer.getOrAddCarvingMask(stage);
    }

    /**
     * Consume the currently held CarvingContext.
     * A null value means we are in the wrong generation stage.
     */
    @Nullable
    public static CarvingContext pop() {
        CarvingContext context = CONTEXT.get();
        CONTEXT.set(null);
        return context;
    }

    /**
     * Should only be called during the air carving stage (ChunkStatus.CARVERS).
     */
    public static void push(ServerWorld world, IChunk chunk) {
        if (chunk instanceof ChunkPrimer) {
            CONTEXT.set(new CarvingContext(world, (ChunkPrimer) chunk));
        } else if (chunk != null) {
            // Shouldn't ever happen unless another mod has done a similar hook in ChunkStatus and changed the IChunk type
            BetterCaves.LOGGER.error("ERROR: Attempted to push invalid IChunk implementation to CarvingContext: {}", chunk.getClass());
        }
    }
}
