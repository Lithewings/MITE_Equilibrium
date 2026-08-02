package com.equilibrium.item;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.item.tools_attribute.ModToolMaterials;
import com.equilibrium.item.tools_attribute.flint.FlintAxeOrHatchet;
import com.equilibrium.item.tools_attribute.flint.FlintKnife;
import com.equilibrium.item.tools_attribute.flint.FlintShovel;
import com.equilibrium.item.tools_attribute.metal.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = OnServerInitialize.MOD_ID)
public class Tools {

    // ========== 字段声明（不初始化） ==========
    public static Item FLINT_HATCHET;
    public static Item FLINT_AXE;
    public static Item FLINT_KNIFE;
    public static Item FLINT_SHOVEL;

    public static Item COPPER_AXE;
    public static Item GOLD_AXE;
    public static Item SILVER_AXE;
    public static Item IRON_AXE;
    public static Item MITHRIL_AXE;
    public static Item ADAMANTIUM_AXE;

    public static Item COPPER_PICKAXE;
    public static Item GOLD_PICKAXE;
    public static Item SILVER_PICKAXE;
    public static Item IRON_PICKAXE;
    public static Item MITHRIL_PICKAXE;
    public static Item ADAMANTIUM_PICKAXE;

    public static Item COPPER_HOE;
    public static Item GOLD_HOE;
    public static Item SILVER_HOE;
    public static Item IRON_HOE;
    public static Item MITHRIL_HOE;
    public static Item ADAMANTIUM_HOE;

    public static Item COPPER_HAMMER;
    public static Item SILVER_HAMMER;
    public static Item GOLD_HAMMER;
    public static Item IRON_HAMMER;
    public static Item MITHRIL_HAMMER;
    public static Item ADAMANTIUM_HAMMER;

    public static Item COPPER_SHOVEL;
    public static Item GOLD_SHOVEL;
    public static Item SILVER_SHOVEL;
    public static Item IRON_SHOVEL;
    public static Item MITHRIL_SHOVEL;
    public static Item ADAMANTIUM_SHOVEL;

    public static Item COPPER_SWORD;
    public static Item GOLD_SWORD;
    public static Item SILVER_SWORD;
    public static Item IRON_SWORD;
    public static Item MITHRIL_SWORD;
    public static Item ADMANTIUM_SWORD;

    public static Item COPPER_DAGGER;
    public static Item GOLD_DAGGER;
    public static Item SILVER_DAGGER;
    public static Item IRON_DAGGER;
    public static Item MITHRIL_DAGGER;
    public static Item ADMANTIUM_DAGGER;

    // ========== 注册入口 ==========
    @SubscribeEvent
    public static void registerTools(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), helper -> {
            // 创建所有工具实例（此时注册表可写）
            FLINT_HATCHET = createFlintAxeOrHatchetItem(ModToolMaterials.FLINT_HATCHET, 4, 1f);
            FLINT_AXE = createFlintAxeOrHatchetItem(ModToolMaterials.FLINT_AXE, 5, 1f);
            FLINT_KNIFE = createFlintKnifeItem(ModToolMaterials.FLINT_KNIFE, 3, 2f);
            FLINT_SHOVEL = createFlintShovelItem(ModToolMaterials.FLINT_SHOVEL, 2, 2f);

            COPPER_AXE = createMetalAxeItem(ModToolMaterials.COPPER_AXE, 8, 0.7f);
            GOLD_AXE = createMetalAxeItem(ModToolMaterials.GOLD_AXE, 5, 0.5f);
            SILVER_AXE = createMetalAxeItem(ModToolMaterials.SILVER_AXE, 8, 0.7f);
            IRON_AXE = createMetalAxeItem(ModToolMaterials.IRON_AXE, 9, 0.7f);
            MITHRIL_AXE = createMetalAxeItem(ModToolMaterials.MITHRIL_AXE, 10, 0.8f);
            ADAMANTIUM_AXE = createMetalAxeItem(ModToolMaterials.ADAMANTIUM_AXE, 10, 1f);

            COPPER_PICKAXE = createMetalPickAxeItem(ModToolMaterials.COPPER_PICKAXE, 5, 2f);
            GOLD_PICKAXE = createMetalPickAxeItem(ModToolMaterials.GOLD_PICKAXE, 5, 2f);
            SILVER_PICKAXE = createMetalPickAxeItem(ModToolMaterials.SILVER_PICKAXE, 5, 2f);
            IRON_PICKAXE = createMetalPickAxeItem(ModToolMaterials.IRON_PICKAXE, 6, 2f);
            MITHRIL_PICKAXE = createMetalPickAxeItem(ModToolMaterials.MITHRIL_PICKAXE, 7, 2f);
            ADAMANTIUM_PICKAXE = createMetalPickAxeItem(ModToolMaterials.ADAMANTIUM_PICKAXE, 8, 2f);

            COPPER_HOE = createMetalHoeItem(ModToolMaterials.COPPER_HOE, 4, 3f);
            GOLD_HOE = createMetalHoeItem(ModToolMaterials.GOLD_HOE, 4, 3f);
            SILVER_HOE = createMetalHoeItem(ModToolMaterials.SILVER_HOE, 4, 3f);
            IRON_HOE = createMetalHoeItem(ModToolMaterials.IRON_HOE, 5, 3f);
            MITHRIL_HOE = createMetalHoeItem(ModToolMaterials.MITHRIL_HOE, 6, 3f);
            ADAMANTIUM_HOE = createAdamantiumHoeItem(ModToolMaterials.ADAMANTIUM_HOE, 7, 3f);

            COPPER_HAMMER = createMetalHammerItem(ModToolMaterials.COPPER_HAMMER, 6, 3f);
            SILVER_HAMMER = createSilverHammerItem(ModToolMaterials.SILVER_HAMMER, 6, 3f);
            GOLD_HAMMER = createMetalHammerItem(ModToolMaterials.GOLD_HAMMER, 6, 3f);
            IRON_HAMMER = createMetalHammerItem(ModToolMaterials.IRON_HAMMER, 7, 3f);
            MITHRIL_HAMMER = createMetalHammerItem(ModToolMaterials.MITHRIL_HAMMER, 8, 3f);
            ADAMANTIUM_HAMMER = createMetalHammerItem(ModToolMaterials.ADAMANTIUM_HAMMER, 9, 4f);

            COPPER_SHOVEL = createMetalShovelItem(ModToolMaterials.COPPER_SHOVEL, 3, 4f);
            GOLD_SHOVEL = createMetalShovelItem(ModToolMaterials.GOLD_SHOVEL, 3, 4f);
            SILVER_SHOVEL = createMetalShovelItem(ModToolMaterials.SILVER_SHOVEL, 3, 4f);
            IRON_SHOVEL = createMetalShovelItem(ModToolMaterials.IRON_SHOVEL, 4, 4f);
            MITHRIL_SHOVEL = createMetalShovelItem(ModToolMaterials.MITHRIL_SHOVEL, 5, 4f);
            ADAMANTIUM_SHOVEL = createMetalShovelItem(ModToolMaterials.ADAMANTIUM_SHOVEL, 6, 4f);

            COPPER_SWORD = createMetalSwordItem(ModToolMaterials.COPPER_SWORD, 7, 3f);
            GOLD_SWORD = createMetalSwordItem(ModToolMaterials.GOLD_SWORD, 7, 3f);
            SILVER_SWORD = createSilverSwordItem(ModToolMaterials.SILVER_SWORD, 7, 3f);
            IRON_SWORD = createMetalSwordItem(ModToolMaterials.IRON_SWORD, 8, 3f);
            MITHRIL_SWORD = createMetalSwordItem(ModToolMaterials.MITHRIL_SWORD, 9, 3f);
            ADMANTIUM_SWORD = createMetalSwordItem(ModToolMaterials.ADAMANTIUM_SWORD, 10, 3f);

            COPPER_DAGGER = createMetalDaggerItem(ModToolMaterials.COPPER_DAGGER, 6, 4f);
            GOLD_DAGGER = createMetalDaggerItem(ModToolMaterials.GOLD_DAGGER, 6, 4f);
            SILVER_DAGGER = createSilverDaggerItem(ModToolMaterials.SILVER_DAGGER, 6, 4f);
            IRON_DAGGER = createMetalDaggerItem(ModToolMaterials.IRON_DAGGER, 7, 4f);
            MITHRIL_DAGGER = createMetalDaggerItem(ModToolMaterials.MITHRIL_DAGGER, 8, 4f);
            ADMANTIUM_DAGGER = createMetalDaggerItem(ModToolMaterials.ADAMANTIUM_DAGGER, 9, 4f);

            // 注册所有工具（与原来 registerModItemTools 顺序一致）
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_hammer"), ADAMANTIUM_HAMMER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_hammer"), COPPER_HAMMER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_hammer"), SILVER_HAMMER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_hammer"), GOLD_HAMMER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_hammer"), IRON_HAMMER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_hammer"), MITHRIL_HAMMER);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_axe"), ADAMANTIUM_AXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_axe"), MITHRIL_AXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_axe"), IRON_AXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_axe"), COPPER_AXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_axe"), SILVER_AXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_axe"), GOLD_AXE);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_hoe"), ADAMANTIUM_HOE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_hoe"), MITHRIL_HOE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_hoe"), IRON_HOE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_hoe"), COPPER_HOE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_hoe"), SILVER_HOE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_hoe"), GOLD_HOE);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_pickaxe"), ADAMANTIUM_PICKAXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_pickaxe"), MITHRIL_PICKAXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_pickaxe"), IRON_PICKAXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_pickaxe"), COPPER_PICKAXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_pickaxe"), SILVER_PICKAXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_pickaxe"), GOLD_PICKAXE);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_sword"), ADMANTIUM_SWORD);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_sword"), MITHRIL_SWORD);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_sword"), IRON_SWORD);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_sword"), COPPER_SWORD);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_sword"), SILVER_SWORD);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_sword"), GOLD_SWORD);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_shovel"), ADAMANTIUM_SHOVEL);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_shovel"), MITHRIL_SHOVEL);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_shovel"), IRON_SHOVEL);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_shovel"), COPPER_SHOVEL);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_shovel"), SILVER_SHOVEL);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_shovel"), GOLD_SHOVEL);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "adamantium_dagger"), ADMANTIUM_DAGGER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "mithril_dagger"), MITHRIL_DAGGER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "iron_dagger"), IRON_DAGGER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "copper_dagger"), COPPER_DAGGER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "silver_dagger"), SILVER_DAGGER);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "gold_dagger"), GOLD_DAGGER);

            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "flint_hatchet"), FLINT_HATCHET);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "flint_axe"), FLINT_AXE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "flint_knife"), FLINT_KNIFE);
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("miteequilibrium", "flint_shovel"), FLINT_SHOVEL);
        });
    }

    // ========== 以下所有工厂方法保持不变 ==========
    public static Item createAdamantiumHoeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new AdamantiumHoe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createFlintShovelItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new FlintShovel(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createFlintKnifeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new FlintKnife(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createSilverSwordItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new SilverSword(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createSilverDaggerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new SilverDagger(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createFlintAxeOrHatchetItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new FlintAxeOrHatchet(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalAxeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalAxe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalHoeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalHoe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalShovelItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalShovel(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalPickAxeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalPickAxe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalSwordItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalSword(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalDaggerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalDagger(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalHammerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalHammer(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createSilverHammerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new SilverHammer(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }
}