package com.yungnickyoung.minecraft.bettercaves.worldgen.context;

import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Context for creation of an Aquifer. Set by
 * {@link com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix.ChunkMapMixin}
 * and consumed by {@link com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix.AquiferMixin}
 */
@NullMarked
public record AquiferContext(@Nullable LiquidRegions liquidRegions) {
    private static final ScopedValue<AquiferContext> CONTEXT = ScopedValue.newInstance();

    public static <R, X extends Throwable> R call(LiquidRegions liquidRegions, ScopedValue.CallableOp<R, X> callable) throws X {
        return ScopedValue.where(CONTEXT, new AquiferContext(liquidRegions))
                .call(callable);
    }

    public static <R, X extends Throwable> R callWithoutRegions(ScopedValue.CallableOp<R, X> callable) throws X {
        return ScopedValue.where(CONTEXT, new AquiferContext(null))
                .call(callable);
    }

    public static @Nullable AquiferContext get() {
        return CONTEXT.isBound() ? CONTEXT.get() : null;
    }
}