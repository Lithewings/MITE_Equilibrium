package com.equilibrium.block.anvil.iron_anvil_block;



import com.equilibrium.block.anvil.AnvilBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.equilibrium.block.UseBlockActionUtil.isTableBlocked;
import static com.equilibrium.util.AnvilPhase.getPhaseFromDurability;
import static net.minecraft.sounds.SoundSource.BLOCKS;

public class IronAnvilBlock extends FallingBlock {
    public static final MapCodec<IronAnvilBlock> CODEC = simpleCodec(IronAnvilBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    private static final VoxelShape X_STEP_SHAPE = Block.box(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
    private static final VoxelShape X_STEM_SHAPE = Block.box(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
    private static final VoxelShape X_FACE_SHAPE = Block.box(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape Z_STEP_SHAPE = Block.box(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
    private static final VoxelShape Z_STEM_SHAPE = Block.box(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
    private static final VoxelShape Z_FACE_SHAPE = Block.box(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
    private static final VoxelShape X_AXIS_SHAPE = Shapes.or(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE);
    private static final VoxelShape Z_AXIS_SHAPE = Shapes.or(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE);
    private static final Component TITLE = Component.translatable("container.repair");
    private static final float FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER = 2.0F;
    private static final int FALLING_BLOCK_ENTITY_MAX_DAMAGE = 40;


    public static final int IRON_ANVIL_MAX_DURABILITY = 64;
    public static final IntegerProperty IRON_ANVIL_DURABILITY_PROPERTY = IntegerProperty.create("iron_anvil_durability",0, IRON_ANVIL_MAX_DURABILITY);
    public static final IntegerProperty IRON_ANVIL_STAGE = IntegerProperty.create("iron_anvil_stage",0,2);


    @Override
    public MapCodec<IronAnvilBlock> codec() {
        return CODEC;
    }

    public IronAnvilBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(IRON_ANVIL_DURABILITY_PROPERTY, IRON_ANVIL_MAX_DURABILITY)
                .setValue(IRON_ANVIL_STAGE,0));
    }

    //掉落物品的耐久是什么?
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack drop = super.getDrops(state, builder).getFirst();
        drop.set(DataComponents.MAX_STACK_SIZE,1);
        drop.set(DataComponents.MAX_DAMAGE,IRON_ANVIL_MAX_DURABILITY);
        drop.set(DataComponents.DAMAGE,IRON_ANVIL_MAX_DURABILITY - state.getValue(IRON_ANVIL_DURABILITY_PROPERTY));
        return List.of(drop);
    }


    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClientSide())
            return;

        int durability = IRON_ANVIL_MAX_DURABILITY - itemStack.getDamageValue();
        int phase = getPhaseFromDurability(IRON_ANVIL_MAX_DURABILITY,durability);
        if (placer != null) {
            placer.sendSystemMessage(Component.nullToEmpty("铁砧使用状态:"+phase));
            placer.sendSystemMessage(Component.nullToEmpty("铁砧耐久:"+durability));
        }
        //根据物品耐久,将耐久值放入方块状态中
        world.setBlockAndUpdate(pos, AnvilBlocks.IRON_ANVIL.get().defaultBlockState()
                //copy facing
                .setValue(IronAnvilBlock.FACING, state.getValue(IronAnvilBlock.FACING))
                //copy damage
                .setValue(IRON_ANVIL_DURABILITY_PROPERTY, durability)
                .setValue(IRON_ANVIL_STAGE,phase)
        );

    }



    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {

        if(isTableBlocked(world,pos,player)){
            return InteractionResult.PASS;
        }



        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player.getMainHandItem().is(Items.IRON_BLOCK)) {


            //修复
            int durability = state.getValue(IRON_ANVIL_DURABILITY_PROPERTY);

            int beforePhase = getPhaseFromDurability(IRON_ANVIL_MAX_DURABILITY,durability);

            if (beforePhase==0)
                return InteractionResult.PASS;

            //增加耐久
            int fixed = Math.clamp((int)(durability +0.33f*(float) IRON_ANVIL_MAX_DURABILITY), 0,IRON_ANVIL_MAX_DURABILITY);
            int afterPhase = getPhaseFromDurability(IRON_ANVIL_MAX_DURABILITY,fixed);


            world.setBlockAndUpdate(pos, AnvilBlocks.IRON_ANVIL.get().defaultBlockState()
                    //copy facing
                    .setValue(IronAnvilBlock.FACING, state.getValue(IronAnvilBlock.FACING))
                    //copy damage
                    .setValue(IRON_ANVIL_DURABILITY_PROPERTY,fixed)
                    .setValue(IRON_ANVIL_STAGE,afterPhase)
            );


            player.sendSystemMessage(Component.nullToEmpty("铁砧使用状态:"+afterPhase));
            player.sendSystemMessage(Component.nullToEmpty("铁砧耐久:"+fixed));

            //消耗一个铁块,若代码执行到这里,一定是有损坏的铁砧进行了修复
            //创造模式测试不消耗铁块
            if(!player.isCreative())
                player.getMainHandItem().setCount(player.getMainHandItem().getCount()-1);



            //播放声音
            world.playSound(null,pos, SoundEvents.ANVIL_USE,BLOCKS,1f,1f);
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(world, pos));
            player.awardStat(Stats.INTERACT_WITH_ANVIL);
            return InteractionResult.PASS;
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING,IRON_ANVIL_DURABILITY_PROPERTY,IRON_ANVIL_STAGE);
    }



    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getClockWise());
    }




















    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider(
                (syncId, inventory, player) -> new IronAnvilScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos)), TITLE
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        entity.setHurtsEntities(2.0F, 40);
    }

    @Override
    public void onLand(Level world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.levelEvent(LevelEvent.SOUND_ANVIL_LAND, pos, 0);
        }
    }

    @Override
    public void onBrokenAfterFall(Level world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
        }
    }

    @Override
    public DamageSource getFallDamageSource(Entity attacker) {
        return attacker.damageSources().anvil(attacker);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getMapColor(world, pos).col;
    }
}
