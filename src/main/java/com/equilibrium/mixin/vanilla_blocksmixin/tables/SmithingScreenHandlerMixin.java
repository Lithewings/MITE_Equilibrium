package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import com.equilibrium.item.Armors;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, World world) {
        super(type, syncId, playerInventory, context);
    }
    @Shadow
    @Final
    private List<RecipeEntry<SmithingRecipe>> recipes;

    @Inject(method = "getForgingSlotsManager",at = @At(value = "HEAD"), cancellable = true)
    protected void getForgingSlotsManager(CallbackInfoReturnable<ForgingSlotsManager> cir) {
        cir.cancel();
        cir.setReturnValue(ForgingSlotsManager.create()
                .input(0, 8, 48, stack ->this.recipes.stream().anyMatch(recipe -> ((SmithingRecipe)recipe.value()).testTemplate(stack)))
                .input(1, 26, 48, stack ->this.recipes.stream().anyMatch(recipe -> true))
                .input(2, 44, 48, stack -> this.recipes.stream().anyMatch(recipe -> ((SmithingRecipe)recipe.value()).testAddition(stack)))
                .output(3, 98, 48)
                .build());
    }

    @Inject(method = "canTakeOutput",at = @At("HEAD"), cancellable = true)
    protected void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if(this.getSlot(1).getStack().isOf(Armors.MITHRIL_CHEST_PLATE)||
            this.getSlot(1).getStack().isOf(Armors.MITHRIL_HELMET)||
               this.getSlot(1).getStack().isOf(Armors.MITHRIL_LEGGINGS)||
                    this.getSlot(1).getStack().isOf(Armors.MITHRIL_BOOTS)
                    ){
            cir.setReturnValue(true);
            this.getSlot(3).getStack().addEnchantment(player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.MENDING).get(),1);
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
            Armors.MITHRIL_CHEST_PLATE,Items.NETHERITE_CHESTPLATE,
            Armors.MITHRIL_HELMET,Items.NETHERITE_HELMET,
            Armors.MITHRIL_LEGGINGS,Items.NETHERITE_LEGGINGS,
            Armors.MITHRIL_BOOTS,Items.NETHERITE_BOOTS
    );


    @Inject(method = "updateResult",at = @At("TAIL"))
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
        ItemEnchantmentsComponent originalEnchantments = this.getSlot(1).getStack().getEnchantments();
        if(this.getSlot(0).getStack().isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) && this.getSlot(2).getStack().isOf(Items.NETHERITE_INGOT)){
            Item itemKey = this.getSlot(1).getStack().getItem();
            if(ITEM_MAP.containsKey(itemKey)){
                ItemStack itemStack = new ItemStack(ITEM_MAP.get(itemKey));
                itemStack.set(DataComponentTypes.ENCHANTMENTS,originalEnchantments);
                this.output.setStack(0, itemStack);
            }
        }


    }
}
