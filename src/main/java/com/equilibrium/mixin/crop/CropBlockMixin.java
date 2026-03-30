package com.equilibrium.mixin.crop;

import com.equilibrium.OnServerInitialize;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.equilibrium.OnServerInitialize.*;
import static com.equilibrium.server_and_client.server.CropIllnessEvent.*;
import static com.equilibrium.server_and_client.server.CropIllnessEvent.updateCropBlockPos;


@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable {


    @Shadow
    public abstract int getAge(BlockState state);

    @Shadow
    public abstract BlockState withAge(int age);

    @Shadow
    public abstract void applyGrowth(World world, BlockPos pos, BlockState state);

    protected CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }

    @Inject(method = "applyGrowth", at = @At("HEAD"), cancellable = true)
    public void applyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        ci.cancel();
        updateState(world,pos);
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = 7;//7=MaxGge
        if (i > j) {
            i = j;
        }
        world.setBlockState(pos, this.withAge(i).with(CROP_IS_ILLNESS, CROP_BLOCK_POS.getOrDefault(pos, false)), Block.NOTIFY_LISTENERS);
    }


    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        updateState(world,pos);


        //右键查看状态
        if (state.contains(CROP_IS_ILLNESS) && player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.isSneaking()) {
            serverPlayerEntity.sendMessage(state.getBlock().getName());
            serverPlayerEntity.sendMessage(Text.of("Illness: " + state.get(CROP_IS_ILLNESS)));
            serverPlayerEntity.sendMessage(Text.of("Phase: " + state.get(AGE)+"/"+"7"));




        }

        //测试
        if (player.getMainHandStack().isOf(Items.STICK)) {
            CROP_BLOCK_POS.put(pos, true);
            world.setBlockState(pos, state.with(CROP_IS_ILLNESS, true));
        }

        return super.onUse(state, world, pos, player, hit);
    }





    @Shadow
    public static final IntProperty AGE = Properties.AGE_7;

    @Shadow
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Inject(method = "appendProperties", at = @At(value = "TAIL"))
    //添加生病状态
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(CROP_IS_ILLNESS);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        if(state.get(CROP_IS_ILLNESS)){
            return List.of();
        }
        return super.getDroppedStacks(state,builder);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        updateState(world,pos);


        if (state.isAir()) {
            //在破坏方块的瞬间必须清空状态,因为空气没有illness状态
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            CROP_BLOCK_POS.remove(pos);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        updateState(world,pos);


        //将其坐标和生病情况记录在公共集合中,必须时刻同步生病状态
        if (world instanceof ServerWorld) {
            CROP_BLOCK_POS.put(pos, false);
            world.setBlockState(pos, state.with(CROP_IS_ILLNESS, false));
        }
    }

    @Unique
    private static void updateState(World world,BlockPos triggerPos) {

        if (world instanceof ServerWorld serverWorld) {
            updateCropBlockPos(serverWorld,triggerPos);
        }
    }


    @Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
        updateState(world,pos);
        if (!world.isSkyVisible(pos)) {
            if (world.getRandom().nextInt(64) == 0)
                world.breakBlock(pos, true);
        }


        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int i = this.getAge(state);
            //MaxAge=7
            if (i < 7){
                float f = CropBlock.getAvailableMoisture(this, world, pos);
                float times = 128/4f;
                //检查农田是否具有施肥标签
                if (world.getBlockState(pos.down()).contains(FERTILIZED)) {
                    if (world.getBlockState(pos.down()).get(FERTILIZED) == true)
                        //原先的两倍加速
                        times = 64f/4f;
                    else
                        times = 128/4f;
                } else
                    OnServerInitialize.LOGGER.error("No such Block State called fertilized");


                if (random.nextInt((int) (times * 25.0F / f) + 1) == 0) {
                    world.setBlockState(pos, this.withAge(i + 1).with(CROP_IS_ILLNESS, CROP_BLOCK_POS.getOrDefault(pos, false)), Block.NOTIFY_LISTENERS);
                }
            }
        }


    }
}
