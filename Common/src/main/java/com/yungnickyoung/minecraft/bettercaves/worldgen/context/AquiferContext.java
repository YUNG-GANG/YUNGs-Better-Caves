package com.yungnickyoung.minecraft.bettercaves.worldgen.context;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class AquiferContext {
    private static final ThreadLocal<AquiferContext> CONTEXT = new ThreadLocal<>();

    private final WeakReference<ServerLevel> serverLevel;

    public AquiferContext(ServerLevel serverLevel) {
        this.serverLevel = new WeakReference<>(serverLevel);
    }

    public ServerLevel getServerLevel() {
        return serverLevel.get();
    }

    @Nullable
    public static AquiferContext pop() {
        AquiferContext context = CONTEXT.get();
        CONTEXT.remove();
        return context;
    }

    @Nullable
    public static AquiferContext peek() {
        return CONTEXT.get();
    }

    public static void push(ServerLevel world) {
        CONTEXT.set(new AquiferContext(world));
    }
}
