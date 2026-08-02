package com.equilibrium.mixin.crafttime;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin {
    @Final
    @Shadow public final NonNullList<Slot> slots = NonNullList.create();

    @Inject(method = "doClick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/inventory/ClickType;SWAP:Lnet/minecraft/world/inventory/ClickType;"
            ),
            cancellable = true)
    private void internalOnSlotClick(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        //试图修复右键容器之外的栏造成异常发包的问题
        if(!player.level().isClientSide && slotIndex!=-999){
            Slot slot3 = this.slots.get(slotIndex);
            if(slot3 instanceof ResultSlot && ((AbstractContainerMenu)(Object)this instanceof CraftingMenu || (AbstractContainerMenu)(Object)this instanceof InventoryMenu)){
                if (actionType == ClickType.THROW && slotIndex >= 0) {

                    int j = button == 0 ? 1 : slot3.getItem().getCount();
                    ItemStack itemStack = slot3.safeTake(j, Integer.MAX_VALUE, player);


                    //合成时将物品放回玩家背包
                    if (player.getInventory().add(itemStack)){
                        player.getInventory().add(itemStack);
                    }else {
                        player.spawnAtLocation(itemStack);
                    }
                    ci.cancel();
                }
            }
        }
    }
}
