package com.equilibrium.mixin.vanilla_blocksmixin.tables;


import com.equilibrium.item.armor.ArmorItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(SmithingMenu.class)
public abstract class SmithingScreenHandlerMixin extends ItemCombinerMenu {

    public SmithingScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, Level world) {
        super(type, syncId, playerInventory, context);
    }
    @Shadow
    @Final
    private List<RecipeHolder<SmithingRecipe>> recipes;

    @Inject(method = "createInputSlotDefinitions",at = @At(value = "HEAD"), cancellable = true)
    protected void getForgingSlotsManager(CallbackInfoReturnable<ItemCombinerMenuSlotDefinition> cir) {
        cir.cancel();
        cir.setReturnValue(ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 8, 48, stack ->this.recipes.stream().anyMatch(recipe -> ((SmithingRecipe)recipe.value()).isTemplateIngredient(stack)))
                .withSlot(1, 26, 48, stack ->this.recipes.stream().anyMatch(recipe -> true))
                .withSlot(2, 44, 48, stack -> this.recipes.stream().anyMatch(recipe -> ((SmithingRecipe)recipe.value()).isAdditionIngredient(stack)))
                .withResultSlot(3, 98, 48)
                .build());
    }

    @Inject(method = "mayPickup",at = @At("HEAD"), cancellable = true)
    protected void canTakeOutput(Player player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if(this.getSlot(1).getItem().is(ArmorItems.MITHRIL_CHEST_PLATE.get())||
            this.getSlot(1).getItem().is(ArmorItems.MITHRIL_HELMET.get())||
               this.getSlot(1).getItem().is(ArmorItems.MITHRIL_LEGGINGS.get())||
                    this.getSlot(1).getItem().is(ArmorItems.MITHRIL_BOOTS.get())
                    ){
            cir.setReturnValue(true);
            this.getSlot(3).getItem().enchant(player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.MENDING).get(),1);
        }
    }

//    @Override
//    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
//        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
//    }
//
//



    @Unique
    private static final Map<Item, Item> ITEM_MAP =Map.of(
            ArmorItems.MITHRIL_CHEST_PLATE.get().asItem(),Items.NETHERITE_CHESTPLATE,
            ArmorItems.MITHRIL_HELMET.get().asItem(),Items.NETHERITE_HELMET,
            ArmorItems.MITHRIL_LEGGINGS.get().asItem(),Items.NETHERITE_LEGGINGS,
            ArmorItems.MITHRIL_BOOTS.get().asItem(),Items.NETHERITE_BOOTS
    );


    @Inject(method = "createResult",at = @At("TAIL"))
    public void updateResult(CallbackInfo ci) {


//        SmithingRecipeInput smithingRecipeInput = this.createRecipeInput();
//        List<RecipeEntry<SmithingRecipe>> list = this.world.getRecipeManager().getAllMatches(RecipeType.SMITHING, smithingRecipeInput, this.world);
//        if (list.isEmpty()) {
//            this.output.setStack(0, ItemStack.EMPTY);
//        } else {
//            RecipeEntry<SmithingRecipe> recipeEntry = (RecipeEntry<SmithingRecipe>)list.get(0);
//            ItemStack itemStack = recipeEntry.value().craft(smithingRecipeInput, this.world.getRegistryManager());
//            if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
//                this.currentRecipe = recipeEntry;
//                this.output.setLastRecipe(recipeEntry);
//                this.output.setStack(0, itemStack);
//            }
//        }
//        this.output.setStack(0, new ItemStack(Items.DIAMOND));
        ItemEnchantments originalEnchantments = this.getSlot(1).getItem().getEnchantments();
        if(this.getSlot(0).getItem().is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) && this.getSlot(2).getItem().is(Items.NETHERITE_INGOT)){
            Item itemKey = this.getSlot(1).getItem().getItem();
            if(ITEM_MAP.containsKey(itemKey)){
                ItemStack itemStack = new ItemStack(ITEM_MAP.get(itemKey));
                itemStack.set(DataComponents.ENCHANTMENTS,originalEnchantments);
                this.resultSlots.setItem(0, itemStack);
            }
        }


    }
}
