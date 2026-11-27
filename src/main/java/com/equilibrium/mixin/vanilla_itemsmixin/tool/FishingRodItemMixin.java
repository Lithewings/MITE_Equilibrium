package com.equilibrium.mixin.vanilla_itemsmixin.tool;

import com.equilibrium.item.fishrod.AnvilRepairRuleView;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.item.FishingRodItem.class)
public abstract class FishingRodItemMixin extends Item implements AnvilRepairRuleView {
    public FishingRodItemMixin(Properties settings) {
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
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(Items.IRON_NUGGET);
    }
}
