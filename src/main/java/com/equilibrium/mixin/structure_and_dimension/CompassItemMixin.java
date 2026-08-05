package com.equilibrium.mixin.structure_and_dimension;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CompassItem.class)
public class CompassItemMixin extends Item {

    public CompassItemMixin(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(!user.level().isClientSide()) {
            user.sendSystemMessage(Component.nullToEmpty(
                            "[x]: " + (int) user.getX() +
                                    "[y]:" + (int) user.getY() +
                                    "[z]: " + (int) user.getZ()
                    )
            );
            return InteractionResultHolder.success(itemStack);
        }else
            return InteractionResultHolder.fail(itemStack);

    }
}
