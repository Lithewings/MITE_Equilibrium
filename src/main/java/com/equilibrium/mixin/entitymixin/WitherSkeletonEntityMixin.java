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
        boolean originCondition =  this.isRemoved()
                || this.isInvulnerable() && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isSourceCreativePlayer()
                || damageSource.isIn(DamageTypeTags.IS_FIRE) && this.isFireImmune()
                || damageSource.isIn(DamageTypeTags.IS_FALL) && this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE);


        if (damageSource.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().getEnchantments().isEmpty())
            return true;
        else {
            return originCondition;
        }
    }
}
