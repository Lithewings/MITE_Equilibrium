package com.equilibrium.mixin.vanilla_itemsmixin;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.component.DataComponentTypes.ENCHANTMENTS;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin extends Block {
    public AbstractCauldronBlockMixin(Settings settings) {
        super(settings);
    }
    @Inject(method = "onUseWithItem",at = @At("HEAD"),cancellable = true)
    protected void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
        //也就只有附魔的桶会被拦截吧,不会有附魔的物品可以与之互动了对吧
        if(player.getMainHandStack().isOf(Items.BUCKET)) {
            if(!player.getMainHandStack().getEnchantments().isEmpty()){
                player.sendMessage(Text.of("无法与附魔物品交互"), true);
                cir.setReturnValue(ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
            }
        }
    }

}
