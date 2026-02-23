package com.equilibrium.mixin.structure_and_dimension;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CompassItem.class)
public class CompassItemMixin extends Item {

    public CompassItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(!user.getWorld().isClient()) {
            user.sendMessage(Text.of(
                            "[x]: " + (int) user.getX() +
                                    "[y]:" + (int) user.getY() +
                                    "[z]: " + (int) user.getZ()
                    )
            );
            return TypedActionResult.success(itemStack);
        }else
            return TypedActionResult.fail(itemStack);

    }
}
