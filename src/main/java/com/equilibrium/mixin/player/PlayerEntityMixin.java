package com.equilibrium.mixin.player;


import com.equilibrium.item.Armors;
import com.equilibrium.item.Tools;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.status.RegisterStatusEffect;
import com.equilibrium.status.disease_IR.DiabetesEffect;
import com.equilibrium.status.disease_IR.SugarMap;
import com.equilibrium.tags.ModItemTags;
import com.equilibrium.util.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
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
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
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
import static com.equilibrium.item.tools_attribute.ExtraDamageFromExperienceLevel.getDamageLevel;
import static com.equilibrium.status.disease_IR.DiabetesEffect.tryApplyDiabetesEffect;
import static com.equilibrium.util.ableToMine.getBlockHarvestLevel;
import static com.equilibrium.util.ableToMine.getItemHarvestLevel;
import static java.lang.Math.max;
import static net.minecraft.entity.attribute.EntityAttributes.*;
import static net.minecraft.registry.tag.EntityTypeTags.SKELETONS;
import static net.minecraft.registry.tag.EntityTypeTags.UNDEAD;
import static net.minecraft.util.math.MathHelper.nextBetween;

@Mixin(PlayerEntity.class)
//和源码构造方式一致,继承谁这里也跟着继承
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void PlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        Objects.requireNonNull(this.getAttributeInstance(GENERIC_ATTACK_DAMAGE),"error:com.equilibrium.mixin.player.PlayerEntityMixin").setBaseValue(1.0F);
        Objects.requireNonNull(this.getAttributeInstance(GENERIC_MOVEMENT_SPEED),"error:com.equilibrium.mixin.player.PlayerEntityMixin").setBaseValue(0.1F);
        Objects.requireNonNull(this.getAttributeInstance(PLAYER_ENTITY_INTERACTION_RANGE),"error:com.equilibrium.mixin.player.PlayerEntityMixin").setBaseValue(1.5F);
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
        if (serverState.playerDeathTimes == 1)
            this.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true, this.getServer());
        else if ((this.experienceLevel < 5)) {
            this.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(false, this.getServer());
            this.vanishCursedItems();
            //掉落所有物品
            this.inventory.dropAll();

        } else {
            this.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true, this.getServer());
            this.experienceLevel = this.experienceLevel > 35 ? this.experienceLevel - 5 : 0;
        }
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    private boolean modifyDamageForStick(Entity target, DamageSource source, float amount) {
        float otherBonus = 1.0F;

        if (this.getMainHandStack().isIn(ModItemTags.DAGGERS) && target instanceof PassiveEntity) {
            otherBonus = 1.5F;
        }
        if ((this.getMainHandStack().isOf(Tools.SILVER_DAGGER)) && target.getType().isIn(UNDEAD)) {
            otherBonus = 1.25F;
        }
        if ((this.getMainHandStack().isOf(Tools.SILVER_SWORD)) && target.getType().isIn(UNDEAD)) {
            otherBonus = 1.5F;
        }
        if ((this.getMainHandStack().isOf(Tools.SILVER_HAMMER)) && target.getType().isIn(UNDEAD)) {
            otherBonus = 1.5F;
        }
        //锤子独立乘区
        if ((this.getMainHandStack().isIn(ModItemTags.HAMMERS)) && target.getType().isIn(SKELETONS)) {
            otherBonus *= 1.5F;
        }
        if (source.getAttacker() instanceof ServerPlayerEntity player && isShowDamageEnabled()) {
            player.sendMessage(Text.of(String.valueOf(amount*otherBonus)),true);

        }
        return target.damage(source, amount*otherBonus);
    }







    @Inject(method = "attack", at = @At("HEAD"))
    public void attackStart(Entity target, CallbackInfo ci) {
        //经验攻击特效
        float experienceBonus = getDamageLevel(this.experienceLevel);
        //工具攻击特效
        float otherBonus = 1.0F;

        //非独立乘区
        this.getAttributeInstance(GENERIC_ATTACK_DAMAGE).setBaseValue(experienceBonus * otherBonus);

        //其他逻辑

//            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(ServerInfoRecorder.getServerInstance());
//            int accuracy = 100 - serverState.playerDeathTimes - 40;
//            boolean shouldInvulnerable = this.getRandom().nextInt(100) >= accuracy;


    }

    @Inject(method = "attack", at = @At("TAIL"))
    public void attackEnd(Entity target, CallbackInfo ci) {
        this.getAttributeInstance(GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
    }



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

    //植物营养素
    @Unique
    public long phytonutrient = 0;
    //胰岛素抵抗
    @Unique
    public int diabetes = 48000;

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    public void eatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {


        //触发使用食物的事件
//        if(!world.isClient()) {
//            ActionResult result = OnPlayerEntityEatEvent.EVENT.invoker().interact(this.getWorld().getPlayerByUuid(this.getUuid()));
//        }

        if (stack.isIn(ModItemTags.HARMFOOD)) {
            this.phytonutrient -= 48000;
            StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.POISON, 400, 0, true, true, true);
            this.setStatusEffect(statusEffectInstance, null);
        }
        if (stack.isIn(ModItemTags.PHYTONUTRIENT_LEVEL1)) {
            this.phytonutrient += 6000;
//            if(!this.getWorld().isClient){
//                this.sendMessage(Text.of("食用了一个+6000植物营养素的食物,目前的植物营养素的值为: "+this.phytonutrient));
//            }
        }
        if (stack.isIn(ModItemTags.PHYTONUTRIENT_LEVEL2)) {
            this.phytonutrient += 48000;
        }
        int sugarAmount = SugarMap.SUGAR_MAP.getOrDefault(stack.getItem(),0);
        this.diabetes+= sugarAmount;
        if(!this.getWorld().isClient()&&sugarAmount>0)
            tryApplyDiabetesEffect((PlayerEntity)(Object)this,diabetes);
    }



    //服务端调用
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        super.readCustomDataFromNbt(nbt);
        this.phytonutrient = nbt.getInt("phytonutrient");
        this.diabetes = nbt.getInt("diabetes");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("phytonutrient", (int) this.phytonutrient);
        nbt.putInt("diabetes", this.diabetes);
    }


    @Shadow
    public double getEntityInteractionRange() {
        return this.getAttributeValue(PLAYER_ENTITY_INTERACTION_RANGE);
    }

    @Shadow
    private final PlayerAbilities abilities = new PlayerAbilities();

    //加快饥饿速度
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    public void addExhaustion(float exhaustion, CallbackInfo ci) {
        ci.cancel();
        if (!this.abilities.invulnerable) {
            if (!this.getWorld().isClient) {
                this.hungerManager.addExhaustion(exhaustion * 4);
            }
        }
    }

    @Unique
    public StateSaverAndLoader serverState;


//    StatusEffectInstance NIGHT_VISION = new StatusEffectInstance(StatusEffects.NIGHT_VISION,2000);
//    StatusEffectInstance MINING_FATIGUE = new StatusEffectInstance(StatusEffects.MINING_FATIGUE,2000);


    @Inject(method = "jump", at = @At("TAIL"))
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
    @Inject(method = "getBlockInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) - 1);

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
        } else if (itemStack.isOf(Items.STICK) || itemStack.isOf(Items.BONE)) {
            //木棍和骨头
            interactionRange = 1.5f + 0.5f;
        } else {
            interactionRange = 1.5f + 0f;
        }


        RegistryKey<World> end = World.END;
        //潜行向下看时,增加生物交互距离
        if (this.isSneaking() && this.getPitch() > 0)
            interactionRange += 0.5f;
        //末地可以摸到末影龙
        if (this.getWorld().getRegistryKey() == end) {
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
        if (stack.isSuitableFor(block)) {
            f = f * 16;
        }

        //玩家挖掘时,会在客户端和服务端均进行计算,发包时是客户端发包,再看服务端那边是否已计算完毕
        //有的时候由于服务端延迟,导致客户端已挖掘的方块在服务端那边还没有被计算完毕,导致幽灵方块现象
        if (this.getWorld() instanceof ServerWorld serverWorld) {
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

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    public abstract void sendMessage(Text message, boolean overlay);


    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();


    @Shadow
    public abstract void tick();

    @Shadow
    public abstract float getAbsorptionAmount();

    @Shadow
    @Final
    protected static TrackedData<Byte> MAIN_ARM;

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Shadow
    public abstract PlayerInventory getInventory();

    @Shadow
    public abstract HungerManager getHungerManager();

    @Shadow
    public abstract void jump();


    @Shadow
    public abstract boolean canHarvest(BlockState state);

    @Shadow
    public abstract PlayerAbilities getAbilities();

    @Shadow
    public abstract void sendAbilitiesUpdate();

    @Shadow
    protected abstract float getOffGroundSpeed();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Unique
    private double lastSleepTime = 0;


    @Inject(method = "wakeUp(ZZ)V", at = @At("TAIL"))
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
            } else
                this.addExhaustion(3f);
        }

    }


    @Unique
    private float regerationFactor = 1;


    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        //首日保护
        if (this.getWorld().getTimeOfDay() < 24000) {
            this.phytonutrient = 192000;
            if (!this.hasStatusEffect(StatusEffects.SATURATION))
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 24000, 0, false, false, true));
            this.diabetes=0;
        }


        if (this.isSleeping() && !this.getWorld().isClient())
            this.lastSleepTime = this.getWorld().getTimeOfDay();


        //你也许可以用这个方法来改进生命值上限/饱食度上限
        //不要在这里使用这个代码,这会使得无时无刻玩家基础伤害固定为这个值从而打不出跳劈伤害,请到武器栏使用这个
//        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.min(1.0 + (this.experienceLevel * 0.01), 1.5));

        //更新生命值上限,最大生命值
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel((PlayerEntity) (Object) this));


        //更新回血速率
        this.regerationFactor = this.regerationFactor * this.phytonutrient < 100 ? 4 : 1;
        //秘银胸甲提供两倍回血速率
        if (this.getEquippedStack(EquipmentSlot.CHEST).isOf(Armors.MITHRIL_CHEST_PLATE))
            this.regerationFactor = this.regerationFactor * 0.5f;

        int maxHealth = PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel((PlayerEntity) (Object) this);
        if (this.age % (960 * regerationFactor) == 0) {
            //在tick中加入生命回复任务
            if (this.getHealth() < maxHealth) {
                this.heal(1.0F);
//                MITEequilibrium.LOGGER.info("Natural Regeneration +1 ");
            }
        }
        if (!this.getWorld().isClient) {
            if (this.getWorld().isRaining() && this.getWorld().isSkyVisible(this.getBlockPos())) {
                //雨天施加挖掘疲劳
                boolean hasMiningFatigue = this.hasStatusEffect(StatusEffects.MINING_FATIGUE);
                if (!hasMiningFatigue)
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 120, 0, false, false, false));
                else if (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getDuration() <= 20) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 120, 0, false, false, false));
                }
                //雨天施加饥饿
                this.addExhaustion(0.0005f);
            }





            //糖尿病计时器,每一刻降低1,如果没有开启任何进阶难度词条,则此机制时刻将总糖值设为0,可以保护玩家一定不受糖尿病影响
            this.diabetes=isAnyExtraEntryExisting(this.getServer(),null)?diabetes-1:0;
            //小于0就赋值为0,大于0不动
            this.diabetes = this.diabetes < 0 ? 0 : diabetes;
            //溢出判断,大于96000就为96000,否则不动
            this.diabetes = this.diabetes > 96000 ? 96000 : this.diabetes;
            int diabetesProgress = DiabetesEffect.getProgress(diabetes);
            if (diabetesProgress!=-1) {
                this.setStatusEffect(new StatusEffectInstance(RegisterStatusEffect.INSULIN_RESISTANCE, -1, diabetesProgress, false, false, false),null);
            } else {
                if (this.hasStatusEffect(RegisterStatusEffect.INSULIN_RESISTANCE)) {
                    this.removeStatusEffect(RegisterStatusEffect.INSULIN_RESISTANCE);
                }
            }







            //规则:是否启用营养不良?
            if (getGameBooleanRuleFromServer(ENABLE_PHYTONUTRIENT, Objects.requireNonNull(this.getWorld().getServer(), "Server can not be found ? Impossible!")))
                this.phytonutrient--;

            //小于0就赋值为0,大于0不动
            this.phytonutrient = this.phytonutrient < 0 ? 0 : this.phytonutrient;
            //溢出判断,大于192000就为192000,否则不动
            this.phytonutrient = this.phytonutrient > 192000 ? 192000 : this.phytonutrient;
            //施加饥饿效果
            if (this.phytonutrient < 100) {
                if (!this.hasStatusEffect(RegisterStatusEffect.PHYTONUTRIENT)) {
                    StatusEffectInstance statusEffectInstance1 = new StatusEffectInstance(RegisterStatusEffect.PHYTONUTRIENT, -1, 0, false, false, false);
                    StatusEffectUtil.addEffectToPlayersWithinDistance((ServerWorld) this.getWorld(), this, this.getPos(), 16, statusEffectInstance1, -1);

                }
            } else {
                if (this.hasStatusEffect(RegisterStatusEffect.PHYTONUTRIENT)) {
                    this.removeStatusEffect(RegisterStatusEffect.PHYTONUTRIENT);
                }
            }
        }
    }


}



