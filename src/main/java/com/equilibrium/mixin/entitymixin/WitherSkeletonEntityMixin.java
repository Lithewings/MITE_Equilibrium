package com.equilibrium.mixin.entitymixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(WitherSkeletonEntity.class)
public class WitherSkeletonEntityMixin extends HostileEntity {
    protected WitherSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public int getXpToDrop(){
        return getXpForLevel(3);
    }




    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getSource() instanceof PlayerEntity
                ? !forPlayerIsEnchantedItemCauseDamage(damageSource)
                : super.isInvulnerableTo(damageSource);
    }

}
