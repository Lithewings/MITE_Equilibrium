package com.equilibrium.entity.mob.earth_elemental;

import com.equilibrium.item.Tools;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractEarthElementalEntity extends HostileEntity {


    protected final Block material;

    protected AbstractEarthElementalEntity(EntityType<? extends HostileEntity> entityType, World world, Block material) {
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
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return world.getBlockState(this.getBlockPos().down()).isOf(material);
    }

    @Override
    protected abstract int getXpToDrop();

    @Override
    protected abstract RegistryKey<LootTable> getLootTableId();

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void initGoals() {

        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    public void initCustomGoals() {
        this.goalSelector.add(2, new EarthElementalAttackGoal<>(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));

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
            this.mob.setAttacking(false);
        }

        @Override
        public void tick() {
            super.tick();
            this.ticks++;
            if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
                this.mob.setAttacking(true);
            } else {
                this.mob.setAttacking(false);
            }
        }
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        //仅仅受来自玩家的镐类工具和锤类工具的伤害,免疫非间接伤害,诸如弓箭、药水、来自岩浆等环境伤害
        if (source.getAttacker() instanceof PlayerEntity player) {
            //source.isDirect()确保attacker为玩家
            ItemStack mainHandItemstack = player.getMainHandStack();
            if (source.isDirect() && (mainHandItemstack.isIn(ModItemTags.PICKAXES) || mainHandItemstack.isIn(ModItemTags.HAMMERS))) {
                //默认从1开始计算,铜锤银镐金锤等都是等级1
                int weaponHardness = getWeaponHardness(mainHandItemstack);
                if (weaponHardness >= HARDNESS_LEVEL.getOrDefault(material, 0F))
                    //额外条件通过,以下为原版判断过程
                    return super.damage(source, amount);
                else
                    return false;
            }
        }
        boolean isKill = source.getTypeRegistryEntry().matchesKey(DamageTypes.GENERIC_KILL);

        if (isKill)
            return super.damage(source, amount);


        boolean isExplosion = source.getTypeRegistryEntry().matchesKey(DamageTypes.EXPLOSION) ||source.getTypeRegistryEntry().matchesKey(DamageTypes.PLAYER_EXPLOSION);
        boolean isFall = source.getTypeRegistryEntry().matchesKey(DamageTypes.FALL);
        boolean isLightening = source.getTypeRegistryEntry().matchesKey(DamageTypes.LIGHTNING_BOLT);

        if (isExplosion || isFall || isLightening)
            return super.damage(source, amount);
        else
            return false;

    }

    private static int getWeaponHardness(ItemStack mainHandItemstack) {
        int weaponHardness = 1;

        if (mainHandItemstack.isOf(Tools.IRON_PICKAXE) || mainHandItemstack.isOf(Tools.IRON_HAMMER))
            weaponHardness = 2;
        else if (mainHandItemstack.isOf(Tools.MITHRIL_PICKAXE) || mainHandItemstack.isOf(Tools.MITHRIL_HAMMER))
            weaponHardness = 3;
        else if (mainHandItemstack.isOf(Tools.ADAMANTIUM_PICKAXE) || mainHandItemstack.isOf(Tools.ADAMANTIUM_HAMMER))
            weaponHardness = 4;
        return weaponHardness;
    }
}
