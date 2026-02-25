package com.equilibrium.mixin.some_special_rules;

import com.equilibrium.entity.mob.FireElementalEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SpawnHelper.Info.class)
public abstract class FireElementalCannotInfluenceSpawnNumberLimit {

    @Shadow
    @Final
    private Object2IntOpenHashMap<SpawnGroup> groupToCount;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void ignoreFireElementalEntityCount(MobEntity entity, Chunk chunk, CallbackInfo ci) {
        boolean monsterCountExist = this.groupToCount.containsKey(entity.getType().getSpawnGroup());
        boolean entityIsFireElementalEntity = entity instanceof FireElementalEntity;
        if (monsterCountExist && entityIsFireElementalEntity) {
            //火元素不消耗刷怪上限
            ci.cancel();
        }
    }
}
