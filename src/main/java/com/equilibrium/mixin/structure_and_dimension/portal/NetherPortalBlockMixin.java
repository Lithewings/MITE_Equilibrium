package com.equilibrium.mixin.structure_and_dimension.portal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin extends Block implements Portal {

    @Shadow @Nullable protected abstract TeleportTarget getOrCreateExitPortalTarget(ServerWorld world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder);

    @Shadow @Final private static Logger LOGGER;

    public NetherPortalBlockMixin(Settings settings) {
        super(settings);
    }

    // ---------- 延迟加载方法：每次调用时返回维度 Key，不会在类加载时触碰 World ----------
    @Unique
    private static RegistryKey<World> getOverworldKey() {
        return World.OVERWORLD;
    }

    @Unique
    private static RegistryKey<World> getNetherKey() {
        return World.NETHER;
    }

    @Unique
    private static RegistryKey<World> getUnderworldKey() {
        return RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "underworld"));
    }

    @Unique
    private static final String TRANSPORT_TARGET1 = "You will transport to overworld";
    @Unique
    private static final String TRANSPORT_TARGET2 = "You will transport to underworld";
    @Unique
    private static final String TRANSPORT_TARGET3 = "You will transport to nether";

    @Unique
    private static String getTeleportWorld(World world, Entity entity) {
        RegistryKey<World> registryKey = world.getRegistryKey();
        RegistryKey<World> teleport;

        if (registryKey == null) {
            return "Not an illegal transportation";
        } else {
            boolean atBottom = Math.abs(entity.getY() - world.getBottomY()) < 5;
            boolean buffer = Math.abs(entity.getY() - world.getBottomY()) >= 5 && Math.abs(entity.getY() - world.getBottomY()) < 8;

            if (world.getRegistryKey() == getOverworldKey() && atBottom) {
                teleport = getUnderworldKey();
            } else if (world.getRegistryKey() == getOverworldKey() && !atBottom && !buffer) {
                teleport = getOverworldKey();
            } else if (world.getRegistryKey() == getUnderworldKey() && !atBottom && !buffer) {
                teleport = getOverworldKey();
            } else if (world.getRegistryKey() == getUnderworldKey() && atBottom) {
                teleport = getNetherKey();
            } else if (world.getRegistryKey() == getNetherKey()) {
                teleport = getUnderworldKey();
            } else {
                teleport = world.getRegistryKey();
            }

            String worldType;
            if (teleport == getUnderworldKey()) {
                worldType = TRANSPORT_TARGET2;
            } else if (teleport == getOverworldKey()) {
                worldType = TRANSPORT_TARGET1;
            } else if (teleport == getNetherKey()) {
                worldType = TRANSPORT_TARGET3;
            } else {
                worldType = "Not an illegal transportation";
            }
            return worldType;
        }
    }

    @Inject(method = "onEntityCollision", at = @At(value = "HEAD"), cancellable = true)
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        ci.cancel();

        if (entity.canUsePortals(true)) {
            entity.tryUsePortal(this, pos);
            if (entity instanceof PlayerEntity player) {
                String teleport = getTeleportWorld(world, entity);
                if (Objects.equals(teleport, TRANSPORT_TARGET1)) {
                    player.sendMessage(Text.of(Text.translatable("teleport.overworld").formatted(Formatting.YELLOW)), true);
                } else if (Objects.equals(teleport, TRANSPORT_TARGET2)) {
                    player.sendMessage(Text.of(Text.translatable("teleport.underworld").formatted(Formatting.YELLOW)), true);
                } else if (Objects.equals(teleport, TRANSPORT_TARGET3)) {
                    player.sendMessage(Text.of(Text.translatable("teleport.nether").formatted(Formatting.YELLOW)), true);
                } else {
                    player.sendMessage(Text.of("You shouldn't receive this message, you might teleport to an unsupported dimension"), true);
                }
            }
        }
    }

    @Unique
    public TeleportTarget toSpawn(ServerWorld world, Entity entity) {
        RegistryKey<World> registryKey = world.getRegistryKey();
        ServerWorld serverWorld = world.getServer().getWorld(registryKey);
        if (serverWorld == null) {
            return null;
        } else {
            BlockPos blockPos = serverWorld.getSpawnPos();
            float f = entity.getYaw();
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                return serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP);
            }
            Vec3d vec3d = entity.getWorldSpawnPos(serverWorld, blockPos).toBottomCenterPos();
            return new TeleportTarget(
                    serverWorld,
                    vec3d,
                    entity.getVelocity(),
                    f,
                    entity.getPitch(),
                    TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
            );
        }
    }

    @Inject(method = "createTeleportTarget", at = @At(value = "HEAD"), cancellable = true)
    public void createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTarget> cir) {
        cir.cancel();

        RegistryKey<World> registryKey = world.getRegistryKey();
        RegistryKey<World> teleport;

        if (registryKey == null) {
            cir.setReturnValue(null);
            return;
        }

        ServerWorld serverWorld;
        boolean inNether = world.getRegistryKey() == getNetherKey();
        WorldBorder worldBorder = world.getWorldBorder();
        double d = DimensionType.getCoordinateScaleFactor(world.getDimension(), world.getDimension());
        BlockPos blockPos = worldBorder.clamp(entity.getX() * d, entity.getY(), entity.getZ() * d);

        boolean atBottom = Math.abs(entity.getY() - world.getBottomY()) < 5;
        boolean buffer = Math.abs(entity.getY() - world.getBottomY()) >= 5 && Math.abs(entity.getY() - world.getBottomY()) < 8;

        if (world.getRegistryKey() == getOverworldKey() && atBottom) {
            teleport = getUnderworldKey();
        } else if (world.getRegistryKey() == getOverworldKey() && !atBottom && !buffer) {
            teleport = getOverworldKey();
            if (entity instanceof ServerPlayerEntity || (entity.hasPlayerRider() && !entity.getWorld().isClient)) {
                serverWorld = world.getServer().getWorld(teleport);

                StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 255, false, false, false);
                StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, entity.getPos(), 4, blindness, 100);

                StatusEffectInstance slowness = new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 255, false, false, false);
                StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, entity.getPos(), 4, slowness, 100);

                StatusEffectInstance nausea = new StatusEffectInstance(StatusEffects.NAUSEA, 100, 255, false, false, false);
                StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, entity.getPos(), 4, nausea, 100);

                entity.teleportTo(toSpawn(serverWorld,entity));
                return;
            } else {
                teleport = getUnderworldKey();
            }
        } else if (world.getRegistryKey() == getUnderworldKey() && !atBottom && !buffer) {
            teleport = getOverworldKey();
        } else if (world.getRegistryKey() == getUnderworldKey() && atBottom) {
            teleport = getNetherKey();
        } else if (world.getRegistryKey() == getNetherKey()) {
            teleport = getUnderworldKey();
        } else {
            teleport = world.getRegistryKey();
        }

        serverWorld = world.getServer().getWorld(teleport);
        cir.setReturnValue(this.getOrCreateExitPortalTarget(serverWorld, entity, pos, blockPos, inNether, worldBorder));
    }
}