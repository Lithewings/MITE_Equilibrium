package com.equilibrium.block.anvil_block.adamantium_anvil_block;


import com.equilibrium.block.ModBlocksRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.equilibrium.block.UseBlockActionUtil.isTableBlocked;
import static com.equilibrium.block.anvil_block.util.getPhaseFromDurability;
import static net.minecraft.sound.SoundCategory.BLOCKS;

public class AdamantiumAnvilBlock extends FallingBlock {
    public static final MapCodec<AdamantiumAnvilBlock> CODEC = createCodec(AdamantiumAnvilBlock::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    private static final VoxelShape X_STEP_SHAPE = Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
    private static final VoxelShape X_STEM_SHAPE = Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
    private static final VoxelShape X_FACE_SHAPE = Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape Z_STEP_SHAPE = Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
    private static final VoxelShape Z_STEM_SHAPE = Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
    private static final VoxelShape Z_FACE_SHAPE = Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
    private static final VoxelShape X_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, X_STEP_SHAPE, X_STEM_SHAPE, X_FACE_SHAPE);
    private static final VoxelShape Z_AXIS_SHAPE = VoxelShapes.union(BASE_SHAPE, Z_STEP_SHAPE, Z_STEM_SHAPE, Z_FACE_SHAPE);
    private static final Text TITLE = Text.translatable("container.repair");
    private static final float FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER = 2.0F;
    private static final int FALLING_BLOCK_ENTITY_MAX_DAMAGE = 40;


    public static final int ADAMANTIUM_ANVIL_MAX_DURABILITY = 1;
    public static final IntProperty ADAMANTIUM_ANVIL_DURABILITY_PROPERTY = IntProperty.of("adamantium_anvil_durability",0, ADAMANTIUM_ANVIL_MAX_DURABILITY);
    public static final IntProperty ADAMANTIUM_ANVIL_STAGE = IntProperty.of("adamantium_anvil_stage",0,2);


    @Override
    public MapCodec<AdamantiumAnvilBlock> getCodec() {
        return CODEC;
    }

    public AdamantiumAnvilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ADAMANTIUM_ANVIL_DURABILITY_PROPERTY, ADAMANTIUM_ANVIL_MAX_DURABILITY)
                .with(ADAMANTIUM_ANVIL_STAGE,0));
    }

    //掉落物品的耐久是什么?
    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        ItemStack drop = super.getDroppedStacks(state, builder).getFirst();
        drop.set(DataComponentTypes.MAX_STACK_SIZE,1);
//        drop.set(DataComponentTypes.MAX_DAMAGE, ADAMANTIUM_ANVIL_MAX_DURABILITY);
        drop.set(DataComponentTypes.DAMAGE, ADAMANTIUM_ANVIL_MAX_DURABILITY - state.get(ADAMANTIUM_ANVIL_DURABILITY_PROPERTY));
        return List.of(drop);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClient())
            return;

        int durability = ADAMANTIUM_ANVIL_MAX_DURABILITY - itemStack.getDamage();
        int phase = getPhaseFromDurability(ADAMANTIUM_ANVIL_MAX_DURABILITY,durability);
//        if (placer != null) {
//            placer.sendMessage(Text.of("砧使用状态:"+phase));
//            placer.sendMessage(Text.of("砧耐久:"+durability));
//        }
        //根据物品耐久,将耐久值放入方块状态中
        world.setBlockState(pos, ModBlocksRegistry.ADAMANTIUM_ANVIL.getDefaultState()
                //copy facing
                .with(AdamantiumAnvilBlock.FACING, state.get(AdamantiumAnvilBlock.FACING))
                //copy damage
                .with(ADAMANTIUM_ANVIL_DURABILITY_PROPERTY, durability)
                .with(ADAMANTIUM_ANVIL_STAGE,phase)
        );

    }




    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if(isTableBlocked(world,pos,player)){
            return ActionResult.PASS;
        }



        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.getMainHandStack().isOf(ModBlocksRegistry.ADAMANTIUM_BLOCK.asItem())) {


            //修复
            int durability = state.get(ADAMANTIUM_ANVIL_DURABILITY_PROPERTY);

            int beforePhase = getPhaseFromDurability(ADAMANTIUM_ANVIL_MAX_DURABILITY,durability);

            if (beforePhase==0)
                return ActionResult.PASS;

            //增加耐久
            int fixed = Math.clamp((int)(durability +0.33f*(float) ADAMANTIUM_ANVIL_MAX_DURABILITY), 0, ADAMANTIUM_ANVIL_MAX_DURABILITY);
            int afterPhase = getPhaseFromDurability(ADAMANTIUM_ANVIL_MAX_DURABILITY,fixed);


            world.setBlockState(pos, ModBlocksRegistry.ADAMANTIUM_ANVIL.getDefaultState()
                    //copy facing
                    .with(AdamantiumAnvilBlock.FACING, state.get(AdamantiumAnvilBlock.FACING))
                    //copy damage
                    .with(ADAMANTIUM_ANVIL_DURABILITY_PROPERTY,fixed)
                    .with(ADAMANTIUM_ANVIL_STAGE,afterPhase)
            );


//            player.sendMessage(Text.of("铁砧使用状态:"+afterPhase));
//            player.sendMessage(Text.of("铁砧耐久:"+fixed));

            //消耗一个铁块,若代码执行到这里,一定是有损坏的铁砧进行了修复
            //创造模式测试不消耗铁块
            if(!player.isCreative())
                player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);



            //播放声音
            world.playSound(null,pos, SoundEvents.BLOCK_ANVIL_USE,BLOCKS,1f,1f);
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_ANVIL);
            return ActionResult.PASS;
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ADAMANTIUM_ANVIL_DURABILITY_PROPERTY, ADAMANTIUM_ANVIL_STAGE);
    }



    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }




















    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new AdamantiumScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE
        );
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setHurtEntities(2.0F, 40);
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(WorldEvents.ANVIL_LANDS, pos, 0);
        }
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
        }
    }

    @Override
    public DamageSource getDamageSource(Entity attacker) {
        return attacker.getDamageSources().fallingAnvil(attacker);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return state.getMapColor(world, pos).color;
    }
}
