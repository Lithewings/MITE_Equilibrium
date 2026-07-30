package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Strider.class)
public abstract class StriderEntityMixin extends Animal implements ItemSteerable, Saddleable {
    @Shadow
    @Final
    private ItemBasedSteering steering;

    protected StriderEntityMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }
    @Shadow
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
//        if (player.getMainHandStack()==ItemStack.EMPTY && this.isSaddled()) {
//            this.saddledComponent.setSaddled(false);
//            player.getInventory().insertStack(player.getInventory().selectedSlot,Items.SADDLE.getDefaultStack());
//            return ActionResult.success(this.getWorld().isClient);
//        }
        boolean bl = this.isFood(player.getItemInHand(hand));
        if (!bl && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if(this.isSaddled() && player.getMainHandItem().isEmpty()) {
            this.ejectPassengers();
            this.steering.setSaddle(false);
            player.getInventory().placeItemBackInInventory(Items.SADDLE.getDefaultInstance());
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }



        else {
            InteractionResult actionResult = super.mobInteract(player, hand);
            if (!actionResult.consumesAction()) {
                ItemStack itemStack = player.getItemInHand(hand);
                return itemStack.is(Items.SADDLE) ? itemStack.interactLivingEntity(player, this, hand) : InteractionResult.PASS;
            } else {
                if (bl && !this.isSilent()) {
                    this.level()
                            .playSound(
                                    null,
                                    this.getX(),
                                    this.getY(),
                                    this.getZ(),
                                    SoundEvents.STRIDER_EAT,
                                    this.getSoundSource(),
                                    1.0F,
                                    1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                            );
                }

                return actionResult;
            }
        }
    }

}
