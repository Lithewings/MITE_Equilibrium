package com.equilibrium.mixin.vanilla_itemsmixin;

import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;

import static net.minecraft.item.Items.register;

@Mixin(Items.class)
public class ItemsMixin {
    //强行覆盖会造成注册表混淆导致多人游戏退出时闪退或者无法进入
    //saturationModifier :营养值增加倍率


//    @Shadow
//    public static Item register(Block block) {
//        return register(new BlockItem(block, new Item.Settings()));
//    }
//    @Shadow
//    public static Item register(Block block, UnaryOperator<Item.Settings> settingsOperator) {
//        return register(new BlockItem(block, (Item.Settings)settingsOperator.apply(new Item.Settings())));
//    }
//    @Shadow
//    public static Item register(Block block, Block... blocks) {
//        BlockItem blockItem = new BlockItem(block, new Item.Settings());
//
//        for (Block block2 : blocks) {
//            Item.BLOCK_ITEMS.put(block2, blockItem);
//        }
//
//        return register(blockItem);
//    }
//    @Shadow
//    public static Item register(BlockItem item) {
//        return register(item.getBlock(), item);
//    }
//    @Shadow
//    public static Item register(Block block, Item item) {
//        return register(Registries.BLOCK.getId(block), item);
//    }
//    @Shadow
//    public static Item register(String id, Item item) {
//        return register(Identifier.ofVanilla(id), item);
//    }
//    @Shadow
//    public static Item register(Identifier id, Item item) {
//        return register(RegistryKey.of(Registries.ITEM.getKey(), id), item);
//    }
//    @Shadow
//    public static Item register(RegistryKey<Item> key, Item item) {
//        if (item instanceof BlockItem) {
//            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
//        }
//
//        return Registry.register(Registries.ITEM, key, item);
//    }


//    @Shadow
//    @Final
//    public static Item GOLDEN_APPLE = register(Identifier.of("minecraft","golden_apple"), new EnchantedAppleItem(new EnchantedAppleItem.Settings().rarity(Rarity.RARE).food(EnchantedAppleItem.GOLDEN_APPLE)));

//    @Shadow
//    @Final
//    public static Item PUMPKIN_PIE = register("pumpkin_pie", new Item(new Item.Settings().food(new FoodComponent(10,12f,false,1.6F, Optional.empty(), List.of()))));
//
//
//
//    @Shadow
//    @Final
//    public static final Item MELON_SLICE = register("melon_slice", new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(1).saturationModifier(0).build())));
//
//
//    @Shadow
//    @Final
//    public static final Item BREAD = register("bread", new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(2).saturationModifier((float) 8 /4).build())));
//
//    @Shadow
//    @Final
//    public static final Item BAKED_POTATO = register("baked_potato", new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(2).saturationModifier((float) 6 /4).build())));


    //    private final static Item LAPIS_LAZULI = register("lapis_lazuli", new Item(new Item.Settings()));

}
