package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.BooleanStorageUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.Path;


@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin  extends MobEntity implements Monster {
    @Shadow
    public float prevWingPosition;

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    //玩家每死亡一次,末影龙获得一点基础护甲





    /**
     * @param damageSource
     */
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        String fileName = "Finish The Game Once.dat";

        Path configPath = FabricLoader.getInstance().getConfigDir().normalize().resolve(fileName);

        // 保存到自定义路径
        try {
            BooleanStorageUtil.save(true, configPath.toFile().getPath());

            if(this.getWorld() instanceof ServerWorld serverWorld){
                for( PlayerEntity player : serverWorld.getPlayers()){
                    player.sendMessage(Text.of("主线完成,现所有世界选项按钮均已解锁(游戏重启生效)"));
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        // 从自定义路径读取
//        boolean value = BooleanStorageUtil.load(configPath.toString(), false);
    }

    @Unique
    StateSaverAndLoader stateSaverAndLoader;


    @Override
    public void tick(){
        super.tick();
        if(this.getWorld() instanceof ServerWorld serverWorld){
            this.stateSaverAndLoader = StateSaverAndLoader.getServerState(serverWorld.getServer());
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(Math.min(stateSaverAndLoader.playerDeathTimes,40));
//            serverWorld.getPlayers().getFirst().sendMessage(Text.of("这条龙的护甲为"+this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).getBaseValue()),true);
        }
    }
}




