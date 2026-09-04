package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.duck.ILiquidRegionsProvider;
import com.yungnickyoung.minecraft.bettercaves.services.Services;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Apply LiquidRegions changes to aquifers.
 * This is where all the LiquidRegions stuff actually has an effect.
 * The LiquidRegions are passed in through {@link AquiferContext}.
 */
@NullMarked
@Mixin(Aquifer.NoiseBasedAquifer.class)
public class AquiferMixin implements ILiquidRegionsProvider {
    @Unique
    private static final AtomicBoolean hasWarned = new AtomicBoolean(false);

    /**
     * Some mods will change worldgen enough that they will interfere
     */
    @Unique
    private static final List<String> OK_TO_INTERFERE_MODS = List.of(
            /*
            Moderner Beta changes world gen all the way through, so our AquiferContext never gets attached. It's not really
            an issue though because this doesn't seem to result in any visible cave weirdness: from flying around I couldn't
            see any of the normal lava-water mixing issues that show up when AquiferContext doesn't work.
             */
            "moderner_beta"
    );

    @Unique
    private @Nullable LiquidRegions liquidRegions;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bettercaves$setLiquidRegions(final NoiseChunk noiseChunk,
                                              final ChunkPos pos,
                                              final NoiseRouter router,
                                              final PositionalRandomFactory positionalRandomFactory,
                                              final int minBlockY,
                                              final int yBlockSize,
                                              final Aquifer.FluidPicker globalFluidPicker,
                                              final CallbackInfo ci) {
        var context = AquiferContext.get();
        if (context == null) {
            if (!hasWarned.getAndSet(true)) {
                warnAboutAquifer();
            }
        } else if (context.liquidRegions() != null) {
            this.liquidRegions = context.liquidRegions();
        }
    }

    @Unique
    private static void warnAboutAquifer() {
        var mods = OK_TO_INTERFERE_MODS.stream()
                .filter(Services.PLATFORM::isModLoaded)
                .toList();
        if (mods.isEmpty()) {
            StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
            var stackTrace = stackWalker.walk(sfs -> sfs.skip(1)
                    .map(sf -> sf.toStackTraceElement().toString())
                    .collect(Collectors.joining("\n")));
            BetterCavesCommon.LOGGER.error("Failed to fetch the AquiferContext. Liquid Regions for YUNG's Better Caves may not generate properly.");
            BetterCavesCommon.LOGGER.error("Please report this issue to YUNG's Better Caves, including this stack trace: {}", stackTrace);
            BetterCavesCommon.LOGGER.error("This is a mod compatibility issue. Please report it to the YUNG's Better Caves GitHub issue tracker!");
        } else {
            BetterCavesCommon.LOGGER.warn("Failed to fetch the AquiferContext for YUNG's Better Caves.");
            if (mods.size() == 1) {
                BetterCavesCommon.LOGGER.warn("This is likely because you have the mod '{}' installed.", mods.get(0));
            } else {
                BetterCavesCommon.LOGGER.warn("This is likely because of the following mods: {}.", String.join(", ", mods));
            }
            BetterCavesCommon.LOGGER.warn("If caves generate fine, there's nothing to worry about. If caves seem to generate weirdly, you can report that to the YUNG's Better Caves GitHub issue tracker.");
        }
    }

    /**
     * Replaces Aquifer-generated liquids at and below the liquidAltitude with the proper Better Caves liquid,
     * as defined by the LiquidRegions data for the current chunk.
     */
    @Inject(method = "computeSubstance", at = @At("RETURN"), cancellable = true)
    private void bettercaves$fixAquiferLiquids(DensityFunction.FunctionContext context,
                                               double d,
                                               CallbackInfoReturnable<@Nullable BlockState> cir) {
        // Only modify aquifers if LiquidRegions are enabled for the current level
        if (this.liquidRegions == null) {
            return;
        }

        BlockState blockState = cir.getReturnValue();
        if (blockState == null) return; // Only modify air or liquid blocks

        // Fetch the LiquidRegions data for the current chunk.
        // If the LiquidRegions data has been generated before, that cached result will be reused.
        ChunkPos chunkPos = ChunkPos.containing(new BlockPos(context.blockX(), context.blockY(), context.blockZ()));
        LiquidRegions.CacheData cacheData = this.liquidRegions.getOrCreateLiquidBlocksForChunk(chunkPos);
        if (cacheData == null) {
            BetterCavesCommon.LOGGER.warn("No LiquidRegions data found for chunk {} in AquiferMixin, this should not happen!", chunkPos);
            return;
        }

        if (context.blockY() > cacheData.liquidAltitude()) {
            return; // Only modify if it's at or below the liquid altitude for this position
        }

        int localX = context.blockX() & 15;
        int localZ = context.blockZ() & 15;
        BlockState liquidBlock = cacheData.liquidBlocks()[localX][localZ];

        // Only modify if the block is different from the liquid block it should be
        if (liquidBlock == null || !blockState.is(liquidBlock.getBlock())) {
            cir.setReturnValue(liquidBlock);
        }
    }

    @Override public @Nullable LiquidRegions bettercaves$getLiquidRegions() {
        return this.liquidRegions;
    }
}
