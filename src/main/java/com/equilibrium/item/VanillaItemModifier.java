package com.equilibrium.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

import static com.equilibrium.item.reference.ItemMaxStackSize.ITEM_MAX_STACK_SIZE;
import static com.equilibrium.item.reference.ItemMaxStackSize.itemMaxStackSizeInit;

public class VanillaItemModifier implements DefaultItemComponentEvents.ModifyCallback {

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


        context.modify(Items.TORCH, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 32);
        });

        context.modify(Items.TORCH, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 32);
        });
        context.modify(Items.WHEAT_SEEDS, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 64);
        });
        context.modify(Items.NETHER_WART, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 32);
        });
        context.modify(Items.MELON_SEEDS, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 64);
        });
        context.modify(Items.PUMPKIN_SEEDS, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 64);
        });
        context.modify(Items.FISHING_ROD, builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 16);
        });
        context.modify(Items.WOODEN_SHOVEL, builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 240);
        });
        context.modify(Items.DANDELION, builder -> {
            builder.set(DataComponents.MAX_STACK_SIZE, 32);
        });
    }
}