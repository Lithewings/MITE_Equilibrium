package com.equilibrium.server_and_client.server.command;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.server_and_client.server.event.OnCraftingMetalPickAxe;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.BooleanStorageUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

import static com.equilibrium.difficulty_entry.DifficultyEntryDisplay.showAllValuesToServerPlayer;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.*;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.*;
import static com.equilibrium.server_and_client.server.command.ServerCommands.TeleportCommand.execute;
import static com.equilibrium.util.BooleanStorageUtil.loadWorldInformation;
import static net.minecraft.world.World.OVERWORLD;

public class ServerCommands {
    static class TeleportCommand {
        private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
                Text.translatable("commands.teleport.invalidPosition")
        );

        static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
            for (Entity entity : targets) {
                teleport(
                        source,
                        entity,
                        (ServerWorld) destination.getWorld(),
                        destination.getX(),
                        destination.getY(),
                        destination.getZ(),
                        EnumSet.noneOf(PositionFlag.class),
                        destination.getYaw(),
                        destination.getPitch(),
                        null
                );
            }

            if (targets.size() == 1) {
                source.sendFeedback(
                        () -> Text.translatable("commands.teleport.success.entity.single", ((Entity) targets.iterator().next()).getDisplayName(), destination.getDisplayName()),
                        true
                );
            } else {
                source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
            }

            return targets.size();
        }

        private static void teleport(
                ServerCommandSource source,
                Entity target,
                ServerWorld world,
                double x,
                double y,
                double z,
                Set<PositionFlag> movementFlags,
                float yaw,
                float pitch,
                @Nullable LookTarget facingLocation
        ) throws CommandSyntaxException {
            BlockPos blockPos = BlockPos.ofFloored(x, y, z);
            if (!World.isValid(blockPos)) {
                throw INVALID_POSITION_EXCEPTION.create();
            } else {
                float f = MathHelper.wrapDegrees(yaw);
                float g = MathHelper.wrapDegrees(pitch);
                if (target.teleport(world, x, y, z, movementFlags, f, g)) {
                    if (facingLocation != null) {
                        facingLocation.look(source, target);
                    }

                    if (!(target instanceof LivingEntity livingEntity) || !livingEntity.isFallFlying()) {
                        target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
                        target.setOnGround(true);
                    }

                    if (target instanceof PathAwareEntity pathAwareEntity) {
                        pathAwareEntity.getNavigation().stop();
                    }
                }
            }
        }

        static record LookAtEntity(Entity entity,
                                   EntityAnchorArgumentType.EntityAnchor anchor) implements TeleportCommand.LookTarget {
            @Override
            public void look(ServerCommandSource source, Entity entity) {
                if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.lookAtEntity(source.getEntityAnchor(), this.entity, this.anchor);
                } else {
                    entity.lookAt(source.getEntityAnchor(), this.anchor.positionAt(this.entity));
                }
            }
        }

        static record LookAtPosition(Vec3d position) implements LookTarget {
            @Override
            public void look(ServerCommandSource source, Entity entity) {
                entity.lookAt(source.getEntityAnchor(), this.position);
            }
        }

        @FunctionalInterface
        interface LookTarget {
            void look(ServerCommandSource source, Entity entity);
        }
    }


    // 注册命令的标准方式，适配 CommandDispatcher 的签名
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("sun")
                        .requires(source -> {
                            return getGameBooleanRuleFromServer(ENABLE_MORE_RAIN_WEATHER,source.getServer());
                        })
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayerEntity){
                                                if(serverPlayerEntity.totalExperience>=500) {
                                                    context.getSource().getServer().getOverworld().setWeather(6400, 0, false, false);
                                                    serverPlayerEntity.addExperience(-500);
                                                }
                                                else
                                                    serverPlayerEntity.sendMessage(Text.of("经验不足500xp"));
                                            }
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"sun\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );











        dispatcher.register(
                CommandManager.literal("clearHunger")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayerEntity){
                                                HungerManager hungerManager = serverPlayerEntity.getHungerManager();
                                                hungerManager.setFoodLevel(0);
                                                hungerManager.setSaturationLevel(0);
                                            }
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"clearHunger\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );
        dispatcher.register(
                CommandManager.literal("checkDifficultyEntry")
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayerEntity)
                                                showAllValuesToServerPlayer(serverPlayerEntity,true);
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"checkDifficultyEntry\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );

        dispatcher.register(
                CommandManager.literal("checkExtraDifficultyEntry")
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayerEntity serverPlayerEntity)
                                                showAllValuesToServerPlayer(serverPlayerEntity,false);
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"checkExtraDifficultyEntry\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );

        dispatcher.register(
                CommandManager.literal("village")
                        //在开启了关闭村庄的进阶词条时,本命令不生效
                        .requires(source -> !getGameBooleanRuleFromServer(DISABLE_VILLAGE_AND_PILLAGE,source.getServer()))
                        .executes
                                (OnCraftingMetalPickAxe::isPickAxeCrafted)

        );
        dispatcher.register(
                CommandManager.literal("deathTime")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(context.getSource().getServer());
                            if (player != null)
                                player.sendMessage(Text.of("你的总死亡次数为: " + stateSaverAndLoader.playerDeathTimes));
                            return 1;
                        })
        );
        dispatcher.register(
                CommandManager.literal("showxp")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            player.sendMessage(Text.of("You have "+player.totalExperience+" xp"));
                            return 1;
                        })
        );


        dispatcher.register(
                CommandManager.literal("checkAdvancement")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            long originalSeed = context.getSource().getServer().getWorld(OVERWORLD).getSeed();
                            Path path = context.getSource().getServer().getSavePath(WorldSavePath.ROOT)
                                    .normalize().resolve("WorldInformationRecorder.dat");

                            BooleanStorageUtil.WorldInformationRecorder worldInformationRecorder = loadWorldInformation(path.toString());

                            if (worldInformationRecorder != null) {
                                int day = worldInformationRecorder.getFinishDay();
                                long seed = worldInformationRecorder.getSeed();
                                boolean isGrandStageClear = worldInformationRecorder.getIsGrandStageClear();
                                String version = worldInformationRecorder.getVersion();

                                if (day >= 0 && seed == originalSeed) {
                                    Text clearStatus = Text.translatable(isGrandStageClear
                                            ? "miteequilibrium.message.boolean.true"
                                            : "miteequilibrium.message.boolean.false");

                                    player.sendMessage(Text.translatable("miteequilibrium.message.days_clear", day));
                                    player.sendMessage(Text.translatable("miteequilibrium.message.world_seed", seed));
                                    player.sendMessage(Text.translatable("miteequilibrium.message.grand_stage_clear", clearStatus));
                                    player.sendMessage(Text.translatable("miteequilibrium.message.version_info", version));

                                } else if (day >= 0 && seed != originalSeed) {
                                    player.sendMessage(Text.translatable("miteequilibrium.message.invalid_clear_seed_mismatch"));
                                } else {
                                    player.sendMessage(Text.translatable("miteequilibrium.message.invalid_clear_info"));
                                }
                            } else {
                                player.sendMessage(Text.translatable("miteequilibrium.message.no_clear_info"));
                            }
                            return 1;
                        })
        );

        dispatcher.register(
                CommandManager.literal("teleportToPlayer")
                        .then(
                                CommandManager.argument("destination", EntityArgumentType.player())
                                        .requires(source -> source.getEntity() instanceof PlayerEntity) // 确保执行者是玩家
                                        .executes(
                                                context -> {
                                                    Entity entity = context.getSource().getEntity();

                                                    if (getGameBooleanRuleFromServer(DISABLE_PLAYER_TELEPORT, context.getSource().getServer())) {
                                                        if (entity instanceof PlayerEntity player)
                                                            player.sendMessage(Text.of("玩家间的传送功能已被禁用"));
                                                        return 0;
                                                    }
                                                    if (entity instanceof PlayerEntity player && player.totalExperience >= 500) {
                                                        player.addExperience(-500);
                                                        return execute(
                                                                context.getSource(),
                                                                Collections.singleton(context.getSource().getEntityOrThrow()), // 执行者
                                                                EntityArgumentType.getPlayer(context, "destination") // 目标为玩家
                                                        );
                                                    } else if (entity instanceof PlayerEntity player && player.totalExperience < 500) {
                                                        player.sendMessage(Text.of("至少需要500经验值进行玩家传送"));
                                                    }
                                                    return 0;
                                                }
                                        )
                        )
        );

    }

}
