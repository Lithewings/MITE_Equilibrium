package com.equilibrium.block.portalblock;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

import static net.minecraft.world.level.Level.OVERWORLD;

public class PortalBlock extends Block implements Portal {
    public static final MapCodec<PortalBlock> CODEC = simpleCodec(PortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final int field_31196 = 2;
    protected static final VoxelShape X_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    @Override
    public MapCodec<PortalBlock> codec() {
        return CODEC;
    }

    public PortalBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch ((Direction.Axis)state.getValue(AXIS)) {
            case Z:
                return Z_SHAPE;
            case X:
            default:
                return X_SHAPE;
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.dimensionType().natural() && world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < world.getDifficulty().getId()) {
            while (world.getBlockState(pos).is(this)) {
                pos = pos.below();
            }

            if (world.getBlockState(pos).isValidSpawn(world, pos, EntityType.ZOMBIFIED_PIGLIN)) {
                Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, pos.above(), MobSpawnType.STRUCTURE);
                if (entity != null) {
                    entity.setPortalCooldown();
                }
            }
        }
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = state.getValue(AXIS);
        boolean bl = axis2 != axis && axis.isHorizontal();
        return !bl && !neighborState.is(this) && !new PortalShape(world, pos, axis2).isComplete()
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel world, Entity entity) {
        return entity instanceof Player playerEntity
                ? Math.max(
                1,
                world.getGameRules()
                        .getInt(playerEntity.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY)
        )
                : 0;
    }


    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel world, Entity entity, BlockPos pos) {
        ResourceKey<Level> registryKey = world.dimension();
        ServerLevel serverWorld = world.getServer().getLevel(registryKey);
        if (serverWorld == null) {
            return null;
        } else {
            boolean netherExtension8x = false;
            WorldBorder worldBorder = serverWorld.getWorldBorder();
            double d = DimensionType.getTeleportationScale(world.dimensionType(), serverWorld.dimensionType());
            BlockPos teleportPos = RandomPositionHelper.getRandomPosInSquareRing(pos,world.getRandom(),world.getSeaLevel());

            return this.getOrCreateExitPortalTarget(serverWorld, entity, pos, teleportPos, netherExtension8x, worldBorder);
        }
    }
    @Nullable
    public final DimensionTransition getOrCreateExitPortalTarget(
            ServerLevel world, Entity entity, BlockPos pos, BlockPos teleportPos, boolean inNether, WorldBorder worldBorder
    ) {
        Optional<BlockPos> optional = world.getPortalForcer().findClosestPortalPosition(teleportPos, inNether, worldBorder);
        BlockUtil.FoundRectangle rectangle;
        DimensionTransition.PostDimensionTransition postDimensionTransition;
        if (optional.isPresent()) {
            BlockPos blockPos = (BlockPos) optional.get();
            BlockState blockState = world.getBlockState(blockPos);
            rectangle = BlockUtil.getLargestRectangleAround(
                    blockPos, blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, posx -> world.getBlockState(posx) == blockState
            );
            postDimensionTransition = DimensionTransition.PLAY_PORTAL_SOUND.then(entityx -> entityx.placePortalTicket(blockPos));
        } else {
            Direction.Axis axis = (Direction.Axis) entity.level().getBlockState(pos).getOptionalValue(AXIS).orElse(Direction.Axis.X);


            for (int dx = 0; dx < 4; dx++) {
                for (int dz = 0; dz < 4; dz++) {
                    BlockPos platformPos = teleportPos.offset(dx, -1, dz);
                    world.destroyBlock(platformPos,false);
                    world.setBlockAndUpdate(platformPos,
                            world.dimension() == OVERWORLD?
                                    Blocks.STONE.defaultBlockState():
                                    Blocks.NETHERRACK.defaultBlockState());
                }
            }


            Optional<BlockUtil.FoundRectangle> optional2 = world.getPortalForcer().createPortal(teleportPos, axis);

            if (optional2.isEmpty()) {
                LOGGER.error("Unable to create a portal, likely target out of worldborder");
                return null;
            }

            rectangle = (BlockUtil.FoundRectangle) optional2.get();
            postDimensionTransition = DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET);

            world.destroyBlock(rectangle.minCorner,false);
        }
        return getExitPortalTarget(entity, teleportPos, rectangle, world, postDimensionTransition);

    }


    private static DimensionTransition getExitPortalTarget(
            Entity entity, BlockPos pos, BlockUtil.FoundRectangle exitPortalRectangle, ServerLevel world, DimensionTransition.PostDimensionTransition postDimensionTransition
    ) {
        BlockState blockState = entity.level().getBlockState(pos);
        Direction.Axis axis;
        Vec3 vec3d;
        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            BlockUtil.FoundRectangle rectangle = BlockUtil.getLargestRectangleAround(
                    pos, axis, 21, Direction.Axis.Y, 21, posx -> entity.level().getBlockState(posx) == blockState
            );
            vec3d = entity.getRelativePortalPosition(axis, rectangle);
        } else {
            axis = Direction.Axis.X;
            vec3d = new Vec3(0.5, 0.0, 0.0);
        }

        return getExitPortalTarget(world, exitPortalRectangle, axis, vec3d, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), postDimensionTransition);
    }


    private static DimensionTransition getExitPortalTarget(
            ServerLevel world,
            BlockUtil.FoundRectangle exitPortalRectangle,
            Direction.Axis axis,
            Vec3 positionInPortal,
            Entity entity,
            Vec3 velocity,
            float yaw,
            float pitch,
            DimensionTransition.PostDimensionTransition postDimensionTransition
    ) {
        BlockPos blockPos = exitPortalRectangle.minCorner;
        BlockState blockState = world.getBlockState(blockPos);
        Direction.Axis axis2 = (Direction.Axis)blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double d = (double)exitPortalRectangle.axis1Size;
        double e = (double)exitPortalRectangle.axis2Size;
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        int i = axis == axis2 ? 0 : 90;
        Vec3 vec3d = axis == axis2 ? velocity : new Vec3(velocity.z, velocity.y, -velocity.x);
        double f = (double)entityDimensions.width() / 2.0 + (d - (double)entityDimensions.width()) * positionInPortal.x();
        double g = (e - (double)entityDimensions.height()) * positionInPortal.y();
        double h = 0.5 + positionInPortal.z();
        boolean bl = axis2 == Direction.Axis.X;
        Vec3 vec3d2 = new Vec3((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
        Vec3 vec3d3 =findOpenPosition(vec3d2, world, entity, entityDimensions);
        return new DimensionTransition(world, vec3d3, vec3d, yaw + (float)i, pitch, postDimensionTransition);
    }



    public static Vec3 findOpenPosition(Vec3 fallback, ServerLevel world, Entity entity, EntityDimensions dimensions) {
        if (!(dimensions.width() > 4.0F) && !(dimensions.height() > 4.0F)) {
            double d = (double)dimensions.height() / 2.0;
            Vec3 vec3d = fallback.add(0.0, d, 0.0);
            VoxelShape voxelShape = Shapes.create(AABB.ofSize(vec3d, (double)dimensions.width(), 0.0, (double)dimensions.width()).expandTowards(0.0, 1.0, 0.0).inflate(1.0E-6));
            Optional<Vec3> optional = world.findFreePosition(
                    entity, voxelShape, vec3d, (double)dimensions.width(), (double)dimensions.height(), (double)dimensions.width()
            );
            Optional<Vec3> optional2 = optional.map(pos -> pos.subtract(0.0, d, 0.0));
            return (Vec3)optional2.orElse(fallback);
        } else {
            return fallback;
        }
    }





    @Override
    public Transition getLocalTransition() {
        return Transition.CONFUSION;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            world.playLocalSound(
                    (double)pos.getX() + 0.5,
                    (double)pos.getY() + 0.5,
                    (double)pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        for (int i = 0; i < 4; i++) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = ((double)random.nextFloat() - 0.5) * 0.5;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double j = ((double)random.nextFloat() - 0.5) * 0.5;
            int k = random.nextInt(2) * 2 - 1;
            if (!world.getBlockState(pos.west()).is(this) && !world.getBlockState(pos.east()).is(this)) {
                d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
                g = (double)(random.nextFloat() * 2.0F * (float)k);
            } else {
                f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
                j = (double)(random.nextFloat() * 2.0F * (float)k);
            }

            world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((Direction.Axis)state.getValue(AXIS)) {
                    case Z:
                        return state.setValue(AXIS, Direction.Axis.X);
                    case X:
                        return state.setValue(AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

}
