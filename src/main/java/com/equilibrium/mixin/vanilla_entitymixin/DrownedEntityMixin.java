package com.equilibrium.mixin.vanilla_entitymixin;

import com.jcraft.jorbis.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrownedEntity.class)
public abstract class DrownedEntityMixin extends ZombieEntity implements RangedAttackMob {
    @Shadow protected abstract boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack);

    public DrownedEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        World world = this.getWorld();
        //玩家头顶接触水时执行氧气值减少的攻击特效
        if (bl && target instanceof PlayerEntity player && world.getBlockState(player.getBlockPos().up()).isOf(Blocks.WATER)) {
            player.setAir(Math.clamp(player.getAir()-90,-20,player.getMaxAir()));
        }

        return bl;
    }
}
