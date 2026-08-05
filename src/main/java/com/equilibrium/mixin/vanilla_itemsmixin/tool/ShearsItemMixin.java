package com.equilibrium.mixin.vanilla_itemsmixin.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item {
    public ShearsItemMixin(Properties settings) {
        super(settings);
    }

    //修改默认工具属性
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Properties ShearsItem(Properties settings) {
        return new Properties().durability(6400).component(DataComponents.TOOL, ShearsItem.createToolProperties());

    }




}
