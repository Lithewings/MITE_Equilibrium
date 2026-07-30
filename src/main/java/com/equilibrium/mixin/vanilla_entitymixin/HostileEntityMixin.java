package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(Monster.class)
public class HostileEntityMixin extends PathfinderMob implements Enemy {
    protected HostileEntityMixin(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(1);
    }
}
