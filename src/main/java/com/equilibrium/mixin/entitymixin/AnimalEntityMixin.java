package com.equilibrium.mixin.entitymixin;

import com.equilibrium.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {
    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }


    @Unique
    public int itemLayTime = this.random.nextInt(6000) + 18000;

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient && this.isAlive() && !this.isBaby() && --this.itemLayTime <= 0) {
            //肥料制造机器
            this.dropItem(ModItems.MANURE);
            this.emitGameEvent(GameEvent.ENTITY_PLACE);
            this.itemLayTime = this.random.nextInt(6000) + 18000;
        }
    }



    @Override

    public DamageSource getRecentDamageSource() {
        if (this.getWorld().getTime() -this.lastDamageTime > 1600L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }

}
