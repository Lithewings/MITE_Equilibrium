package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StriderEntity.class)
public abstract class StriderEntityMixin extends AnimalEntity implements ItemSteerable, Saddleable {
    @Shadow public abstract boolean isCold();
    @Shadow
    @Final
    private SaddledComponent saddledComponent;



    protected StriderEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    @Shadow
    public boolean isSaddled() {
        return this.saddledComponent.isSaddled();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
//        if (player.getMainHandStack()==ItemStack.EMPTY && this.isSaddled()) {
//            this.saddledComponent.setSaddled(false);
//            player.getInventory().insertStack(player.getInventory().selectedSlot,Items.SADDLE.getDefaultStack());
//            return ActionResult.success(this.getWorld().isClient);
//        }
        boolean bl = this.isBreedingItem(player.getStackInHand(hand));
        if (!bl && this.isSaddled() && !this.hasPassengers() && !player.shouldCancelInteraction()) {
            if (!this.getWorld().isClient) {
                player.startRiding(this);
            }

            return ActionResult.success(this.getWorld().isClient);
        } else if(this.isSaddled() && player.getMainHandStack().isEmpty()) {
            this.removeAllPassengers();
            this.saddledComponent.setSaddled(false);
            player.getInventory().offerOrDrop(Items.SADDLE.getDefaultStack());
            return ActionResult.success(this.getWorld().isClient);
        }



        else {
            ActionResult actionResult = super.interactMob(player, hand);
            if (!actionResult.isAccepted()) {
                ItemStack itemStack = player.getStackInHand(hand);
                return itemStack.isOf(Items.SADDLE) ? itemStack.useOnEntity(player, this, hand) : ActionResult.PASS;
            } else {
                if (bl && !this.isSilent()) {
                    this.getWorld()
                            .playSound(
                                    null,
                                    this.getX(),
                                    this.getY(),
                                    this.getZ(),
                                    SoundEvents.ENTITY_STRIDER_EAT,
                                    this.getSoundCategory(),
                                    1.0F,
                                    1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                            );
                }

                return actionResult;
            }
        }
    }

}
