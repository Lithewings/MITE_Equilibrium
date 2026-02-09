package com.equilibrium.mixin.entitymixin;

import com.equilibrium.item.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.entity.utilForEntity.forPlayerIsEnchantedItemCauseDamage;
import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(BlazeEntity.class)
public class BlazeEntityMixin extends HostileEntity {


    protected BlazeEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public int getXpToDrop() {
        return getXpForLevel(3);
    }


    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.getAttacker() instanceof PlayerEntity
                ? !forPlayerIsEnchantedItemCauseDamage(damageSource)
                : super.isInvulnerableTo(damageSource);
    }

}