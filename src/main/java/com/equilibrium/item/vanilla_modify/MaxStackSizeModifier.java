package com.equilibrium.item.vanilla_modify;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

import static com.equilibrium.item.vanilla_modify.FoodComponentModifier.ItemMaxStackSize.ITEM_MAX_STACK_SIZE;
import static com.equilibrium.item.vanilla_modify.FoodComponentModifier.ItemMaxStackSize.itemMaxStackSizeInit;

public class MaxStackSizeModifier implements DefaultItemComponentEvents.ModifyCallback {
    @Override
    public void modify(DefaultItemComponentEvents.ModifyContext context) {
        itemMaxStackSizeInit();
        for (Map.Entry<Item, Integer> entry : ITEM_MAX_STACK_SIZE.entrySet()) {
            Item item = entry.getKey();
            int maxStackSize = entry.getValue();
            context.modify(item, builder -> {
                if(item.getDefaultMaxStackSize()>=8)
                    builder.set(DataComponents.MAX_STACK_SIZE, maxStackSize);
            });
        };
        context.modify(Items.FISHING_ROD, builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 16);
        });
        context.modify(Items.WOODEN_SHOVEL, builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 240);
        });
    }
}