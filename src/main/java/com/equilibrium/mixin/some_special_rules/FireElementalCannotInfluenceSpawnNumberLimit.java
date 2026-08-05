package com.equilibrium.mixin.some_special_rules;

import com.equilibrium.entity.mob.FireElementalEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(NaturalSpawner.SpawnState.class)
public abstract class FireElementalCannotInfluenceSpawnNumberLimit {

    @Shadow
    @Final
    private Object2IntOpenHashMap<MobCategory> mobCategoryCounts;

    @Inject(method = "afterSpawn", at = @At("HEAD"), cancellable = true)
    private void ignoreFireElementalEntityCount(Mob entity, ChunkAccess chunk, CallbackInfo ci) {
        boolean monsterCountExist = this.mobCategoryCounts.containsKey(entity.getType().getCategory());
        boolean entityIsFireElementalEntity = entity instanceof FireElementalEntity;
        if (monsterCountExist && entityIsFireElementalEntity) {
            //火元素不消耗刷怪上限
            ci.cancel();
        }
    }
}
