package com.equilibrium.block.anvil_block.IronAnvilBlock;


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
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.screen.AnvilScreenHandler;
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
import static net.minecraft.sound.SoundCategory.BLOCKS;

public class IronAnvilBlock extends FallingBlock {
    public static final MapCodec<IronAnvilBlock> CODEC = createCodec(IronAnvilBlock::new);
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


    public static final int IRON_ANVIL_MAX_DURABILITY = 64;
    public static final IntProperty IRON_ANVIL_DURABILITY_PROPERTY = IntProperty.of("iron_anvil_durability",0, IRON_ANVIL_MAX_DURABILITY);
    public static final IntProperty IRON_ANVIL_STAGE = IntProperty.of("iron_anvil_stage",0,2);


    @Override
    public MapCodec<IronAnvilBlock> getCodec() {
        return CODEC;
    }

    public IronAnvilBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(IRON_ANVIL_DURABILITY_PROPERTY, IRON_ANVIL_MAX_DURABILITY)
                .with(IRON_ANVIL_STAGE,0));
    }

    //掉落物品的耐久是什么?
    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        ItemStack drop = super.getDroppedStacks(state, builder).getFirst();
        drop.set(DataComponentTypes.MAX_STACK_SIZE,1);
        drop.set(DataComponentTypes.MAX_DAMAGE,IRON_ANVIL_MAX_DURABILITY);
        drop.set(DataComponentTypes.DAMAGE,IRON_ANVIL_MAX_DURABILITY - state.get(IRON_ANVIL_DURABILITY_PROPERTY));
        return List.of(drop);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClient())
            return;

        int durability = IRON_ANVIL_MAX_DURABILITY - itemStack.getDamage();
        int phase = getPhaseFromDurability(IRON_ANVIL_MAX_DURABILITY,durability);
        if (placer != null) {
            placer.sendMessage(Text.of("铁砧使用状态:"+phase));
            placer.sendMessage(Text.of("铁砧耐久:"+durability));
        }
        //根据物品耐久,将耐久值放入方块状态中
        world.setBlockState(pos, ModBlocksRegistry.IRON_ANVIL.getDefaultState()
                //copy facing
                .with(IronAnvilBlock.FACING, state.get(IronAnvilBlock.FACING))
                //copy damage
                .with(IRON_ANVIL_DURABILITY_PROPERTY, durability)
                .with(IRON_ANVIL_STAGE,phase)
        );

    }
    public static int getPhaseFromDurability(int maxDurability, int durability) {
        // 最大耐久必须大于0，否则无法划分阶段
        // 确保耐久度在 [0, maxDurability] 范围内
        int clamped = Math.max(0, Math.min(durability, maxDurability));

        // 计算两个分割点
        int oneThird = maxDurability / 3;           // 下1/3边界
        int twoThird = maxDurability * 2 / 3;       // 上2/3边界

        if (clamped > twoThird) {
            return 0;   // 高耐久
        } else if (clamped > oneThird) {
            return 1;   // 中耐久
        } else {
            return 2;   // 低耐久
        }
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if(isTableBlocked(world,pos,player)){
            return ActionResult.PASS;
        }

        int i = state.get(IRON_ANVIL_DURABILITY_PROPERTY);


        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.getMainHandStack().isOf(Items.IRON_BLOCK)) {

            //模拟一次修复


//            BlockState newAnvilBlock = null;
//            int count = player.getMainHandStack().getCount();
//            //损坏的第一阶段—>完好无损
//            if (state.getBlock() == Blocks.CHIPPED_ANVIL){
//                newAnvilBlock = Blocks.ANVIL.getDefaultState()
//                        .with(FACING, (Direction) state.get(FACING))
//                        .with(ANVIL_DURABILITY,Math.clamp(i+24,0,64));
//                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
//            }
//            //损坏的第二阶段—>第一阶段
//            else if(state.getBlock() == Blocks.DAMAGED_ANVIL){
//                newAnvilBlock =Blocks.CHIPPED_ANVIL.getDefaultState()
//                        .with(FACING, (Direction) state.get(FACING))
//                        .with(ANVIL_DURABILITY,Math.clamp(i+24,0,64));
//                player.playSound(SoundEvents.BLOCK_ANVIL_USE);
//            }
//            else {
//                //如果是完好无损的铁砧,正常交互
//                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
//                player.incrementStat(Stats.INTERACT_WITH_ANVIL);
//                return ActionResult.PASS;
//            }
//            world.setBlockState(pos, newAnvilBlock);
//            //消耗一个铁块,若代码执行到这里,一定是有损坏的铁砧进行了修复
//            //创造模式测试不消耗铁块
//            if(!player.isCreative())
//                player.getMainHandStack().setCount(count-1);


            //播放声音
            world.playSound(null,pos, SoundEvents.BLOCK_ANVIL_USE,BLOCKS,1f,1f);
            return ActionResult.PASS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_ANVIL);
            return ActionResult.PASS;
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING,IRON_ANVIL_DURABILITY_PROPERTY,IRON_ANVIL_STAGE);
    }



    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }




















    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new IronAnvilScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE
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
