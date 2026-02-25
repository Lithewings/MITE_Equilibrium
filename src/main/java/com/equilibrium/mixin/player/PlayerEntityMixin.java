package com.equilibrium.mixin.player;

import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import com.equilibrium.persistent_state.StateSaverAndLoader;
import com.equilibrium.status.registerStatusEffect;
import com.equilibrium.tags.ModBlockTags;
import com.equilibrium.tags.ModItemTags;
import com.equilibrium.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



import static com.equilibrium.item.tools_attribute.ExtraDamageFromExperienceLevel.getDamageLevel;
import static com.equilibrium.util.ableToMine.getBlockHarvestLevel;
import static com.equilibrium.util.ableToMine.getItemHarvestLevel;
import static java.lang.Math.max;
import static net.minecraft.registry.tag.EntityTypeTags.SKELETONS;
import static net.minecraft.registry.tag.EntityTypeTags.UNDEAD;
import static net.minecraft.util.math.MathHelper.nextBetween;

@Mixin(PlayerEntity.class)
//和源码构造方式一致,继承谁这里也跟着继承
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected void vanishCursedItems() {
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (!itemStack.isEmpty() && EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                this.inventory.removeStack(i);
            }
        }
    }






    @Override
    public void dropInventory() {
        super.dropInventory();
        serverState = StateSaverAndLoader.getServerState(this.getServer());
        //首次死亡的掉落保护
        if(serverState.playerDeathTimes==1)
            this.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true,this.getServer());
        else if ((this.experienceLevel<5)) {
            this.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(false,this.getServer());
            this.vanishCursedItems();
        //掉落所有物品
            this.inventory.dropAll();

        }else{
            this.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true,this.getServer());
            this.experienceLevel = this.experienceLevel>35 ? this.experienceLevel-5 :0;
        }
    }


    @Inject(method = "attack",at = @At("HEAD"))
    public void attackStart(Entity target, CallbackInfo ci) {
        //经验攻击特效
        float experienceBonus = getDamageLevel(this.experienceLevel);
        //工具攻击特效
        float otherBonus = 1.0F;
        if(this.getMainHandStack().isIn(ModItemTags.DAGGERS) && target instanceof PassiveEntity) {
            otherBonus=1.5F;
        }
        if((this.getMainHandStack().isOf(Tools.SILVER_DAGGER) ) && target.getType().isIn(UNDEAD)) {
            otherBonus=1.5F;
        }
        if((this.getMainHandStack().isOf(Tools.SILVER_SWORD) ) && target.getType().isIn(UNDEAD)) {
            otherBonus=2F;
        }
        if((this.getMainHandStack().isOf(Tools.SILVER_HAMMER) ) && target.getType().isIn(UNDEAD)) {
            otherBonus=2F;
        }
        //锤子独立乘区
        if((this.getMainHandStack().isIn(ModItemTags.HAMMERS) ) && target.getType().isIn(SKELETONS)) {
            otherBonus*=1.5F;
        }

        //非独立乘区
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(experienceBonus*otherBonus);

        //例子: 玩家等级为5级,铜短剑的伤害为额外伤害为5点,使用跳斩伤害猪(10点生命)再获得1.5倍率伤害增益
        //基础伤害,面板伤害=(1.25*1.5+5)=6.875
        //最终伤害 = 6.875*1.5 ~ 10.8

        //其他逻辑

//            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(ServerInfoRecorder.getServerInstance());
//            int accuracy = 100 - serverState.playerDeathTimes - 40;
//            boolean shouldInvulnerable = this.getRandom().nextInt(100) >= accuracy;


    }

    @Inject(method = "attack",at = @At("TAIL"))
    public void attackEnd(Entity target, CallbackInfo ci) {
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
    }

    //植物营养素
    @Unique
    public long phytonutrient = 0;
    //生物交互距离增益







    @Shadow
    public int totalExperience;
    @Shadow
    private int lastPlayedLevelUpSoundTime;
    @Shadow
    public float experienceProgress;
    @Shadow
    public int experienceLevel;
    @Shadow
    protected HungerManager hungerManager;





    @Inject(method = "canHarvest", at = @At(value = "HEAD"), cancellable = true)
    public void canHarvest(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }



    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    public void eatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {



        //触发使用食物的事件
//        if(!world.isClient()) {
//            ActionResult result = OnPlayerEntityEatEvent.EVENT.invoker().interact(this.getWorld().getPlayerByUuid(this.getUuid()));
//        }

        if (stack.isIn(ModItemTags.HARMFOOD)){
            this.phytonutrient-=48000;
            StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.POISON, 400,0,true,true,true);
            this.setStatusEffect(statusEffectInstance,null);
        }
        if (stack.isIn(ModItemTags.PHYTONUTRIENT_LEVEL1)){
            this.phytonutrient+=6000;
//            if(!this.getWorld().isClient){
//                this.sendMessage(Text.of("食用了一个+6000植物营养素的食物,目前的植物营养素的值为: "+this.phytonutrient));
//            }
        }
        if (stack.isIn(ModItemTags.PHYTONUTRIENT_LEVEL2)){
            this.phytonutrient+=48000;
        }

    }




    //服务端调用
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        super.readCustomDataFromNbt(nbt);
        this.phytonutrient = nbt.getInt("Phytonutrient");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Phytonutrient", (int) this.phytonutrient);
    }


    @Shadow
    public double getEntityInteractionRange() {
        return this.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
    }
    @Shadow
    private final PlayerAbilities abilities = new PlayerAbilities();
    //加快饥饿速度
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    public void addExhaustion(float exhaustion, CallbackInfo ci) {
        ci.cancel();
        if (!this.abilities.invulnerable) {
            if (!this.getWorld().isClient) {
                this.hungerManager.addExhaustion(exhaustion*4);
            }
        }
    }
    @Unique
    public StateSaverAndLoader serverState;



//    StatusEffectInstance NIGHT_VISION = new StatusEffectInstance(StatusEffects.NIGHT_VISION,2000);
//    StatusEffectInstance MINING_FATIGUE = new StatusEffectInstance(StatusEffects.MINING_FATIGUE,2000);



    @Inject(method = "jump", at = @At("TAIL"))
    public void jump(CallbackInfo ci)  {
//        if(this.getWorld() instanceof ServerWorld serverWorld)
//            testChunkLoading(serverWorld,new ChunkPos(0,0));
        }




    //以下是修改方块交互距离
    @Inject(method = "getBlockInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)-1);

    }

    @Unique
    private double interactionRange = 2f;



    //以下修改实体交互距离
    @Inject(method = "getEntityInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        ItemStack itemStack = this.getMainHandStack();
        if (itemStack.isIn(ModItemTags.SHOVELS)) {
            //铲子
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.isIn(ModItemTags.PICKAXES)) {
            //镐子
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.isIn(ModItemTags.AXES)) {
            //斧子
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.isIn(ModItemTags.SWORDS)) {
            //剑
            interactionRange = 1.5f + 1f;
        } else if (itemStack.isIn(ModItemTags.HOES)) {
            //锄头
            interactionRange = 1.5f + 0.75f;
            //手斧
        } else if (itemStack.isIn(ModItemTags.HATCHET)) {
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.isIn(ModItemTags.DAGGERS)) {
            //小刀、匕首
            interactionRange = 1.5f + 0.5f;
        }
        else if (itemStack.isOf(Items.STICK) || itemStack.isOf(Items.BONE)) {
            //木棍和骨头
            interactionRange = 1.5f + 0.5f;
        } else {
            interactionRange = 1.5f + 0f;
        }


        RegistryKey<World> end = World.END;
        //潜行向下看时,增加生物交互距离
        if(this.isSneaking() && this.getPitch()>0)
            interactionRange += 0.5f;
        //末地可以摸到末影龙
        if(this.getWorld().getRegistryKey()==end){
            interactionRange += 1.5f;
        }

//        if(!this.getWorld().isClient)
//            this.sendMessage(Text.of("你的生物交互距离为"+interactionRange));

        cir.setReturnValue(interactionRange);

    }


    //玩家基础属性
    @Inject(method = "createPlayerAttributes", at = @At("HEAD"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(
                LivingEntity.createLivingAttributes()
                        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
                        .add(EntityAttributes.GENERIC_ATTACK_SPEED)
                        .add(EntityAttributes.GENERIC_LUCK)
                        .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                        .add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
                        .add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,1.5)
                        .add(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)
                        .add(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED)
                        .add(EntityAttributes.PLAYER_SNEAKING_SPEED)
                        .add(EntityAttributes.PLAYER_MINING_EFFICIENCY)
                        .add(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO)


        );


    }


    @Unique
    private int itemHarvest;
    @Unique
    private int blockHarvest;
    @Shadow
    @Final
    PlayerInventory inventory;


    @Shadow
    @Nullable
    public FishingBobberEntity fishHook;



    @Shadow
    public void addExhaustion(float exhaustion) {
        if (!this.abilities.invulnerable) {
            if (!this.getWorld().isClient) {
                this.hungerManager.addExhaustion(exhaustion);
            }
        }
    }


    //修改挖掘速度
    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        cir.cancel();

        this.addExhaustion(0.0005f);
        ItemStack stack = this.getMainHandStack();
        float f = this.inventory.getBlockBreakingSpeed(block);
        if (f > 1.0F) {
            f += (float) this.getAttributeValue(EntityAttributes.PLAYER_MINING_EFFICIENCY);
        }

        if (StatusEffectUtil.hasHaste(this)) {
            f *= 1.0F + (float) (StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2F;
        }

        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            f *= switch (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
        }

        f *= (float) this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
        if (this.isSubmergedIn(FluidTags.WATER)) {
            f *= (float) this.getAttributeInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).getValue();
        }

        if (!this.isOnGround()) {
            f /= 5.0F;
        }
        if(block.isIn(ModBlockTags.CATEGORY)||block.isIn(ModBlockTags.SHOULD_BE_SOFT))
            f= f * 8;

        if (stack.isSuitableFor(block)||(stack.isIn(ModItemTags.PICKAXES)&&block.isIn(ModBlockTags.ORE))) {
            f = f * 4;
        }

        if (stack.isSuitableFor(block)||(stack.isIn(ModItemTags.HAMMERS)&&block.isIn(ModBlockTags.ORE))) {
            f = f * 4;
        }

        this.itemHarvest = getItemHarvestLevel(stack);
        this.blockHarvest = getBlockHarvestLevel(block);
        if (this.itemHarvest >= this.blockHarvest) {

            cir.setReturnValue(f * (0.040F) * (this.experienceLevel<35?1 + this.experienceLevel * 0.1F :1.35F + this.experienceLevel * 0.1F));
        } else {
            cir.setReturnValue(0f);
        }

//        if (!this.getWorld().isClient) {
//            this.sendMessage(Text.of("" + getPhytonutrient()));
//        }

    }

    @Inject(method = "getNextLevelExperience", at = @At("HEAD"), cancellable = true)
    //经验曲线
    public void getNextLevelExperience(CallbackInfoReturnable<Integer> cir) {
        int level = this.experienceLevel;
        int nextLevel = 10 * (level + 1);
        cir.setReturnValue(nextLevel);

    }


    @Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
    public void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }






















    //营养不良造成的缓慢回血倍率
    @Unique
    public int malnourishedForSlowHealing;
    @Shadow
    public abstract boolean isCreative();

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow public abstract void sendMessage(Text message, boolean overlay);


    @Shadow public abstract Iterable<ItemStack> getArmorItems();


    @Shadow public abstract void tick();

    @Shadow public abstract float getAbsorptionAmount();

    @Shadow @Final protected static TrackedData<Byte> MAIN_ARM;

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow public abstract HungerManager getHungerManager();

    @Shadow public abstract void jump();


    @Shadow public abstract boolean canHarvest(BlockState state);

    @Shadow public abstract PlayerAbilities getAbilities();

    @Shadow public abstract void sendAbilitiesUpdate();

    @Unique
    private double lastSleepTime = 0;












    @Inject(method = "wakeUp(ZZ)V",at = @At("TAIL"))
    public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            double timeNow = this.getWorld().getTimeOfDay();
            if (timeNow - this.lastSleepTime > 7000) {
//                this.sendMessage(Text.of("睡得好!"));
                this.addExhaustion(7f);
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 150, 1));
            } else if (timeNow - this.lastSleepTime > 4000) {
//                this.sendMessage(Text.of("睡得还好!"));
                this.addExhaustion(4f);
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 75, 0));
            }
            else
                this.addExhaustion(3f);
        }

    }








    @Unique
    private float regerationFactor = 1;







    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci){






        //首日保护
        if(this.getWorld().getTimeOfDay()<24000) {
            this.phytonutrient = 192000;
            if(!this.hasStatusEffect(StatusEffects.SATURATION))
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 24000, 0, false, false, true));
        }



        if(this.isSleeping()&&!this.getWorld().isClient())
            this.lastSleepTime = this.getWorld().getTimeOfDay();




        //你也许可以用这个方法来改进生命值上限/饱食度上限
        //不要在这里使用这个代码,这会使得无时无刻玩家基础伤害固定为这个值从而打不出跳劈伤害,请到武器栏使用这个
//        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.min(1.0 + (this.experienceLevel * 0.01), 1.5));

        //更新生命值上限,最大生命值
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel((PlayerEntity)(Object)this));


        //更新回血速率
        this.regerationFactor = this.regerationFactor * this.phytonutrient < 100 ? 4 : 1;
        //秘银胸甲提供两倍回血速率
        if(this.getEquippedStack(EquipmentSlot.CHEST).isOf(Armors.MITHRIL_CHEST_PLATE))
            this.regerationFactor = this.regerationFactor * 0.5f;

        int maxHealth = PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel((PlayerEntity)(Object)this);
        if (this.age % (960 * regerationFactor) == 0) {
            //在tick中加入生命回复任务
            if (this.getHealth() < maxHealth) {
                this.heal(1.0F);
//                MITEequilibrium.LOGGER.info("Natural Regeneration +1 ");
            }
        }
        if (!this.getWorld().isClient) {
            this.phytonutrient--;
            //小于0就赋值为0,大于0不动
            this.phytonutrient = this.phytonutrient < 0 ? 0 : this.phytonutrient;
            //溢出判断,大于192000就为192000,否则不动
            this.phytonutrient = this.phytonutrient > 192000 ? 192000 : this.phytonutrient;
            //施加饥饿效果
            if (this.phytonutrient < 100) {
                if (!this.hasStatusEffect(registerStatusEffect.PHYTONUTRIENT)) {
                    StatusEffectInstance statusEffectInstance1 = new StatusEffectInstance(registerStatusEffect.PHYTONUTRIENT, -1, 0, false, false, false);
                    StatusEffectUtil.addEffectToPlayersWithinDistance((ServerWorld) this.getWorld(), this, this.getPos(), 16, statusEffectInstance1, -1);

                }
            } else {
                if (this.hasStatusEffect(registerStatusEffect.PHYTONUTRIENT)) {
                    this.removeStatusEffect(registerStatusEffect.PHYTONUTRIENT);
                }
            }
        }
    }



}



