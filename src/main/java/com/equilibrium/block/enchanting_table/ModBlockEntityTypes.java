package com.equilibrium.block.enchanting_table;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.enchanting_table.diamond.DiamondEnchantingTableBlockEntity;
import com.equilibrium.block.enchanting_table.emerald.EmeraldEnchantingTableBlockEntity;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = OnServerInitialize.MOD_ID)
public class ModBlockEntityTypes {

    // 只声明字段，不初始化
    public static BlockEntityType<EmeraldEnchantingTableBlockEntity> EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<DiamondEnchantingTableBlockEntity> DIAMOND_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE;

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), helper -> {
            EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE = Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "emerald_enchanting_table"),
                    BlockEntityType.Builder.of(
                            EmeraldEnchantingTableBlockEntity::new,
                            ModBlocksRegistry.EMERALD_ENCHANTING_TABLE  // 获取已注册的方块
                    ).build(
                            DataFixers.getDataFixer()
                                    .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))
                                    .getType(References.BLOCK_ENTITY)
                    )
            );

            DIAMOND_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE = Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "diamond_enchanting_table"),
                    BlockEntityType.Builder.of(
                            DiamondEnchantingTableBlockEntity::new,
                            ModBlocksRegistry.DIAMOND_ENCHANTING_TABLE
                    ).build(
                            DataFixers.getDataFixer()
                                    .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))
                                    .getType(References.BLOCK_ENTITY)
                    )
            );
        });
    }
}