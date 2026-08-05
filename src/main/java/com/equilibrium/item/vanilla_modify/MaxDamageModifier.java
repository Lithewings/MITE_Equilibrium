package com.equilibrium.item.vanilla_modify;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;

public class MaxDamageModifier implements DefaultItemComponentEvents.ModifyCallback {
    @Override
    public void modify(DefaultItemComponentEvents.ModifyContext context) {
        context.modify(Items.FISHING_ROD, builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 16);
        });
        context.modify(Items.WOODEN_SHOVEL, builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 240);
        });
    }
}