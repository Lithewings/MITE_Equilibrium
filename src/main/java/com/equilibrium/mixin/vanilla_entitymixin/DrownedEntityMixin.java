package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Drowned.class)
public abstract class DrownedEntityMixin extends Zombie implements RangedAttackMob {
    @Shadow protected abstract boolean canReplaceCurrentItem(ItemStack newStack, ItemStack oldStack);

    public DrownedEntityMixin(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean bl = super.doHurtTarget(target);
        Level world = this.level();
        //玩家头顶接触水时执行氧气值减少的攻击特效
        if (bl && target instanceof Player player && world.getBlockState(player.blockPosition().above()).is(Blocks.WATER)) {
            player.setAirSupply(Math.clamp(player.getAirSupply()-90,-20,player.getMaxAirSupply()));
        }

        return bl;
    }
}
