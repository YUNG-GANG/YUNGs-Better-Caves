package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects generation of liquid regions at the start of the first phase of chunk generation,
 * which is basically as early as possible.
 * This works in tandem with {@link ChunkGeneratorMixin} to ensure liquid regions are generated.
 * These need to be generated before Aquifers are generated - see {@link AquiferMixin}.
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "onStructureStartsAvailable", at = @At("HEAD"))
    private void bettercaves$generateLiquidRegions2(ChunkAccess chunkAccess, CallbackInfo ci) {
        if (!LiquidRegionsController.getInstance().hasSettingsForLevel((ServerLevel) (Object) this)) {
            return; // No liquid regions to generate for this level
        }

        LiquidRegionsController.getInstance()
                .getLiquidRegionsForServerLevel((ServerLevel) (Object) this)
                .getOrCreateLiquidBlocksForChunk(chunkAccess.getPos());
    }
}
