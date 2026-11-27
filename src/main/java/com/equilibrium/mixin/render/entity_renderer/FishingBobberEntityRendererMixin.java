package com.equilibrium.mixin.render.entity_renderer;

import com.equilibrium.item.fishrod.FishingRodConfig;
import com.equilibrium.item.fishrod.FishingRodItem;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 让鱼线连在本模组钓竿正确的那只手上。
 */
@Mixin(FishingHookRenderer.class)
public class FishingBobberEntityRendererMixin {

    @Redirect(
            method = "getPlayerHandPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/neoforged/neoforge/common/ItemAbility;)Z")
    )
    private boolean equilibrium$isOfRodList(ItemStack instance, ItemAbility itemAbility) {
        return FishingRodConfig.isAnyFishingRod(instance);
    }

}
