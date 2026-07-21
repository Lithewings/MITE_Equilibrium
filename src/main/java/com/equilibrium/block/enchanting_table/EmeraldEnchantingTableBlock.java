package com.equilibrium.block.enchanting_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmeraldEnchantingTableBlock extends BlockWithEntity {




    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

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
    public static final MapCodec<EmeraldEnchantingTableBlock> CODEC = createCodec(EmeraldEnchantingTableBlock::new);
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream(-2, 0, -2, 2, 1, 2)
            .filter(pos -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2)
            .map(BlockPos::toImmutable)
            .toList();


    public static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
        return world.getBlockState(tablePos.add(providerOffset)).isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                && world.getBlockState(tablePos.add(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2))
                .isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }



    public EmeraldEnchantingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends EmeraldEnchantingTableBlock> getCodec() {
        return createCodec(EmeraldEnchantingTableBlock::new);
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModEnchantingTableBlockEntity(pos, state);

    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? validateTicker(type, ModBlockEntityTypes.ENCHANTING_TABLE_BLOCK_ENTITY_TYPE, ModEnchantingTableBlockEntity::tick):null;
    }


    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }




    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
        }
    }


    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ModEnchantingTableBlockEntity) {
            Text text = ((Nameable)blockEntity).getDisplayName();
            return new SimpleNamedScreenHandlerFactory(
                    (syncId, inventory, player) -> new ModEnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos),12), text
            );
        } else {
            return null;
        }
    }






}
