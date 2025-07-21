package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.mixin.accessor.StructureManagerAccessor;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects generation of liquid regions at the start of the first phase of chunk generation,
 * which is basically as early as possible.
 * These need to be generated before Aquifers are generated - see {@link AquiferMixin}.
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(method = "createStructures", at = @At("HEAD"))
    private void bettercaves$generateLiquidRegions1(RegistryAccess $$0, ChunkGeneratorStructureState $$1, StructureManager $$2, ChunkAccess chunkAccess, StructureTemplateManager $$4, CallbackInfo ci) {
        if (((StructureManagerAccessor) $$2).getLevel() instanceof ServerLevel serverLevel) {
            LiquidRegionsController.getInstance()
                    .getLiquidRegionsForServerLevel(serverLevel)
                    .getOrCreateLiquidBlocksForChunk(chunkAccess.getPos());
        } else {
            BetterCavesCommon.LOGGER.error("Better Caves expected a ServerLevel, but got: {}", ((StructureManagerAccessor) $$2).getLevel().getClass().getName());
            BetterCavesCommon.LOGGER.error("Liquid regions will not be generated for this chunk. This may be fatal!");
        }
    }
}
