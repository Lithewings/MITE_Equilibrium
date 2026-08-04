package com.equilibrium.entity.mob.earth_elemental;

import com.equilibrium.item.Tools;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractEarthElementalEntity extends Monster {


    protected final Block material;

    protected AbstractEarthElementalEntity(EntityType<? extends Monster> entityType, Level world, Block material) {
        super(entityType, world);
        this.material = material;
    }

    //建议进行单独配置而不是硬编码
    public static Map<Block, Float> HARDNESS_LEVEL = Map.of(
            Blocks.COBBLESTONE, 1F,
            Blocks.END_STONE, 1F,
            Blocks.NETHERRACK, 1F,
            Blocks.OBSIDIAN, 2F
    );

    public static final float HARDNESS_LEVEL_MIN = Collections.min(HARDNESS_LEVEL.values());
    public static final float HARDNESS_LEVEL_MAX = Collections.max(HARDNESS_LEVEL.values());

    public float getKnockBackResistance() {
        float hardness = HARDNESS_LEVEL.getOrDefault(material, 1F);

        float targetMin = 0.6f;
        float targetMax = 0.8f;

        // 处理所有硬度相同的情况(防御性编程)
        if (HARDNESS_LEVEL_MIN == HARDNESS_LEVEL_MAX) {
            return targetMax;
        }
        // 归一化到 [0, 1]
        float normalized = (hardness - HARDNESS_LEVEL_MIN) / (HARDNESS_LEVEL_MAX - HARDNESS_LEVEL_MIN);
        // 映射到目标区间 [0.6, 0.8]
        return normalized * (targetMax - targetMin) + targetMin;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        return world.getBlockState(this.blockPosition().below()).is(material);
    }

    @Override
    protected abstract int getBaseExperienceReward();

    @Override
    protected abstract ResourceKey<LootTable> getDefaultLootTable();

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.initCustomGoals();
    }

    public void initCustomGoals() {
        this.goalSelector.addGoal(2, new EarthElementalAttackGoal<>(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

    }

    //该Goal的new中,对于Entity只能传入AbstractEarthElementalEntity的子类
    protected static class EarthElementalAttackGoal<T extends AbstractEarthElementalEntity> extends MeleeAttackGoal {
        //取自僵尸的attackGoal逻辑
        private final AbstractEarthElementalEntity mob;
        private int ticks;


        public EarthElementalAttackGoal(T mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
            this.mob = mob;
        }

        @Override
        public void start() {
            super.start();
            this.ticks = 0;
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
        }

        @Override
        public void tick() {
            super.tick();
            this.ticks++;
            if (this.ticks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
                this.mob.setAggressive(true);
            } else {
                this.mob.setAggressive(false);
            }
        }
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        //仅仅受来自玩家的镐类工具和锤类工具的伤害,免疫非间接伤害,诸如弓箭、药水、来自岩浆等环境伤害
        if (source.getEntity() instanceof Player player) {
            //source.isDirect()确保attacker为玩家
            ItemStack mainHandItemstack = player.getMainHandItem();
            if (source.isDirect() && (mainHandItemstack.is(ModItemTags.PICKAXES) || mainHandItemstack.is(ModItemTags.HAMMERS))) {
                //默认从1开始计算,铜锤银镐金锤等都是等级1
                int weaponHardness = getWeaponHardness(mainHandItemstack);
                if (weaponHardness >= HARDNESS_LEVEL.getOrDefault(material, 0F))
                    //额外条件通过,以下为原版判断过程
                    return super.hurt(source, amount);
                else
                    return false;
            }
        }
        boolean isKill = source.typeHolder().is(DamageTypes.GENERIC_KILL);

        if (isKill)
            return super.hurt(source, amount);


        boolean isExplosion = source.typeHolder().is(DamageTypes.EXPLOSION) ||source.typeHolder().is(DamageTypes.PLAYER_EXPLOSION);
        boolean isFall = source.typeHolder().is(DamageTypes.FALL);
        boolean isLightening = source.typeHolder().is(DamageTypes.LIGHTNING_BOLT);

        if (isExplosion || isFall || isLightening)
            return super.hurt(source, amount);
        else
            return false;

    }

    private static int getWeaponHardness(ItemStack mainHandItemstack) {
        int weaponHardness = 1;

        if (mainHandItemstack.is(Tools.IRON_PICKAXE.get()) || mainHandItemstack.is(Tools.IRON_HAMMER.get()))
            weaponHardness = 2;
        else if (mainHandItemstack.is(Tools.MITHRIL_PICKAXE.get()) || mainHandItemstack.is(Tools.MITHRIL_HAMMER.get()))
            weaponHardness = 3;
        else if (mainHandItemstack.is(Tools.ADAMANTIUM_PICKAXE.get()) || mainHandItemstack.is(Tools.ADAMANTIUM_HAMMER.get()))
            weaponHardness = 4;
        return weaponHardness;
    }
}
