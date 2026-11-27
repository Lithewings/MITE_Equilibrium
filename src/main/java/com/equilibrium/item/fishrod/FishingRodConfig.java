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
            COPPER_FISHING_ROD.get(),
            SILVER_FISHING_ROD.get(),
            GOLD_FISHING_ROD.get(),
            MITHRIL_FISHING_ROD.get(),
            ADAMANTIUM_FISHING_ROD.get()
    );



    public static boolean isAnyFishingRod(ItemStack stack) {
        return !stack.isEmpty() && FISHING_ROD_ITEMS.contains(stack.getItem());
    }

}
