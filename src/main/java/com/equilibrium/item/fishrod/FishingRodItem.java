package com.equilibrium.item.fishrod;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;


public class FishingRodItem extends Item implements AnvilRepairRuleView {

    private final FishingRodMaterial material;

    public FishingRodItem(FishingRodMaterial material, Properties settings) {
        super(settings);
        this.material = material;
    }

    public FishingRodMaterial getRodMaterial() {
        return this.material;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (user.fishing != null) {
            if (!world.isClientSide) {
                int i = user.fishing.retrieve(itemStack);
                itemStack.hurtAndBreak(i, user, LivingEntity.getSlotForHand(hand));
            }

            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE,
                    SoundSource.NEUTRAL,
                    1.0F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            user.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (world instanceof ServerLevel serverWorld) {
                int j = (int) (EnchantmentHelper.getFishingTimeReduction(serverWorld, itemStack, user) * 20.0F);
                int k = EnchantmentHelper.getFishingLuckBonus(serverWorld, itemStack, user);
                world.addFreshEntity(new FishingHook(user, world, k, j));
            }

            user.awardStat(Stats.ITEM_USED.get(this));
            user.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantability();
    }


    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient);
    }


    @Override
    public int anvilRepairTimes() {
        return 1;
    }
}
