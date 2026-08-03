package com.equilibrium.mixin.crop;

import com.equilibrium.OnServerInitialize;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.equilibrium.OnServerInitialize.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.DISABLE_CROP_GROW;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.*;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.updateCropBlockPos;


@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends BushBlock implements BonemealableBlock {


    @Shadow
    public abstract int getAge(BlockState state);

    @Shadow
    public abstract BlockState getStateForAge(int age);



    protected CropBlockMixin(Properties settings) {
        super(settings);
    }

    @Shadow
    protected int getBonemealAgeIncrease(Level world) {
        return Mth.nextInt(world.random, 2, 5);
    }

    @Inject(method = "growCrops", at = @At("HEAD"), cancellable = true)
    public void applyGrowth(Level world, BlockPos pos, BlockState state, CallbackInfo ci) {
        ci.cancel();
        updateState(world,pos);
        int i = this.getAge(state) + this.getBonemealAgeIncrease(world);
        int j = 7;//7=MaxGge
        if (i > j) {
            i = j;
        }
        world.setBlock(pos, this.getStateForAge(i).setValue(CROP_IS_ILLNESS, CROP_BLOCK_POS.getOrDefault(pos, false)), Block.UPDATE_CLIENTS);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        updateState(world,pos);


        //右键查看状态
        if (state.hasProperty(CROP_IS_ILLNESS) && player instanceof ServerPlayer serverPlayerEntity && serverPlayerEntity.isShiftKeyDown()) {
            serverPlayerEntity.sendSystemMessage(state.getBlock().getName());
            serverPlayerEntity.sendSystemMessage(Component.nullToEmpty("Illness: " + state.getValue(CROP_IS_ILLNESS)));
            serverPlayerEntity.sendSystemMessage(Component.nullToEmpty("Phase: " + state.getValue(AGE)+"/"+"7"));




        }

        //测试
        if (player.getMainHandItem().is(Items.STICK)) {
            CROP_BLOCK_POS.put(pos, true);
            world.setBlockAndUpdate(pos, state.setValue(CROP_IS_ILLNESS, true));
        }

        return super.useWithoutItem(state, world, pos, player, hit);
    }





    @Shadow
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;

    @Shadow
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Inject(method = "createBlockStateDefinition", at = @At(value = "TAIL"))
    //添加生病状态
    protected void appendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(CROP_IS_ILLNESS);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if(state.getValue(CROP_IS_ILLNESS)){
            return List.of();
        }
        return super.getDrops(state,builder);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
        updateState(world,pos);


        if (state.isAir()) {
            //在破坏方块的瞬间必须清空状态,因为空气没有illness状态
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            CROP_BLOCK_POS.remove(pos);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        updateState(world,pos);


        //将其坐标和生病情况记录在公共集合中,必须时刻同步生病状态
        if (world instanceof ServerLevel) {
            CROP_BLOCK_POS.put(pos, false);
            world.setBlockAndUpdate(pos, state.setValue(CROP_IS_ILLNESS, false));
        }
    }

    @Unique
    private static void updateState(Level world,BlockPos triggerPos) {

        if (world instanceof ServerLevel serverWorld) {
            updateCropBlockPos(serverWorld,triggerPos);
        }
    }


    @Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        ci.cancel();
        updateState(world,pos);
        if (!world.canSeeSky(pos)) {
            if (world.getRandom().nextInt(64) == 0)
                world.destroyBlock(pos, true);
        }


        if (world.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            //MaxAge=7
            if (i < 7){
                float f = CropBlock.getGrowthSpeed(state, world, pos);
                float times = 128/4f;
                //检查农田是否具有施肥标签
                if (world.getBlockState(pos.below()).hasProperty(FERTILIZED)) {
                    if (world.getBlockState(pos.below()).getValue(FERTILIZED) == true)
                        //原先的两倍加速
                        times = 64f/4f;
                    else
                        times = 128/4f;
                } else
                    OnServerInitialize.LOGGER.error("No such Block State called fertilized");

                //游戏规则:是否允许农作物自然生长
                if (!getGameBooleanRuleFromServer(DISABLE_CROP_GROW,world.getServer()) && random.nextInt((int) (times * 25.0F / f) + 1) == 0) {
                    world.setBlock(pos, this.getStateForAge(i + 1).setValue(CROP_IS_ILLNESS, CROP_BLOCK_POS.getOrDefault(pos, false)), Block.UPDATE_CLIENTS);
                }
            }
        }


    }
}
