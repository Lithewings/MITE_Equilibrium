package com.equilibrium.block.enchanting_table.emerald;

import com.equilibrium.block.enchanting_table.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EmeraldEnchantingTableBlockEntity extends BlockEntity implements Nameable {
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float flipRandom;
    public float flipTurn;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float bookRotation;
    public float lastBookRotation;
    public float targetBookRotation;
    private static final RandomSource RANDOM = RandomSource.create();
    @Nullable
    private Component customName;

    public EmeraldEnchantingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", Component.Serializer.toJson(this.customName, registryLookup));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        if (nbt.contains("CustomName", Tag.TAG_STRING)) {
            this.customName = parseCustomNameSafe(nbt.getString("CustomName"), registryLookup);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, EmeraldEnchantingTableBlockEntity blockEntity) {
        blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
        blockEntity.lastBookRotation = blockEntity.bookRotation;
        Player playerEntity = world.getNearestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
        if (playerEntity != null) {
            double d = playerEntity.getX() - ((double)pos.getX() + 0.5);
            double e = playerEntity.getZ() - ((double)pos.getZ() + 0.5);
            blockEntity.targetBookRotation = (float)Mth.atan2(e, d);
            blockEntity.nextPageTurningSpeed += 0.1F;
            if (blockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float f = blockEntity.flipRandom;

                do {
                    blockEntity.flipRandom = blockEntity.flipRandom + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (f == blockEntity.flipRandom);
            }
        } else {
            blockEntity.targetBookRotation += 0.02F;
            blockEntity.nextPageTurningSpeed -= 0.1F;
        }

        while (blockEntity.bookRotation >= (float) Math.PI) {
            blockEntity.bookRotation -= (float) (Math.PI * 2);
        }

        while (blockEntity.bookRotation < (float) -Math.PI) {
            blockEntity.bookRotation += (float) (Math.PI * 2);
        }

        while (blockEntity.targetBookRotation >= (float) Math.PI) {
            blockEntity.targetBookRotation -= (float) (Math.PI * 2);
        }

        while (blockEntity.targetBookRotation < (float) -Math.PI) {
            blockEntity.targetBookRotation += (float) (Math.PI * 2);
        }

        float g = blockEntity.targetBookRotation - blockEntity.bookRotation;

        while (g >= (float) Math.PI) {
            g -= (float) (Math.PI * 2);
        }

        while (g < (float) -Math.PI) {
            g += (float) (Math.PI * 2);
        }

        blockEntity.bookRotation += g * 0.4F;
        blockEntity.nextPageTurningSpeed = Mth.clamp(blockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
        blockEntity.ticks++;
        blockEntity.pageAngle = blockEntity.nextPageAngle;
        float h = (blockEntity.flipRandom - blockEntity.nextPageAngle) * 0.4F;
        float i = 0.2F;
        h = Mth.clamp(h, -0.2F, 0.2F);
        blockEntity.flipTurn = blockEntity.flipTurn + (h - blockEntity.flipTurn) * 0.9F;
        blockEntity.nextPageAngle = blockEntity.nextPageAngle + blockEntity.flipTurn;
    }

    @Override
    public Component getName() {
        return (Component)(this.customName != null ? this.customName : Component.translatable("container.enchant"));
    }

    public void setCustomName(@Nullable Component customName) {
        this.customName = customName;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        this.customName = components.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
        super.collectImplicitComponents(componentMapBuilder);
        componentMapBuilder.set(DataComponents.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag nbt) {
        nbt.remove("CustomName");
    }
}
