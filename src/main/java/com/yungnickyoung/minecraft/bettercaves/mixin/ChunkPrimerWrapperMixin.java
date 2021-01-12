/*
 * 100% credit goes to TelepathicGrunt for this!
 *
 * Check out his mods on CurseForge:
 * https://www.curseforge.com/members/telepathicgrunt/projects
 */

package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.world.IServerWorldHolder;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkPrimerWrapper.class)
public class ChunkPrimerWrapperMixin implements IServerWorldHolder {
    @Unique
    private ServerWorld serverWorld;

    @Unique
    private boolean carved;

    @Override
    public void setServerWorld(ServerWorld serverWorld) {
        this.serverWorld = serverWorld;
    }

    @Override
    public ServerWorld getServerWorld() {
        return this.serverWorld;
    }

    @Override
    public void setCarved(boolean carved) {
        this.carved = carved;
    }

    @Override
    public boolean hasBeenCarved() {
        return this.carved;
    }
}