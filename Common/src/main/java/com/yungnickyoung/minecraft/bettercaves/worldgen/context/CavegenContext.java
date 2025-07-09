package com.yungnickyoung.minecraft.bettercaves.worldgen.context;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class CavegenContext {
    private static final ThreadLocal<CavegenContext> CONTEXT = new ThreadLocal<>();

    private final WeakReference<ServerLevel> serverLevel;

    public CavegenContext(ServerLevel serverLevel) {
        this.serverLevel = new WeakReference<>(serverLevel);
    }

    public ServerLevel getServerLevel() {
        return serverLevel.get();
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
