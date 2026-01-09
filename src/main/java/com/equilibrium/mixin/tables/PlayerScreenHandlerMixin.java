package com.equilibrium.mixin.tables;

import com.equilibrium.item.Tools;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> {
    @Shadow @Final private CraftingResultInventory craftingResult;
    @Shadow @Final public boolean onServer;
    @Final
    @Shadow private  PlayerEntity owner;
    @Shadow public abstract int getCraftingResultSlotIndex();


    @Shadow @Final private RecipeInputInventory craftingInput;

    public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }




//    @Inject(method = "onClosed",at = @At(value = "HEAD"), cancellable = true)
//    public void onClosed(PlayerEntity player, CallbackInfo ci) {
//        ci.cancel();
//    }



    @Inject(method = "onContentChanged",at = @At(value = "HEAD"),cancellable = true)
    public void onContentChanged(Inventory inventory, CallbackInfo ci) {
        ci.cancel();
        this.craftingResult.clear();
        if(this.onServer) {
            //确定4个输入物品的合成等级
            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                int craftLevel = 0;
                ItemStack itemStack = this.craftingInput.getStack(i);
                if (itemStack.isIn(ModItemTags.CRAFT_LEVEL1))
                    craftLevel = 1;
                else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL2))
                    craftLevel = 2;
                else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL3))
                    craftLevel = 3;
                else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL4))
                    craftLevel = 4;
                else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL5))
                    craftLevel = 5;
                else
                    craftLevel = 0;
                list.add(craftLevel);
            }
            int maxCraftLevel = Collections.max(list);

            //是否在合成工作台?
            ItemStack itemStack0 = this.craftingInput.getStack(0);ItemStack itemStack1 = this.craftingInput.getStack(1);
            ItemStack itemStack2 = this.craftingInput.getStack(2);ItemStack itemStack3 = this.craftingInput.getStack(3);


            boolean condition1 = itemStack0.isOf(Tools.FLINT_KNIFE)&&itemStack2.isIn(ItemTags.LOGS);
            boolean condition2 = itemStack1.isOf(Tools.FLINT_KNIFE)&&itemStack3.isIn(ItemTags.LOGS);
            boolean isCraftTable =condition1||condition2;

            if (maxCraftLevel > 0 && !isCraftTable) {
            //什么都不做,不更新
            } else
                CraftingScreenHandler.updateResult(this, this.owner.getWorld(), this.owner, this.craftingInput, this.craftingResult, null);
        }
    }

}







