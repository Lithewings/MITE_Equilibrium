package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.BooleanStorageUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.Path;


import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_BOOLEAN_GAME_RULE_KEYS;


@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity implements Monster {
    @Shadow
    public float prevWingPosition;

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

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
        int day = (int) (this.getWorld().getTimeOfDay() / 24000L);

        boolean stageClear = true;
        // 保存通关凭证
        try {
            if (this.getWorld() instanceof ServerWorld serverWorld) {


                int stageFinish = 0;
                int stageLost = 0;
                for (GameRules.Key<GameRules.BooleanRule> booleanRuleKey : ALL_BOOLEAN_GAME_RULE_KEYS) {
                    if (getGameBooleanRuleFromServer(booleanRuleKey, serverWorld.getServer())) {
                        stageFinish++;
//                        for (PlayerEntity player : serverWorld.getPlayers()) {
//                            player.sendMessage(Text.translatable(booleanRuleKey.getTranslationKey()));
//                            player.sendMessage(Text.of("True"));
//                        }
                    } else {
                        stageLost++;
                        for (PlayerEntity player : serverWorld.getPlayers()) {
                            player.sendMessage(Text.translatable(booleanRuleKey.getTranslationKey()));
                        }
                        stageClear = false;
                    }
                }

                if(stageClear){
                    for (PlayerEntity player : serverWorld.getPlayers()) {
                        player.sendMessage(Text.of("主线完成,现所有世界选项按钮均已解锁(游戏重启生效)"));
                    }
                    BooleanStorageUtil.saveFinishGameOnce(true, FabricLoader.getInstance().getConfigDir().normalize().resolve(BooleanStorageUtil.FINISH_GAME_ONCE).toFile().getPath());
                }else {
                    for (PlayerEntity player : serverWorld.getPlayers()) {
                        player.sendMessage(Text.of("未完成的词条如上"));
                        player.sendMessage(Text.of("共"+(stageFinish+stageLost)+"个基础词条"));
                        player.sendMessage(Text.of("完成情况："+stageFinish+"/"+(stageFinish+stageLost)));
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 保存世界通关信息
        try {
            if (this.getWorld() instanceof ServerWorld serverWorld && stageClear) {

                MinecraftServer server = this.getServer();
                Path path = server.getSavePath(WorldSavePath.ROOT).normalize().resolve(BooleanStorageUtil.WORLD_INFORMATION_RECORDER);
                BooleanStorageUtil.saveWorldInformation(day, serverWorld.getSeed(), path.toFile().getPath());

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
    public void tick() {
        super.tick();
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            this.stateSaverAndLoader = StateSaverAndLoader.getServerState(serverWorld.getServer());
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(Math.min(stateSaverAndLoader.playerDeathTimes, 40));
//            serverWorld.getPlayers().getFirst().sendMessage(Text.of("这条龙的护甲为"+this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).getBaseValue()),true);
        }
    }
}




