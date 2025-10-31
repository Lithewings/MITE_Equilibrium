package com.equilibrium.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

public class VanillaItemModifier implements DefaultItemComponentEvents.ModifyCallback {

    @Override
    public void modify(DefaultItemComponentEvents.ModifyContext context) {
        // 修改单个物品
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

    }
}