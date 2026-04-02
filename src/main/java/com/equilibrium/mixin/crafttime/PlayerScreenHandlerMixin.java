package com.equilibrium.mixin.crafttime;

import com.equilibrium.tags.ModItemTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

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




            //无条件输出物品
            CraftingScreenHandler.updateResult(this, this.owner.getWorld(), this.owner, this.craftingInput, this.craftingResult, null);

            if(this.craftingResult.getStack(0).isIn(ModItemTags.CRAFT_TABLE)){
                maxCraftLevel--;
            }
           //然后再施加限制
            //等级是否合法?
            //onServer条件下,一定不发生get不到server的情况
            boolean isLevelValid=!getGameBooleanRuleFromServer(ENABLE_CRAFTING_TIME_AND_LEVEL,this.owner.getServer()) || maxCraftLevel == 0;

            if (!isLevelValid) {
                List<Text> list1 = List.of(INVALID_CRAFTING_TEXT);
                LoreComponent loreComponent = new LoreComponent(list1);
                ItemStack itemStack = this.craftingResult.getStack(0);
                itemStack.set(DataComponentTypes.LORE,loreComponent);
            }
            //定义玩家是否可以取出的逻辑,见MixinInventoryScreen.onMouseClick

        }
    }

}







