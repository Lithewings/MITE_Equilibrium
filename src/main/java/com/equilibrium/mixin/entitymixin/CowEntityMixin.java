package com.equilibrium.mixin.entitymixin;

import com.equilibrium.MITEequilibrium;
import com.equilibrium.entity.goal.AdvanceEscapeDangerGoal;
import com.equilibrium.entity.goal.BreakGrassGoal;
import com.equilibrium.item.food.FoodItems;
import com.equilibrium.network.S2CCowIllnessTextureBooleanPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;


import static com.equilibrium.util.AStarForAnimals.findSimplePath;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity {
    @org.jetbrains.annotations.Nullable @Shadow public abstract PassiveEntity createChild(ServerWorld par1, PassiveEntity par2);

    @Shadow protected abstract void playStepSound(BlockPos pos, BlockState state);

    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }



    @Unique
    private int milkCoolDown =0;

    @Unique
    private int checkEnvironmentIsSuitableTime = 6000;
    @Unique
    private int grassBlockLackTimes;
    @Unique
    private int grassWaterLackTimes;
    @Unique
    private int grassLackTimes;
    @Unique
    private int waterLackTimes;
    @Unique
    private boolean lastIllnessState = false;

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("checkEnvironmentIsSuitableTime", this.checkEnvironmentIsSuitableTime);
        nbt.putInt("grassBlockLackTimes", this.grassBlockLackTimes);
        nbt.putInt("grassWaterLackTimes", this.grassWaterLackTimes);
        nbt.putInt("grassLackTimes", this.grassLackTimes);
        nbt.putInt("waterLackTimes", this.waterLackTimes);
        nbt.putInt("milkCoolDown", this.milkCoolDown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.checkEnvironmentIsSuitableTime = nbt.getInt("checkEnvironmentIsSuitableTime");
        this.grassBlockLackTimes = nbt.getInt("grassBlockLackTimes");
        this.grassWaterLackTimes = nbt.getInt("grassWaterLackTimes");
        this.grassLackTimes = nbt.getInt("grassLackTimes");
        this.waterLackTimes = nbt.getInt("waterLackTimes");
        this.milkCoolDown= nbt.getInt("milkCoolDown");
    }



    @Unique
    private boolean isIllness(){
        return this.grassBlockLackTimes>3||this.grassWaterLackTimes>3||this.grassLackTimes>3||this.waterLackTimes>3;
    }
    @Unique
    private void checkBodyStats(PlayerEntity player){

        player.sendMessage(Text.of("Cow: "));
        player.sendMessage(Text.of("Lack of Water: "+this.waterLackTimes+" times"));
        player.sendMessage(Text.of("Lack of Grass: "+this.grassLackTimes+" times"));
        player.sendMessage(Text.of("Lack of GrassBlock: "+this.grassBlockLackTimes+" times"));
        player.sendMessage(Text.of("Illness: "+isIllness()));

    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.isRemoved() && !this.dead) {
            Entity entity = damageSource.getAttacker();
            LivingEntity livingEntity = this.getPrimeAdversary();
            if (this.scoreAmount >= 0 && livingEntity != null) {
                livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
            }

            if (this.isSleeping()) {
                this.wakeUp();
            }

            if (!this.getWorld().isClient && this.hasCustomName()) {
                MITEequilibrium.LOGGER.info("Named entity {} died: {}", this, this.getDamageTracker().getDeathMessage().getString());
            }

            this.dead = true;
            this.getDamageTracker().update();
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (entity == null || entity.onKilledOther(serverWorld, this)) {
                    this.emitGameEvent(GameEvent.ENTITY_DIE);
                    if(!this.isIllness())
                        this.drop(serverWorld, damageSource);
                    this.onKilledBy(livingEntity);
                }

                this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            }

            this.setPose(EntityPose.DYING);
        }
    }
    @Inject(method = "interactMob",at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        cir.cancel();
        if(player.getWorld().isClient()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if(player.isSneaking()){
            checkBodyStats(player);
            cir.setReturnValue(ActionResult.SUCCESS);
        }

        if(isIllness()){
            player.sendMessage(Text.of("The cow can not be milked or fed due to the illness."));
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        ItemStack itemStack = player.getStackInHand(hand);

        if(itemStack.isEmpty()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
        if (this.milkCoolDown<=0 && itemStack.isOf(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
            player.setStackInHand(hand, itemStack2);
            this.milkCoolDown = 24000;
            cir.setReturnValue(ActionResult.SUCCESS);

        }
        else if(this.milkCoolDown>=0 && this.milkCoolDown<18000 && itemStack.isOf(Items.BOWL) &&!this.isBaby()){
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, FoodItems.MILK_BOWL.getDefaultStack());
            player.setStackInHand(hand, itemStack2);
            milkCoolDown += 6000;
            cir.setReturnValue(ActionResult.SUCCESS);

        } else if (this.milkCoolDown>=0  && itemStack.isOf(Items.BUCKET)||itemStack.isOf(Items.BOWL)) {
            player.sendMessage(Text.of("The cow did not prepare for milking."));
            cir.setReturnValue(super.interactMob(player,hand));
        } else{
            cir.setReturnValue(super.interactMob(player,hand));
        }
    }


    public void test(CowEntity cowEntity){
        if(!this.getWorld().isClient()){
            this.getWorld().getPlayers().forEach(player -> {
                player.sendMessage(Text.of("With Illness? "+isIllness()));
            });

        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
//        test((CowEntity)(Object)this);
        boolean currentIllness = isIllness();
        if (currentIllness&&this.isBaby()){
            this.setBreedingAge(this.getBreedingAge()-1);
        }
        if (currentIllness != this.lastIllnessState) {
            // 状态改变了，发送网络包
            for (ServerPlayerEntity player : this.getServer().getPlayerManager().getPlayerList()) {
                // 检查玩家是否在同一个维度且能看到实体
                if (player.getWorld().getRegistryKey() == this.getWorld().getRegistryKey() &&
                        player.canSee(this)) {
                    ServerPlayNetworking.send(
                            player,
                            new S2CCowIllnessTextureBooleanPacket.CowAppearancePayload(this.getId(), isIllness())
                    );
                }
            }
            lastIllnessState = currentIllness;
        }

        if(!this.getWorld().isClient()) {
            this.checkEnvironmentIsSuitableTime--;
            if (checkEnvironmentIsSuitableTime <= 0) {
                checkEnvironment();
                checkEnvironmentIsSuitableTime = 6000;
            }
            this.milkCoolDown--;
            if(milkCoolDown<=0)
                milkCoolDown=0;


        }
    }

    @Unique
    private void checkEnvironment(){
        //检查环境
        if(!checkFootBlockIsGrassBlock()) {
            this.grassBlockLackTimes++;
        }else
            this.grassBlockLackTimes=0;

        if(!checkWater()){
            this.waterLackTimes++;
        }else
            this.waterLackTimes=0;

        if(!checkGrass()){
            this.grassLackTimes++;
        }else
            this.grassLackTimes=0;


//        switch (this.getRandom().nextInt(2)) {
//            case 0: {
//                if (!checkWater()) {
//                    if (!this.getWorld().isClient()) {
//                        for (PlayerEntity player : this.getWorld().getPlayers()) {
//                            player.sendMessage(Text.of("该生物无法找到水源"));
//                        }
//                    }
//                } else {
//                    if (!this.getWorld().isClient()) {
//                        for (PlayerEntity player : this.getWorld().getPlayers()) {
//                            player.sendMessage(Text.of("该生物可以找到水源"));
//                        }
//                    }
//                }
//                break;
//            }
//            case 1: {
//                if (!checkGrass()) {
//                    if (!this.getWorld().isClient()) {
//                        for (PlayerEntity player : this.getWorld().getPlayers()) {
//                            player.sendMessage(Text.of("该生物无法找到草"));
//                        }
//                    }
//                } else {
//                    if (!this.getWorld().isClient()) {
//                        for (PlayerEntity player : this.getWorld().getPlayers()) {
//                            player.sendMessage(Text.of("该生物可以找到草"));
//                        }
//                    }
//                }
//
//            }
//            break;
//        }
    }



    @Unique
    private boolean checkFootBlockIsGrassBlock(){
        CowEntity cowEntity = (CowEntity)(Object)(this);
        BlockState blockState = cowEntity.getWorld().getBlockState(cowEntity.getBlockPos().down());
        return blockState.isOf(Blocks.GRASS_BLOCK);
    }

    @Unique
    private boolean checkWater(){
        CowEntity cowEntity = (CowEntity)(Object)(this);
        return canNavigateToSurfaceWater(cowEntity);
    }
    @Unique
    private boolean checkGrass(){
        CowEntity cowEntity = (CowEntity)(Object)(this);
        return canNavigateToSurfaceGrass(cowEntity);
    }

    @Unique
    private static boolean canNavigateToSurfaceWater(PathAwareEntity entity) {

        World world =entity.getWorld();

        // 以生物为中心，搜索16格范围内的方块
        int searchRadius = 16;
        int x = entity.getBlockPos().getX();
        int y = entity.getBlockPos().getY();
        int z = entity.getBlockPos().getZ();


        ArrayList<BlockPos> posArrayList= new ArrayList<>();

        // 从左上角到右下角顺序搜索
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                for (int dy = -4; dy <= 4; dy++) {
                    // 计算当前搜索位置的世界坐标
                    int worldX = x + dx;
                    int worldY = y + dy;
                    int worldZ = z + dz;

                    // 获取方块
                    BlockPos pos = new BlockPos(worldX, worldY, worldZ);
                    BlockState blockState = world.getBlockState(pos);

                    boolean isWater = blockState.getBlock() == Blocks.WATER ||
                            blockState.getFluidState().getFluid() == Fluids.WATER ||
                            blockState.getFluidState().getFluid() == Fluids.FLOWING_WATER;

                    if (isWater) {
                        posArrayList.add(pos);
                    }
                }

            }
        }

        if (!posArrayList.isEmpty()) {
            for(BlockPos pos : posArrayList){
//                MITEequilibrium.LOGGER.info(String.valueOf(pos));
                //找到通往水面之上的路径
                List<BlockPos> list = findSimplePath(entity.getWorld(), entity.getBlockPos(), pos.up());
                if (list!=null) {
//                    for (BlockPos blockPos:list){
//                        world.setBlockState(blockPos,Blocks.WHITE_WOOL.getDefaultState());
//
//                        new Thread(() -> {
//                            try {
//                                Thread.sleep(3000); // 10秒 = 10000毫秒
//                                // 延迟结束后，在服务器主线程执行方块操作
//                                world.getServer().execute(() -> {
//                                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
//                                });
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }).start();
//                    }
                    //导航到水附近
                    entity.getNavigation().startMovingTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
                    return true;
                }

            }
        }
        return false;
    }
    @Unique
    private static boolean canNavigateToSurfaceGrass(PathAwareEntity entity) {

        World world =entity.getWorld();

        // 以生物为中心，搜索16格范围内的方块
        int searchRadius = 16;
        int x = entity.getBlockPos().getX();
        int y = entity.getBlockPos().getY();
        int z = entity.getBlockPos().getZ();


        ArrayList<BlockPos> posArrayList= new ArrayList<>();

        // 从左上角到右下角顺序搜索
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                for (int dy = -4; dy <= 4; dy++) {
                    // 计算当前搜索位置的世界坐标
                    int worldX = x + dx;
                    int worldY = y + dy;
                    int worldZ = z + dz;

                    // 获取方块
                    BlockPos pos = new BlockPos(worldX, worldY, worldZ);
                    BlockState blockState = world.getBlockState(pos);

                    if (blockState.isOf(Blocks.SHORT_GRASS)||blockState.isOf(Blocks.TALL_GRASS)) {
                        posArrayList.add(pos);
                    }
                }

            }
        }

        if (!posArrayList.isEmpty()) {
            for(BlockPos pos : posArrayList){
//                MITEequilibrium.LOGGER.info(String.valueOf(pos));
                //找到通往草的路径
                List<BlockPos> list = findSimplePath(entity.getWorld(), entity.getBlockPos(), pos.up());
                if (list!=null) {
                    //导航到草附近
                    entity.getNavigation().startMovingTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
                    return true;
                }

            }
        }
        return false;
    }


    @Inject(method = "initGoals",at = @At(value = "HEAD"),cancellable = true)
    protected void initGoals(CallbackInfo ci) {
        ci.cancel();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AdvanceEscapeDangerGoal(this, 2.25));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, stack -> stack.isIn(ItemTags.COW_FOOD), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(10, new BreakGrassGoal((CowEntity)(Object)this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }
    @Unique
    @Override
    public DamageSource getRecentDamageSource() {
        if (this.getWorld().getTime() -this.lastDamageTime > 1600L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }










}

