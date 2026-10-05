package com.yungnickyoung.minecraft.bettercaves.worldgen.context;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class CavegenContext {
    private static final ThreadLocal<CavegenContext> CONTEXT = new ThreadLocal<>();

    private final WeakReference<ServerLevel> serverLevel;
    private ChunkAccess chunk;
    private Aquifer aquifer;

    public CavegenContext(ServerLevel serverLevel) {
        this.serverLevel = new WeakReference<>(serverLevel);
    }

    public ServerLevel getServerLevel() {
        return serverLevel.get();
    }

    public ChunkAccess getChunk() {
        return chunk;
    }

    public Aquifer getAquifer() {
        return aquifer;
    }

    public static void attach(ChunkAccess chunk, Aquifer aquifer) {
        CavegenContext context = CONTEXT.get();
        if (context != null) {
            context.chunk = chunk;
            context.aquifer = aquifer;
        }
    }

    /**
     * Consume the currently held CavegenContext.
     * A null value means we are in the wrong generation stage, or the context has already been consumed.
     */
    @Nullable
    public static CavegenContext pop() {
        CavegenContext context = CONTEXT.get();
        CONTEXT.remove();
        return context;
    }

    /**
     * Peek the currently held CavegenContext without consuming it.
     * A null value means we are in the wrong generation stage, or the context has already been consumed.
     */
    @Nullable
    public static CavegenContext peek() {
        return CONTEXT.get();
    }

    /**
     * Should only be called during the air carving stage (ChunkStatus.CARVERS).
     */
    public static void push(ServerLevel world) {
        CONTEXT.set(new CavegenContext(world));
    }
}
