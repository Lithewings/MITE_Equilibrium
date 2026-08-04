package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerEntityMixin extends Zombie implements VillagerDataHolder {
    public ZombieVillagerEntityMixin(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
    }
    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_CONVERTING_ID;
    @Shadow
    private int villagerConversionTime;
    @Shadow
    private UUID conversionStarter;


    @Shadow
    private void startConverting(@Nullable UUID uuid, int delay) {
        this.conversionStarter = uuid;
        this.villagerConversionTime = delay;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, delay, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, EntityEvent.ZOMBIE_CONVERTING);
    }


    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(1);
    }




    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                itemStack.consume(1, player);
                if (!this.level().isClientSide) {
                    this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        }else if(itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                itemStack.consume(1, player);
                if (!this.level().isClientSide) {
                    this.startConverting(player.getUUID(), 5);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }
}
