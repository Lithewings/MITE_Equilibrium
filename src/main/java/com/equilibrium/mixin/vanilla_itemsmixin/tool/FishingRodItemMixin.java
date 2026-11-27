package com.equilibrium.mixin.vanilla_itemsmixin.tool;

import com.equilibrium.item.fishrod.AnvilRepairRuleView;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.item.FishingRodItem.class)
public abstract class FishingRodItemMixin extends Item implements AnvilRepairRuleView {
    public FishingRodItemMixin(Settings settings) {
        super(settings);
    }

//    @Inject(method = "<init>",at = @At("TAIL"))
//    public void FishingRodItem(Settings settings, CallbackInfo ci) {
//        settings.component(DataComponentTypes.MAX_DAMAGE,16);
//    }
    //见VanillaItemModifier

    @Override
    public int anvilRepairTimes(){
        return 1;
    }
    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.IRON_NUGGET);
    }
}
