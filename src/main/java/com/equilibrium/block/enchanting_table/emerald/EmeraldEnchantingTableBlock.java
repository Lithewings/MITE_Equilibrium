package com.equilibrium.block.enchanting_table.emerald;

import com.equilibrium.block.enchanting_table.ModBlockEntityTypes;
import com.equilibrium.block.enchanting_table.ModEnchantmentScreenHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmeraldEnchantingTableBlock extends BaseEntityBlock {




    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        for (BlockPos blockPos : POWER_PROVIDER_OFFSETS) {
            if (random.nextInt(16) == 0 && canAccessPowerProvider(world, pos, blockPos)) {
                world.addParticle(
                        ParticleTypes.ENCHANT,
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 2.0,
                        (double)pos.getZ() + 0.5,
                        (double)((float)blockPos.getX() + random.nextFloat()) - 0.5,
                        (double)((float)blockPos.getY() - random.nextFloat() - 1.0F),
                        (double)((float)blockPos.getZ() + random.nextFloat()) - 0.5
                );
            }
        }
    }
    public static final MapCodec<EmeraldEnchantingTableBlock> CODEC = simpleCodec(EmeraldEnchantingTableBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.betweenClosedStream(-2, 0, -2, 2, 1, 2)
            .filter(pos -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2)
            .map(BlockPos::immutable)
            .toList();


    public static boolean canAccessPowerProvider(Level world, BlockPos tablePos, BlockPos providerOffset) {
        return world.getBlockState(tablePos.offset(providerOffset)).is(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                && world.getBlockState(tablePos.offset(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2))
                .is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }



    public EmeraldEnchantingTableBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends EmeraldEnchantingTableBlock> codec() {
        return simpleCodec(EmeraldEnchantingTableBlock::new);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EmeraldEnchantingTableBlockEntity(pos, state);

    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide ? createTickerHelper(type, ModBlockEntityTypes.EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE, EmeraldEnchantingTableBlockEntity::tick):null;
    }


    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }




    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(world, pos));
            return InteractionResult.CONSUME;
        }
    }


    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EmeraldEnchantingTableBlockEntity) {
            Component text = ((Nameable)blockEntity).getDisplayName();
            return new SimpleMenuProvider(
                    (syncId, inventory, player) -> new ModEnchantmentScreenHandler(syncId, inventory, ContainerLevelAccess.create(world, pos),12), text
            );
        } else {
            return null;
        }
    }






}
