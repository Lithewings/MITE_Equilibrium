package com.equilibrium.mixin.crafttime;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Final
    @Shadow public final DefaultedList<Slot> slots = DefaultedList.of();

    @Inject(method = "internalOnSlotClick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/screen/slot/SlotActionType;SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
            ),
            cancellable = true)
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        //试图修复右键容器之外的栏造成异常发包的问题
        if(!player.getWorld().isClient && slotIndex!=-999){
            Slot slot3 = this.slots.get(slotIndex);
            if(slot3 instanceof CraftingResultSlot && ((ScreenHandler)(Object)this instanceof CraftingScreenHandler || (ScreenHandler)(Object)this instanceof PlayerScreenHandler)){
                if (actionType == SlotActionType.THROW && slotIndex >= 0) {

                    int j = button == 0 ? 1 : slot3.getStack().getCount();
                    ItemStack itemStack = slot3.takeStackRange(j, Integer.MAX_VALUE, player);


                    //合成时将物品放回玩家背包
                    if (player.getInventory().insertStack(itemStack)){
                        player.getInventory().insertStack(itemStack);
                    }else {
                        player.dropStack(itemStack);
                    }
                    ci.cancel();
                }
            }
        }
    }
}
