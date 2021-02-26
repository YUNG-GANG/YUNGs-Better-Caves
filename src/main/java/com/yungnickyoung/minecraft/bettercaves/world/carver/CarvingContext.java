package com.yungnickyoung.minecraft.bettercaves.world.carver;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.BitSet;

public class CarvingContext {

    private static final ThreadLocal<CarvingContext> CONTEXT = new ThreadLocal<>();

    private final ProtoChunk protoChunk;
    private final WeakReference<ServerWorld> world;

    public CarvingContext(ServerWorld world, ProtoChunk protoChunk) {
        this.world = new WeakReference<>(world);
        this.protoChunk = protoChunk;
    }

    public ServerWorld getWorld() {
        return world.get();
    }

    public BitSet getMask(GenerationStep.Carver carvingStage) {
        return protoChunk.getOrCreateCarvingMask(carvingStage);
    }

    /**
     * Consume the currently held CarvingContext.
     * A null value means we are in the wrong generation stage, or the context has already been consumed.
     */
    @Nullable
    public static CarvingContext pop() {
        CarvingContext context = CONTEXT.get();
        CONTEXT.set(null);
        return context;
    }

    /**
     * Peek the currently held CarvingContext without consuming it.
     * A null value means we are in the wrong generation stage, or the context has already been consumed.
     */
    @Nullable
    public static CarvingContext peek() {
        return CONTEXT.get();
    }

    /**
     * Should only be called during the air carving stage (ChunkStatus.CARVERS).
     */
    public static void push(ServerWorld world, Chunk chunk) {
        if (chunk instanceof ProtoChunk) {
            CONTEXT.set(new CarvingContext(world, (ProtoChunk) chunk));
        } else if (chunk != null) {
            // Shouldn't ever happen unless another mod has done a similar hook in ChunkStatus and changed the IChunk type
            BetterCaves.LOGGER.error("ERROR: Attempted to push invalid IChunk implementation to CarvingContext: {}", chunk.getClass());
        }
    }
}