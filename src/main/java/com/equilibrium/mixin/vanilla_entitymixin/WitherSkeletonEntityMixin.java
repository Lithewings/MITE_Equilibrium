package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(WitherSkeleton.class)
public class WitherSkeletonEntityMixin extends Monster {
    protected WitherSkeletonEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(3);
    }




    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getEntity() instanceof Player
                ? !forPlayerIsEnchantedItemCauseDamage(damageSource)
                : super.isInvulnerableTo(damageSource);
    }

}
