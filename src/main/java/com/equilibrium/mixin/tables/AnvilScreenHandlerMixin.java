package com.equilibrium.mixin.tables;

import com.equilibrium.util.ServerInfoRecorder;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(AnvilScreenHandler.class)
public abstract  class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    private final Property levelCost = Property.create();
    @Shadow
    private int repairItemUsage;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    //最大200级就可以无限回复耐久了
    @Inject(method = "getNextCost",at = @At("HEAD"), cancellable = true)
    private static void getNextCost(int cost, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue ((int)Math.min((long)cost * 2L + 1L, 49));
    }


    @Inject(method = "canTakeOutput",at = @At("HEAD"),cancellable = true)
    protected void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        boolean preCondition =  (player.isInCreativeMode() || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
        //以下条件不可以满足,否则不接受本次铁砧操作
        boolean additionalCondition = (this.input.getStack(0).isOf(Items.BUCKET)&&this.input.getStack(0).getCount()>1);
        cir.setReturnValue(preCondition&& !additionalCondition );
    }

    //即便大于39级,也可以拿出来,只是显示的是红字而已,实际上可以拿出来
    @Redirect(method = "updateResult", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z",ordinal = 1,opcode = Opcodes.GETFIELD))
    public boolean updateResult(PlayerAbilities instance){
        return true;
    }




    @Inject(method = "onTakeOutput",at = @At("HEAD"),cancellable = true)
    protected void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        ci.cancel();
//        if (!player.getAbilities().creativeMode) {
//            player.addExperienceLevels(-this.levelCost.get());
//        }


        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }

        this.levelCost.set(0);
        this.context.run((world, pos) -> {



            BlockState blockState = world.getBlockState(pos);


            if (!player.isInCreativeMode() && blockState.isIn(BlockTags.ANVIL)) {
                //以下逻辑为铁砧损坏,getLandingState就是执行了一次阶段损坏逻辑




                if(player.getRandom().nextInt(40)==0)
                    blockState = AnvilBlock.getLandingState(blockState);




                if (blockState == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                } else {
                    world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            }
        });
    }











}
