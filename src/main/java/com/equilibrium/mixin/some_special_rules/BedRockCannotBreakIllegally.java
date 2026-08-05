package com.equilibrium.mixin.some_special_rules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@Mixin(Level.class)
public abstract class BedRockCannotBreakIllegally implements LevelAccessor, AutoCloseable{

    @Shadow
    public LevelChunk getChunk(int i, int j) {
        return (LevelChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }


    @Shadow
    public BlockState getBlockState(BlockPos pos) {
        if (this.isOutsideBuildHeight(pos)) {
            return Blocks.VOID_AIR.defaultBlockState();
        } else {
            LevelChunk worldChunk = this.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
            return worldChunk.getBlockState(pos);
        }
    }


    @Shadow public abstract <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> filter, AABB box, Predicate<? super T> predicate);

    @Inject(method = "removeBlock",at = @At(value = "HEAD"), cancellable = true)
    public void removeBlock(BlockPos pos, boolean move, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.getBlockState(pos);
        if(blockState.is(Blocks.BEDROCK)) {
            List<Player> list = this.getEntities(EntityTypeTest.forClass(Player.class), new AABB(pos.getX()-8,pos.getY()-8,pos.getZ()-8,pos.getX()+8,pos.getY()+8,pos.getZ()+8),Player::isAlwaysTicking);
            for(Player player : list) {
                if(!player.isCreative() && player.level().dimension()==ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID, "underworld"))){
                    cir.setReturnValue(false);
                    player.hurt(player.damageSources().badRespawnPointExplosion(player.position()), 114514);
                }
            }
        }
    }
}
