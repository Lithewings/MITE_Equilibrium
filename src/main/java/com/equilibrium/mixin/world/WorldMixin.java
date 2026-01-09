package com.equilibrium.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, AutoCloseable{

    @Shadow
    public WorldChunk getChunk(int i, int j) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }


    @Shadow
    public BlockState getBlockState(BlockPos pos) {
        if (this.isOutOfHeightLimit(pos)) {
            return Blocks.VOID_AIR.getDefaultState();
        } else {
            WorldChunk worldChunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
            return worldChunk.getBlockState(pos);
        }
    }


    @Shadow public abstract <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate);

    @Inject(method = "removeBlock",at = @At(value = "HEAD"), cancellable = true)
    public void removeBlock(BlockPos pos, boolean move, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.getBlockState(pos);
        if(blockState.isOf(Blocks.BEDROCK)) {
            List<PlayerEntity> list = this.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), new Box(pos.getX()-8,pos.getY()-8,pos.getZ()-8,pos.getX()+8,pos.getY()+8,pos.getZ()+8),PlayerEntity::isPlayer);
            for(PlayerEntity player : list) {
                if(!player.isCreative() && player.getWorld().getRegistryKey()==RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "underworld"))){
                    cir.setReturnValue(false);
                    player.damage(player.getDamageSources().badRespawnPoint(player.getPos()), 114514);
                }
            }
        }
    }
}
