package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.BooleanStorageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.Path;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_BASIC_ENTRY_KEYS;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_EXTRA_ENTRY_KEYS;

@Mixin(EnderDragon.class)
public abstract class EnderDragonEntityMixin extends Mob implements Enemy {

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    @Shadow
    @Final
    private EnderDragonPart body;

    protected EnderDragonEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }


    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        int day = (int) (this.level().getDayTime() / 24000L);

        boolean stageClear = true;
        boolean grandStageClear = true;

        try {
            if (this.level() instanceof ServerLevel serverWorld) {
                int stageFinish = 0;
                int stageLost = 0;

                for (GameRules.Key<GameRules.BooleanValue> booleanRuleKey : ALL_BASIC_ENTRY_KEYS) {
                    if (getGameBooleanRuleFromServer(booleanRuleKey, serverWorld.getServer())) {
                        stageFinish++;
                    } else {
                        stageLost++;
                        for (Player player : serverWorld.players()) {
                            player.sendSystemMessage(Component.translatable(booleanRuleKey.getDescriptionId()));
                        }
                        stageClear = false;
                    }
                }

                for (GameRules.Key<GameRules.BooleanValue> booleanRuleKey : ALL_EXTRA_ENTRY_KEYS) {
                    if (!getGameBooleanRuleFromServer(booleanRuleKey, serverWorld.getServer())) {
                        grandStageClear = false;
                    }
                }

                if (stageClear) {
                    for (Player player : serverWorld.players()) {
                        player.sendSystemMessage(Component.literal("主线完成,现所有世界选项按钮均已解锁(游戏重启生效)"));
                        player.sendSystemMessage(Component.literal("Grand Stage Clear ? " + grandStageClear));
                    }

                    Path configPath = FMLPaths.CONFIGDIR.get().resolve(BooleanStorageUtil.FINISH_GAME_ONCE);
                    BooleanStorageUtil.saveFinishGameOnce(true, configPath.toString());
                } else {
                    for (Player player : serverWorld.players()) {
                        player.sendSystemMessage(Component.literal("未完成的词条如上"));
                        player.sendSystemMessage(Component.literal("共" + (stageFinish + stageLost) + "个基础词条"));
                        player.sendSystemMessage(Component.literal("完成情况：" + stageFinish + "/" + (stageFinish + stageLost)));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            if (this.level() instanceof ServerLevel serverWorld && stageClear) {
                MinecraftServer server = this.getServer();
                Path path = server.getWorldPath(LevelResource.ROOT)
                        .normalize()
                        .resolve(BooleanStorageUtil.WORLD_INFORMATION_RECORDER);
                BooleanStorageUtil.saveWorldInformation(day, serverWorld.getSeed(), grandStageClear, path.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private StateSaverAndLoader stateSaverAndLoader;

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverWorld) {
            this.stateSaverAndLoader = StateSaverAndLoader.getServerState(serverWorld.getServer());
            this.getAttribute(Attributes.ARMOR).setBaseValue(Math.min(stateSaverAndLoader.playerDeathTimes, 40));
        }
    }
}