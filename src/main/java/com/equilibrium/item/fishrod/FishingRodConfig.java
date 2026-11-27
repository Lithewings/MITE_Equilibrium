package com.equilibrium.item.fishrod;

import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.equilibrium.item.fishrod.FishingRods.*;
import static com.equilibrium.item.fishrod.FishingRods.ADAMANTIUM_FISHING_ROD;

public class FishingRodConfig {


    public static final List<Item> FISHING_ROD_ITEMS = List.of(
            Items.FISHING_ROD,
            FLINT_FISHING_ROD,
            COPPER_FISHING_ROD,
            SILVER_FISHING_ROD,
            GOLD_FISHING_ROD,
            MITHRIL_FISHING_ROD,
            ADAMANTIUM_FISHING_ROD
    );



    public static boolean isAnyFishingRod(ItemStack stack) {
        return !stack.isEmpty() && FISHING_ROD_ITEMS.contains(stack.getItem());
    }

}
