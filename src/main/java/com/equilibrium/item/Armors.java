package com.equilibrium.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class Armors {

    // 只声明字段，不在此处创建实例
    public static Item COPPER_HELMET;
    public static Item COPPER_CHEST_PLATE;
    public static Item COPPER_LEGGINGS;
    public static Item COPPER_BOOTS;

    public static Item MITHRIL_HELMET;
    public static Item MITHRIL_CHEST_PLATE;
    public static Item MITHRIL_LEGGINGS;
    public static Item MITHRIL_BOOTS;

    public static Item ANCIENT_METAL_CHAINMAIL_HELMET;
    public static Item ANCIENT_METAL_CHAINMAIL_CHEST_PLATE;
    public static Item ANCIENT_METAL_CHAINMAIL_LEGGINGS;
    public static Item ANCIENT_METAL_CHAINMAIL_BOOTS;

    @SubscribeEvent
    public static void registerArmors(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            // 创建实例（此时注册表未冻结）
            COPPER_HELMET = new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.HELMET, new Item.Properties().durability(6 * 32));
            COPPER_CHEST_PLATE = new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(12 * 32));
            COPPER_LEGGINGS = new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(8 * 32));
            COPPER_BOOTS = new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.BOOTS, new Item.Properties().durability(4 * 32));

            MITHRIL_HELMET = new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.HELMET, new Item.Properties().durability(5 * 64));
            MITHRIL_CHEST_PLATE = new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(8 * 64));
            MITHRIL_LEGGINGS = new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(7 * 64));
            MITHRIL_BOOTS = new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.BOOTS, new Item.Properties().durability(4 * 64));

            ANCIENT_METAL_CHAINMAIL_HELMET = new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.HELMET, new Item.Properties().durability(5 * 32));
            ANCIENT_METAL_CHAINMAIL_CHEST_PLATE = new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(8 * 32));
            ANCIENT_METAL_CHAINMAIL_LEGGINGS = new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(7 * 32));
            ANCIENT_METAL_CHAINMAIL_BOOTS = new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.BOOTS, new Item.Properties().durability(4 * 32));

            // 注册所有护甲
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper_helmet"), COPPER_HELMET);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper_chest_plate"), COPPER_CHEST_PLATE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper_leggings"), COPPER_LEGGINGS);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "copper_boots"), COPPER_BOOTS);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_helmet"), MITHRIL_HELMET);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_chest_plate"), MITHRIL_CHEST_PLATE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_leggings"), MITHRIL_LEGGINGS);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "mithril_boots"), MITHRIL_BOOTS);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ancient_metal_chainmail_helmet"), ANCIENT_METAL_CHAINMAIL_HELMET);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ancient_metal_chainmail_chest_plate"), ANCIENT_METAL_CHAINMAIL_CHEST_PLATE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ancient_metal_chainmail_leggings"), ANCIENT_METAL_CHAINMAIL_LEGGINGS);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "ancient_metal_chainmail_boots"), ANCIENT_METAL_CHAINMAIL_BOOTS);
        });
    }
}