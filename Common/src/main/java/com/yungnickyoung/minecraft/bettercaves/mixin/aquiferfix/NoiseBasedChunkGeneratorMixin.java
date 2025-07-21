package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.mixin.accessor.StructureManagerAccessor;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to push and pop the AquiferContext at the start and end of the noise & biome generation steps of worldgen.
 * This should ensure that the AquiferContext is available for aquifer-related operations during these steps.
 * This is necessary because the doFill and doCreateBiomes methods of NoiseBasedChunkGenerator are called in a new thread,
 * so the AquiferContext needs to be re-pushed at the start of the method.
 */
@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {
    @Inject(method = "doFill", at = @At("HEAD"))
    private void bettercaves$doFill(Blender $$0, StructureManager structureManager, RandomState $$2, ChunkAccess $$3, int $$4, int $$5, CallbackInfoReturnable<ChunkAccess> cir) {
        AquiferContext aquiferContext = AquiferContext.peek();

        // We expect the AquiferContext to be null here, since doFill is called in a new thread
        if (aquiferContext == null) {
            LevelAccessor levelAccessor = ((StructureManagerAccessor) structureManager).getLevel();

            // We expect the levelAccessor to be an instance of WorldGenRegion, based on how it's constructed
            // in the NOISE ChunkStatus
            if (levelAccessor instanceof WorldGenRegion region) {
                AquiferContext.push(region.getLevel());
            }
        }
    }

    @Inject(method = "doFill", at = @At("RETURN"))
    private void bettercaves$doFill2(Blender $$0, StructureManager structureManager, RandomState $$2, ChunkAccess $$3, int $$4, int $$5, CallbackInfoReturnable<ChunkAccess> cir) {
        AquiferContext.pop();
    }

    @Inject(method = "doCreateBiomes", at = @At("HEAD"))
    private void bettercaves$doCreateBiomes(Blender $$0, RandomState $$1, StructureManager structureManager, ChunkAccess $$3, CallbackInfo ci) {
        AquiferContext aquiferContext = AquiferContext.peek();

        // We expect the AquiferContext to be null here, since doCreateBiomes is called in a new thread
        if (aquiferContext == null) {
            LevelAccessor levelAccessor = ((StructureManagerAccessor) structureManager).getLevel();

            // We expect the levelAccessor to be an instance of WorldGenRegion, based on how it's constructed
            // in the BIOMES ChunkStatus
            if (levelAccessor instanceof WorldGenRegion region) {
                AquiferContext.push(region.getLevel());
            }
        }
    }

    @Inject(method = "doCreateBiomes", at = @At("RETURN"))
    private void bettercaves$doCreateBiomes2(Blender $$0, RandomState $$1, StructureManager $$2, ChunkAccess $$3, CallbackInfo ci) {
        AquiferContext.pop();
    }
}
