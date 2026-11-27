package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.item.fishrod.FishingRodConfig;
import com.equilibrium.item.fishrod.FishingRodItem;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 让鱼线连在本模组钓竿正确的那只手上。
 */
@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {

    @Redirect(
            method = "getHandPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean equilibrium$isOfRodList(ItemStack stack, Item ignored) {
        return FishingRodConfig.isAnyFishingRod(stack);
    }

}
