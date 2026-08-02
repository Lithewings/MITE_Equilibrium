package com.equilibrium.mixin.crafttime;

import com.equilibrium.tags.ModItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

@Mixin(InventoryMenu.class)
public abstract class PlayerScreenHandlerMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    @Shadow
    @Final
    private CraftingContainer craftSlots;



    @Shadow
    @Final
    private ResultContainer resultSlots;


    @Shadow
    @Final
    public boolean active;


    @Final
    @Shadow private Player owner;


    @Shadow public abstract int getResultSlotIndex();


    public PlayerScreenHandlerMixin(MenuType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }




//    @Inject(method = "onClosed",at = @At(value = "HEAD"), cancellable = true)
//    public void onClosed(PlayerEntity player, CallbackInfo ci) {
//        ci.cancel();
//    }



    @Inject(method = "slotsChanged",at = @At(value = "HEAD"),cancellable = true)
    public void onContentChanged(Container inventory, CallbackInfo ci) {
        ci.cancel();

        if(this.active) {
            //确定4个输入物品的合成等级
            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                int craftLevel = 0;
                ItemStack itemStack = this.craftSlots.getItem(i);
                if (itemStack.is(ModItemTags.CRAFT_LEVEL1))
                    craftLevel = 1;
                else if (itemStack.is(ModItemTags.CRAFT_LEVEL2))
                    craftLevel = 2;
                else if (itemStack.is(ModItemTags.CRAFT_LEVEL3))
                    craftLevel = 3;
                else if (itemStack.is(ModItemTags.CRAFT_LEVEL4))
                    craftLevel = 4;
                else if (itemStack.is(ModItemTags.CRAFT_LEVEL5))
                    craftLevel = 5;
                else
                    craftLevel = 0;
                list.add(craftLevel);
            }
            int maxCraftLevel = Collections.max(list);




            //无条件输出物品
            CraftingMenu.slotChangedCraftingGrid(this, this.owner.level(), this.owner, this.craftSlots, this.resultSlots, null);

            if(this.resultSlots.getItem(0).is(ModItemTags.CRAFT_TABLE)){
                maxCraftLevel--;
            }
           //然后再施加限制
            //等级是否合法?
            //onServer条件下,一定不发生get不到server的情况
            boolean isLevelValid=!(getGameBooleanRuleFromServer(ENABLE_CRAFTING_TIME_AND_LEVEL,this.owner.getServer())) || maxCraftLevel == 0;

            if (!isLevelValid) {
                List<Component> list1 = List.of(INVALID_CRAFTING_TEXT);
                ItemLore loreComponent = new ItemLore(list1);
                ItemStack itemStack = this.resultSlots.getItem(0);
                itemStack.set(DataComponents.LORE,loreComponent);
            }
            //定义玩家是否可以取出的逻辑,见MixinInventoryScreen.onMouseClick

        }
    }

}







