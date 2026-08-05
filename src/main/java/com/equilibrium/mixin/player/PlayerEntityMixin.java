package com.equilibrium.mixin.player;


import com.equilibrium.item.armor.ArmorItems;
import com.equilibrium.item.tool.ToolItems;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.status.RegisterStatusEffect;
import com.equilibrium.status.disease_IR.DiabetesEffect;
import com.equilibrium.status.disease_IR.SugarMap;
import com.equilibrium.tags.ModItemTags;
import com.equilibrium.util.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.Objects;

import static com.equilibrium.GlobalModConfig.isShowDamageEnabled;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_PHYTONUTRIENT;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_SLOW_BREAKING_SPEED;
import static com.equilibrium.item.tool.ExtraDamageFromExperienceLevel.getDamageLevel;
import static com.equilibrium.status.disease_IR.DiabetesEffect.tryApplyDiabetesEffect;
import static com.equilibrium.util.ableToMine.getBlockHarvestLevel;
import static com.equilibrium.util.ableToMine.getItemHarvestLevel;
import static java.lang.Math.max;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static net.minecraft.tags.EntityTypeTags.SKELETONS;
import static net.minecraft.tags.EntityTypeTags.UNDEAD;

@Mixin(Player.class)
//和源码构造方式一致,继承谁这里也跟着继承
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void PlayerEntity(Level world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        Objects.requireNonNull(this.getAttribute(ATTACK_DAMAGE),"error:com.equilibrium.mixin.player.PlayerEntityMixin").setBaseValue(1.0F);
        Objects.requireNonNull(this.getAttribute(MOVEMENT_SPEED),"error:com.equilibrium.mixin.player.PlayerEntityMixin").setBaseValue(0.1F);
        Objects.requireNonNull(this.getAttribute(ENTITY_INTERACTION_RANGE),"error:com.equilibrium.mixin.player.PlayerEntityMixin").setBaseValue(1.5F);
    }


    @Shadow
    protected abstract void destroyVanishingCursedItems();


    @Override
    public void dropEquipment() {
        super.dropEquipment();
        serverState = StateSaverAndLoader.getServerState(this.getServer());
        //首次死亡的掉落保护
        if (serverState.playerDeathTimes == 1)
            this.level().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, this.getServer());
        else if ((this.experienceLevel < 5)) {
            this.level().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(false, this.getServer());
            this.destroyVanishingCursedItems();
            //掉落所有物品
            this.inventory.dropAll();

        } else {
            this.level().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, this.getServer());
            this.experienceLevel = this.experienceLevel > 35 ? this.experienceLevel - 5 : 0;
        }
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean modifyDamageForStick(Entity target, DamageSource source, float amount) {
        float otherBonus = 1.0F;

        if (this.getMainHandItem().is(ModItemTags.DAGGERS) && target instanceof AgeableMob) {
            otherBonus = 1.5F;
        }
        if ((this.getMainHandItem().is(Tools.SILVER_DAGGER.get())) && target.getType().is(UNDEAD)) {
            otherBonus = 1.25F;
        }
        if ((this.getMainHandItem().is(Tools.SILVER_SWORD.get())) && target.getType().is(UNDEAD)) {
            otherBonus = 1.5F;
        }
        if ((this.getMainHandItem().is(Tools.SILVER_HAMMER.get())) && target.getType().is(UNDEAD)) {
            otherBonus = 1.5F;
        }
        //锤子独立乘区
        if ((this.getMainHandItem().is(ModItemTags.HAMMERS)) && target.getType().is(SKELETONS)) {
            otherBonus *= 1.5F;
        }
        if (source.getEntity() instanceof ServerPlayer player && isShowDamageEnabled()) {
            player.displayClientMessage(Component.nullToEmpty(String.valueOf(amount*otherBonus)),true);

        }
        return target.hurt(source, amount*otherBonus);
    }







    @Inject(method = "attack", at = @At("HEAD"))
    public void attackStart(Entity target, CallbackInfo ci) {
        //经验攻击特效
        float experienceBonus = getDamageLevel(this.experienceLevel);
        //工具攻击特效
        float otherBonus = 1.0F;

        //非独立乘区
        this.getAttribute(ATTACK_DAMAGE).setBaseValue(experienceBonus * otherBonus);

        //其他逻辑

//            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(ServerInfoRecorder.getServerInstance());
//            int accuracy = 100 - serverState.playerDeathTimes - 40;
//            boolean shouldInvulnerable = this.getRandom().nextInt(100) >= accuracy;


    }

    @Inject(method = "attack", at = @At("TAIL"))
    public void attackEnd(Entity target, CallbackInfo ci) {
        this.getAttribute(ATTACK_DAMAGE).setBaseValue(1.0);
    }

    @Shadow
    public int experienceLevel;
    @Shadow
    protected FoodData foodData;


    @Inject(method = "hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At(value = "HEAD"), cancellable = true)
    public void canHarvest(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    //植物营养素
    @Unique
    public long phytonutrient = 0;
    //胰岛素抵抗
    @Unique
    public int diabetes = 48000;

    @Inject(method = "eat", at = @At(value = "HEAD"))
    public void eatFood(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> cir) {


        //触发使用食物的事件
//        if(!world.isClient()) {
//            ActionResult result = OnPlayerEntityEatEvent.EVENT.invoker().interact(this.getWorld().getPlayerByUuid(this.getUuid()));
//        }

        if (stack.is(ModItemTags.HARMFOOD)) {
            this.phytonutrient -= 48000;
            MobEffectInstance statusEffectInstance = new MobEffectInstance(MobEffects.POISON, 400, 0, true, true, true);
            this.forceAddEffect(statusEffectInstance, null);
        }
        if (stack.is(ModItemTags.PHYTONUTRIENT_LEVEL1)) {
            this.phytonutrient += 6000;
//            if(!this.getWorld().isClient){
//                this.sendMessage(Text.of("食用了一个+6000植物营养素的食物,目前的植物营养素的值为: "+this.phytonutrient));
//            }
        }
        if (stack.is(ModItemTags.PHYTONUTRIENT_LEVEL2)) {
            this.phytonutrient += 48000;
        }
        int sugarAmount = SugarMap.SUGAR_MAP.getOrDefault(stack.getItem(),0);
        this.diabetes+= sugarAmount;
        if(!this.level().isClientSide()&&sugarAmount>0)
            tryApplyDiabetesEffect((Player)(Object)this,diabetes);
    }



    //服务端调用
    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    public void readCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        super.readAdditionalSaveData(nbt);
        this.phytonutrient = nbt.getInt("phytonutrient");
        this.diabetes = nbt.getInt("diabetes");
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("phytonutrient", (int) this.phytonutrient);
        nbt.putInt("diabetes", this.diabetes);
    }

    @Shadow
    private final Abilities abilities = new Abilities();

    //加快饥饿速度
    @Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
    public void causeFoodExhaustion(float exhaustion, CallbackInfo ci) {
        ci.cancel();
        if (!this.abilities.invulnerable) {
            if (!this.level().isClientSide) {
                this.foodData.addExhaustion(exhaustion * 4);
            }
        }
    }

    @Unique
    public StateSaverAndLoader serverState;


//    StatusEffectInstance NIGHT_VISION = new StatusEffectInstance(StatusEffects.NIGHT_VISION,2000);
//    StatusEffectInstance MINING_FATIGUE = new StatusEffectInstance(StatusEffects.MINING_FATIGUE,2000);


    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    public void jump(CallbackInfo ci) {


//
//        this.sendMessage(Text.of(""+getAttributeInstance(GENERIC_ATTACK_DAMAGE).getBaseValue()));
//        this.sendMessage(Text.of(""+getAttributeInstance(GENERIC_MOVEMENT_SPEED).getBaseValue()));
//        this.sendMessage(Text.of(""+getAttributeInstance(PLAYER_ENTITY_INTERACTION_RANGE).getBaseValue()));

//        this.diabetes=0;
//        if((PlayerEntity)(Object)this instanceof ServerPlayerEntity serverPlayerEntity)
//            showAllValuesToServerPlayer(serverPlayerEntity);
//        if(this.getWorld() instanceof ServerWorld serverWorld)
//            testChunkLoading(serverWorld,new ChunkPos(0,0));
    }


    //以下是修改方块交互距离
    @Inject(method = "blockInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(this.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) - 1);

    }

    @Unique
    private double interactionRange = 2f;


    //以下修改实体交互距离
    @Inject(method = "entityInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        ItemStack itemStack = this.getMainHandItem();
        if (itemStack.is(ModItemTags.SHOVELS)) {
            //铲子
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.is(ModItemTags.PICKAXES)) {
            //镐子
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.is(ModItemTags.AXES)) {
            //斧子
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.is(ModItemTags.SWORDS)) {
            //剑
            interactionRange = 1.5f + 1f;
        } else if (itemStack.is(ModItemTags.HOES)) {
            //锄头
            interactionRange = 1.5f + 0.75f;
            //手斧
        } else if (itemStack.is(ModItemTags.HATCHET)) {
            interactionRange = 1.5f + 0.75f;
        } else if (itemStack.is(ModItemTags.DAGGERS)) {
            //小刀、匕首
            interactionRange = 1.5f + 0.5f;
        } else if (itemStack.is(Items.STICK) || itemStack.is(Items.BONE)) {
            //木棍和骨头
            interactionRange = 1.5f + 0.5f;
        } else {
            interactionRange = 1.5f + 0f;
        }


        ResourceKey<Level> end = Level.END;
        //潜行向下看时,增加生物交互距离
        if (this.isShiftKeyDown() && this.getXRot() > 0)
            interactionRange += 0.5f;
        //末地可以摸到末影龙
        if (this.level().dimension() == end) {
            interactionRange += 1.5f;
        }

//        if(!this.getWorld().isClient)
//            this.sendMessage(Text.of("你的生物交互距离为"+interactionRange));

        cir.setReturnValue(interactionRange);

    }



    @Unique
    private int itemHarvest;
    @Unique
    private int blockHarvest;
    @Shadow
    @Final
    Inventory inventory;


    @Shadow
    public abstract void causeFoodExhaustion(float exhaustion);


    //修改挖掘速度
    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        cir.cancel();
        this.causeFoodExhaustion(0.0005f);
        ItemStack stack = this.getMainHandItem();
        float f = this.inventory.getDestroySpeed(block);
        if (f > 1.0F) {
            f += (float) this.getAttributeValue(Attributes.MINING_EFFICIENCY);
        }

        if (MobEffectUtil.hasDigSpeed(this)) {
            f *= 1.0F + (float) (MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            f *= switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
        }

        f *= (float) this.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
        if (this.isEyeInFluid(FluidTags.WATER)) {
            f *= (float) this.getAttribute(Attributes.SUBMERGED_MINING_SPEED).getValue();
        }

        if (!this.onGround()) {
            f /= 5.0F;
        }
        if (stack.isCorrectToolForDrops(block)) {
            f = f * 16;
        }

        //玩家挖掘时,会在客户端和服务端均进行计算,发包时是客户端发包,再看服务端那边是否已计算完毕
        //有的时候由于服务端延迟,导致客户端已挖掘的方块在服务端那边还没有被计算完毕,导致幽灵方块现象
        if (this.level() instanceof ServerLevel serverWorld) {
            if (!getGameBooleanRuleFromServer(ENABLE_SLOW_BREAKING_SPEED, serverWorld.getServer())) {
                f = f * 16;
            }
        } else {
            if (!getGameBooleanRuleFromClient(ENABLE_SLOW_BREAKING_SPEED)) {
                f = f * 16;
            }
        }

        this.itemHarvest = getItemHarvestLevel(stack);
        this.blockHarvest = getBlockHarvestLevel(block);
        if (this.itemHarvest >= this.blockHarvest) {

            cir.setReturnValue(f * (0.040F) * (this.experienceLevel < 35 ? 1 + this.experienceLevel * 0.1F : 1.35F + this.experienceLevel * 0.1F));
        } else {
            cir.setReturnValue(0f);
        }


    }

    @Inject(method = "getXpNeededForNextLevel", at = @At("HEAD"), cancellable = true)
    //经验曲线
    public void getNextLevelExperience(CallbackInfoReturnable<Integer> cir) {
        int level = this.experienceLevel;
        int nextLevel = 10 * (level + 1);
        cir.setReturnValue(nextLevel);

    }


    @Inject(method = "isHurt", at = @At("HEAD"), cancellable = true)
    public void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }


    //营养不良造成的缓慢回血倍率
    @Unique
    public int malnourishedForSlowHealing;

    @Shadow
    public abstract boolean isCreative();

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract float getAbsorptionAmount();

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow
    public abstract Inventory getInventory();

    @Shadow
    public abstract void jumpFromGround();


    @Shadow
    protected abstract float getFlyingSpeed();

    @Shadow public abstract boolean hurt(DamageSource source, float amount);

    @Unique
    private double lastSleepTime = 0;


    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        if (!this.level().isClientSide) {
            double timeNow = this.level().getDayTime();
            if (timeNow - this.lastSleepTime > 7000) {
//                this.sendMessage(Text.of("睡得好!"));
                this.causeFoodExhaustion(7f);
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 150, 1));
            } else if (timeNow - this.lastSleepTime > 4000) {
//                this.sendMessage(Text.of("睡得还好!"));
                this.causeFoodExhaustion(4f);
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 75, 0));
            } else
                this.causeFoodExhaustion(3f);
        }

    }


    @Unique
    private float regerationFactor = 1;


    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        //首日保护
        if (this.level().getDayTime() < 24000) {
            this.phytonutrient = 192000;
            if (!this.hasEffect(MobEffects.SATURATION))
                this.addEffect(new MobEffectInstance(MobEffects.SATURATION, 24000, 0, false, false, true));
            this.diabetes=0;
        }


        if (this.isSleeping() && !this.level().isClientSide())
            this.lastSleepTime = this.level().getDayTime();


        //你也许可以用这个方法来改进生命值上限/饱食度上限
        //不要在这里使用这个代码,这会使得无时无刻玩家基础伤害固定为这个值从而打不出跳劈伤害,请到武器栏使用这个
//        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.min(1.0 + (this.experienceLevel * 0.01), 1.5));

        //更新生命值上限,最大生命值
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel((Player) (Object) this));


        //更新回血速率
        this.regerationFactor = this.regerationFactor * this.phytonutrient < 100 ? 4 : 1;
        //秘银胸甲提供两倍回血速率
        if (this.getItemBySlot(EquipmentSlot.CHEST).is(Armors.MITHRIL_CHEST_PLATE))
            this.regerationFactor = this.regerationFactor * 0.5f;

        int maxHealth = PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel((Player) (Object) this);
        if (this.tickCount % (960 * regerationFactor) == 0) {
            //在tick中加入生命回复任务
            if (this.getHealth() < maxHealth) {
                this.heal(1.0F);
//                MITEequilibrium.LOGGER.info("Natural Regeneration +1 ");
            }
        }
        if (!this.level().isClientSide) {
            if (this.level().isRaining() && this.level().canSeeSky(this.blockPosition())) {
                //雨天施加挖掘疲劳
                boolean hasMiningFatigue = this.hasEffect(MobEffects.DIG_SLOWDOWN);
                if (!hasMiningFatigue)
                    this.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0, false, false, false));
                else if (this.getEffect(MobEffects.DIG_SLOWDOWN).getDuration() <= 20) {
                    this.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0, false, false, false));
                }
                //雨天施加饥饿
                this.causeFoodExhaustion(0.0005f);
            }





            //糖尿病计时器,每一刻降低1,如果没有开启任何进阶难度词条,则此机制时刻将总糖值设为0,可以保护玩家一定不受糖尿病影响
            this.diabetes=isAnyExtraEntryExisting(this.getServer(),null)?diabetes-1:0;
            //小于0就赋值为0,大于0不动
            this.diabetes = this.diabetes < 0 ? 0 : diabetes;
            //溢出判断,大于96000就为96000,否则不动
            this.diabetes = this.diabetes > 96000 ? 96000 : this.diabetes;
            int diabetesProgress = DiabetesEffect.getProgress(diabetes);
            if (diabetesProgress!=-1) {
                this.forceAddEffect(new MobEffectInstance(RegisterStatusEffect.INSULIN_RESISTANCE, -1, diabetesProgress, false, false, false),null);
            } else {
                if (this.hasEffect(RegisterStatusEffect.INSULIN_RESISTANCE)) {
                    this.removeEffect(RegisterStatusEffect.INSULIN_RESISTANCE);
                }
            }







            //规则:是否启用营养不良?
            if (getGameBooleanRuleFromServer(ENABLE_PHYTONUTRIENT, Objects.requireNonNull(this.level().getServer(), "Server can not be found ? Impossible!")))
                this.phytonutrient--;

            //小于0就赋值为0,大于0不动
            this.phytonutrient = this.phytonutrient < 0 ? 0 : this.phytonutrient;
            //溢出判断,大于192000就为192000,否则不动
            this.phytonutrient = this.phytonutrient > 192000 ? 192000 : this.phytonutrient;
            //施加饥饿效果
            if (this.phytonutrient < 100) {
                if (!this.hasEffect(RegisterStatusEffect.PHYTONUTRIENT)) {
                    MobEffectInstance statusEffectInstance1 = new MobEffectInstance(RegisterStatusEffect.PHYTONUTRIENT, -1, 0, false, false, false);
                    MobEffectUtil.addEffectToPlayersAround((ServerLevel) this.level(), this, this.position(), 16, statusEffectInstance1, -1);

                }
            } else {
                if (this.hasEffect(RegisterStatusEffect.PHYTONUTRIENT)) {
                    this.removeEffect(RegisterStatusEffect.PHYTONUTRIENT);
                }
            }
        }
    }


}



