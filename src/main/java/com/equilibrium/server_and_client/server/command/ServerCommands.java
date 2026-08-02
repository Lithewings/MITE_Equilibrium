package com.equilibrium.server_and_client.server.command;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.server_and_client.server.event.OnCraftingMetalPickAxe;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.util.BooleanStorageUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.equilibrium.difficulty_entry.DifficultyEntryDisplay.showAllValuesToServerPlayer;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.*;
import static com.equilibrium.server_and_client.server.command.ServerCommands.TeleportCommand.execute;
import static com.equilibrium.util.BooleanStorageUtil.loadWorldInformation;
import static net.minecraft.world.level.Level.OVERWORLD;

public class ServerCommands {
    static class TeleportCommand {
        private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
                Component.translatable("commands.teleport.invalidPosition")
        );

        static int execute(CommandSourceStack source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
            for (Entity entity : targets) {
                teleport(
                        source,
                        entity,
                        (ServerLevel) destination.level(),
                        destination.getX(),
                        destination.getY(),
                        destination.getZ(),
                        EnumSet.noneOf(RelativeMovement.class),
                        destination.getYRot(),
                        destination.getXRot(),
                        null
                );
            }

            if (targets.size() == 1) {
                source.sendSuccess(
                        () -> Component.translatable("commands.teleport.success.entity.single", ((Entity) targets.iterator().next()).getDisplayName(), destination.getDisplayName()),
                        true
                );
            } else {
                source.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
            }

            return targets.size();
        }

        private static void teleport(
                CommandSourceStack source,
                Entity target,
                ServerLevel world,
                double x,
                double y,
                double z,
                Set<RelativeMovement> movementFlags,
                float yaw,
                float pitch,
                @Nullable LookTarget facingLocation
        ) throws CommandSyntaxException {
            BlockPos blockPos = BlockPos.containing(x, y, z);
            if (!Level.isInSpawnableBounds(blockPos)) {
                throw INVALID_POSITION_EXCEPTION.create();
            } else {
                float f = Mth.wrapDegrees(yaw);
                float g = Mth.wrapDegrees(pitch);
                if (target.teleportTo(world, x, y, z, movementFlags, f, g)) {
                    if (facingLocation != null) {
                        facingLocation.look(source, target);
                    }

                    if (!(target instanceof LivingEntity livingEntity) || !livingEntity.isFallFlying()) {
                        target.setDeltaMovement(target.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                        target.setOnGround(true);
                    }

                    if (target instanceof PathfinderMob pathAwareEntity) {
                        pathAwareEntity.getNavigation().stop();
                    }
                }
            }
        }

        static record LookAtEntity(Entity entity,
                                   EntityAnchorArgument.Anchor anchor) implements LookTarget {
            @Override
            public void look(CommandSourceStack source, Entity entity) {
                if (entity instanceof ServerPlayer serverPlayerEntity) {
                    serverPlayerEntity.lookAt(source.getAnchor(), this.entity, this.anchor);
                } else {
                    entity.lookAt(source.getAnchor(), this.anchor.apply(this.entity));
                }
            }
        }

        static record LookAtPosition(Vec3 position) implements LookTarget {
            @Override
            public void look(CommandSourceStack source, Entity entity) {
                entity.lookAt(source.getAnchor(), this.position);
            }
        }

        @FunctionalInterface
        interface LookTarget {
            void look(CommandSourceStack source, Entity entity);
        }
    }


    // 注册命令的标准方式，适配 CommandDispatcher 的签名
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(
                Commands.literal("sun")
                        .requires(source -> {
                            return getGameBooleanRuleFromServer(ENABLE_MORE_RAIN_WEATHER,source.getServer());
                        })
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayer serverPlayerEntity){
                                                if(serverPlayerEntity.totalExperience>=500) {
                                                    context.getSource().getServer().overworld().setWeatherParameters(6400, 0, false, false);
                                                    serverPlayerEntity.giveExperiencePoints(-500);
                                                }
                                                else
                                                    serverPlayerEntity.sendSystemMessage(Component.nullToEmpty("经验不足500xp"));
                                            }
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"sun\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );











        dispatcher.register(
                Commands.literal("clearHunger")
                        .requires(source -> source.hasPermission(2))
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayer serverPlayerEntity){
                                                FoodData hungerManager = serverPlayerEntity.getFoodData();
                                                hungerManager.setFoodLevel(0);
                                                hungerManager.setSaturation(0);
                                            }
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"clearHunger\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );
        dispatcher.register(
                Commands.literal("checkDifficultyEntry")
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayer serverPlayerEntity)
                                                showAllValuesToServerPlayer(serverPlayerEntity,true);
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"checkDifficultyEntry\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );

        dispatcher.register(
                Commands.literal("checkExtraDifficultyEntry")
                        .executes
                                ((context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayer serverPlayerEntity)
                                                showAllValuesToServerPlayer(serverPlayerEntity,false);
                                            else
                                                OnServerInitialize.LOGGER.error("This command \"checkExtraDifficultyEntry\" can only be used by server player");
                                            return 1;
                                        })

                                )
        );

        dispatcher.register(
                Commands.literal("village")
                        //在开启了关闭村庄的进阶词条时,本命令不生效
                        .requires(source -> !getGameBooleanRuleFromServer(DISABLE_VILLAGE_AND_PILLAGE,source.getServer()))
                        .executes
                                (OnCraftingMetalPickAxe::isPickAxeCrafted)

        );
        dispatcher.register(
                Commands.literal("deathTime")
                        .executes(context -> {
                            Player player = context.getSource().getPlayer();
                            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(context.getSource().getServer());
                            if (player != null)
                                player.sendSystemMessage(Component.nullToEmpty("你的总死亡次数为: " + stateSaverAndLoader.playerDeathTimes));
                            return 1;
                        })
        );
        dispatcher.register(
                Commands.literal("showxp")
                        .executes(context -> {
                            Player player = context.getSource().getPlayer();
                            player.sendSystemMessage(Component.nullToEmpty("You have "+player.totalExperience+" xp"));
                            return 1;
                        })
        );


        dispatcher.register(
                Commands.literal("checkAdvancement")
                        .executes(context -> {
                            Player player = context.getSource().getPlayer();
                            long originalSeed = context.getSource().getServer().getLevel(OVERWORLD).getSeed();
                            Path path = context.getSource().getServer().getWorldPath(LevelResource.ROOT)
                                    .normalize().resolve("WorldInformationRecorder.dat");

                            BooleanStorageUtil.WorldInformationRecorder worldInformationRecorder = loadWorldInformation(path.toString());

                            if (worldInformationRecorder != null) {
                                int day = worldInformationRecorder.getFinishDay();
                                long seed = worldInformationRecorder.getSeed();
                                boolean isGrandStageClear = worldInformationRecorder.getIsGrandStageClear();
                                String version = worldInformationRecorder.getVersion();

                                if (day >= 0 && seed == originalSeed) {
                                    Component clearStatus = Component.translatable(isGrandStageClear
                                            ? "miteequilibrium.message.boolean.true"
                                            : "miteequilibrium.message.boolean.false");

                                    player.sendSystemMessage(Component.translatable("miteequilibrium.message.days_clear", day));
                                    player.sendSystemMessage(Component.translatable("miteequilibrium.message.world_seed", seed));
                                    player.sendSystemMessage(Component.translatable("miteequilibrium.message.grand_stage_clear", clearStatus));
                                    player.sendSystemMessage(Component.translatable("miteequilibrium.message.version_info", version));

                                } else if (day >= 0 && seed != originalSeed) {
                                    player.sendSystemMessage(Component.translatable("miteequilibrium.message.invalid_clear_seed_mismatch"));
                                } else {
                                    player.sendSystemMessage(Component.translatable("miteequilibrium.message.invalid_clear_info"));
                                }
                            } else {
                                player.sendSystemMessage(Component.translatable("miteequilibrium.message.no_clear_info"));
                            }
                            return 1;
                        })
        );

        dispatcher.register(
                Commands.literal("teleportToPlayer")
                        .then(
                                Commands.argument("destination", EntityArgument.player())
                                        .requires(source -> source.getEntity() instanceof Player) // 确保执行者是玩家
                                        .executes(
                                                context -> {
                                                    Entity entity = context.getSource().getEntity();

                                                    if (getGameBooleanRuleFromServer(DISABLE_PLAYER_TELEPORT, context.getSource().getServer())) {
                                                        if (entity instanceof Player player)
                                                            player.sendSystemMessage(Component.nullToEmpty("玩家间的传送功能已被禁用"));
                                                        return 0;
                                                    }
                                                    if (entity instanceof Player player && player.totalExperience >= 500) {
                                                        player.giveExperiencePoints(-500);
                                                        return execute(
                                                                context.getSource(),
                                                                Collections.singleton(context.getSource().getEntityOrException()), // 执行者
                                                                EntityArgument.getPlayer(context, "destination") // 目标为玩家
                                                        );
                                                    } else if (entity instanceof Player player && player.totalExperience < 500) {
                                                        player.sendSystemMessage(Component.nullToEmpty("至少需要500经验值进行玩家传送"));
                                                    }
                                                    return 0;
                                                }
                                        )
                        )
        );

    }

}
