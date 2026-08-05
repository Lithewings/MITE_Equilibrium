package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryMenu.class)
public interface PlayerScreenHandlerAccessor {
    @Accessor("craftSlots")
    CraftingContainer getCraftingInput();
}
