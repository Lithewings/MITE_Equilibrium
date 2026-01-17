package com.equilibrium.item;

import com.equilibrium.item.reference.ItemMaxStackSize;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;
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
                if(item.getMaxCount()>=8)
                    builder.add(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
            });
        };


        context.modify(Items.TORCH, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 32);
        });

        context.modify(Items.TORCH, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 32);
        });
        context.modify(Items.WHEAT_SEEDS, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 64);
        });
        context.modify(Items.NETHER_WART, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 32);
        });
        context.modify(Items.MELON_SEEDS, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 64);
        });
        context.modify(Items.PUMPKIN_SEEDS, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 64);
        });
        context.modify(Items.FISHING_ROD, builder -> {
            builder.add(DataComponentTypes.MAX_DAMAGE, 16);
        });
        context.modify(Items.WOODEN_SHOVEL, builder -> {
            builder.add(DataComponentTypes.MAX_DAMAGE, 240);
        });
        context.modify(Items.DANDELION, builder -> {
            builder.add(DataComponentTypes.MAX_STACK_SIZE, 32);
        });
    }
}