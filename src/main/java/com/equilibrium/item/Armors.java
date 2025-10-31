package com.equilibrium.item;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.Optional;

import static com.equilibrium.MITEequilibrium.MOD_ID;

public class Armors {


    public static final Item COPPER_HELMET =new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.HELMET,new Item.Settings().maxDamage(6*32));
    public static final Item COPPER_CHEST_PLATE =new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.CHESTPLATE,new Item.Settings().maxDamage(12*32));
    public static final Item COPPER_LEGGINGS =new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS,new Item.Settings().maxDamage(8*32));
    public static final Item COPPER_BOOTS =new ArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.BOOTS,new Item.Settings().maxDamage(4*32));

    public static final Item MITHRIL_HELMET =new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.HELMET,new Item.Settings().maxDamage(5*64));
    public static final Item MITHRIL_CHEST_PLATE =new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.CHESTPLATE,new Item.Settings().maxDamage(8*64));
    public static final Item MITHRIL_LEGGINGS =new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.LEGGINGS,new Item.Settings().maxDamage(7*64));
    public static final Item MITHRIL_BOOTS =new ArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.BOOTS,new Item.Settings().maxDamage(4*64));






    public static final Item ANCIENT_METAL_CHAINMAIL_HELMET =new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.HELMET,new Item.Settings().maxDamage(5*32));
    public static final Item ANCIENT_METAL_CHAINMAIL_CHEST_PLATE =new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.CHESTPLATE,new Item.Settings().maxDamage(8*32));
    public static final Item ANCIENT_METAL_CHAINMAIL_LEGGINGS =new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.LEGGINGS,new Item.Settings().maxDamage(7*32));
    public static final Item ANCIENT_METAL_CHAINMAIL_BOOTS =new ArmorItem(ModArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.BOOTS,new Item.Settings().maxDamage(4*32));


    public static void registerArmors(Item item ,String string) {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID,string), item);

    }
    public static void registerArmors(){
        registerArmors(COPPER_HELMET,"copper_helmet");
        registerArmors(COPPER_CHEST_PLATE,"copper_chest_plate");
        registerArmors(COPPER_LEGGINGS,"copper_leggings");
        registerArmors(COPPER_BOOTS,"copper_boots");

        registerArmors(MITHRIL_HELMET,"mithril_helmet");
        registerArmors(MITHRIL_CHEST_PLATE,"mithril_chest_plate");
        registerArmors(MITHRIL_LEGGINGS,"mithril_leggings");
        registerArmors(MITHRIL_BOOTS,"mithril_boots");

        registerArmors(ANCIENT_METAL_CHAINMAIL_HELMET,"ancient_metal_chainmail_helmet");
        registerArmors(ANCIENT_METAL_CHAINMAIL_CHEST_PLATE,"ancient_metal_chainmail_chest_plate");
        registerArmors(ANCIENT_METAL_CHAINMAIL_LEGGINGS,"ancient_metal_chainmail_leggings");
        registerArmors(ANCIENT_METAL_CHAINMAIL_BOOTS,"ancient_metal_chainmail_boots");




    }

}
