package com.equilibrium.block.enchanting_table;

import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.block.ModBlocksRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancement.criterion.Criteria;

import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;



public class ModEnchantmentScreenHandler extends ScreenHandler {
    static final Identifier EMPTY_LAPIS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_lapis_lazuli");
    private final Inventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            ModEnchantmentScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final Random random = Random.create();
    private final Property seed = Property.create();
    public final int[] enchantmentPower = new int[3];
    public final int[] enchantmentId = new int[]{-1, -1, -1};
    public final int[] enchantmentLevel = new int[]{-1, -1, -1};
    public final int maxLevel;


    public ModEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, int maxLevel) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY, maxLevel);
    }

    public ModEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, int maxLevel) {
        super(ModBlockScreenTypesRegister.EMERALD_ENCHANTING_TABLE, syncId);
        this.context = context;
        this.maxLevel = maxLevel;
        this.addSlot(new Slot(this.inventory, 0, 15, 47) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 35, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.LAPIS_LAZULI);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ModEnchantmentScreenHandler.EMPTY_LAPIS_SLOT_TEXTURE);
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addProperty(Property.create(this.enchantmentPower, 0));
        this.addProperty(Property.create(this.enchantmentPower, 1));
        this.addProperty(Property.create(this.enchantmentPower, 2));
        this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
        this.addProperty(Property.create(this.enchantmentId, 0));
        this.addProperty(Property.create(this.enchantmentId, 1));
        this.addProperty(Property.create(this.enchantmentId, 2));
        this.addProperty(Property.create(this.enchantmentLevel, 0));
        this.addProperty(Property.create(this.enchantmentLevel, 1));
        this.addProperty(Property.create(this.enchantmentLevel, 2));
    }


    public int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return 0;
        } else {
            if (bookshelfCount > 24) {
                bookshelfCount = 24;
            }


            int max = bookshelfCount;
            int lower = max / 3;                     // 0 ~ lower 区间
            int upper = 2 * max / 3;                 // lower ~ upper 区间

            // 槽位0：0 到 lower 随机
            int levels_0 = 1 + random.nextInt(2)+ random.nextInt(lower + 1);
            // 槽位1：lower 到 upper 随机（确保 upper >= lower）
            int levels_1 = 2 + random.nextInt(3)+ random.nextInt(upper - lower + 1);
            // 槽位2：固定为 max
            //举例,12个书架最大可达到3+12=15级附魔等级,需要花费5200点经验
            int levels_2 = random.nextInt(4) + max;

            switch (slotIndex){
                case 0 -> {
                    //受最大支持等级影响
                    //取值范围:1~6级
                    return Math.min(this.maxLevel,levels_0);
                }
                case 1->{
                    //受最大支持等级影响
                    //取值范围:1~8级
                    return Math.min(this.maxLevel,levels_1);
                }
                case 2->{
                    //取值范围:0(此时该附魔栏为空)~15级
                    //每次有1/4的概率取到最大经验值
                    return levels_2;
                }
                default -> {
                    return 0;
                }
            }
        }
    }
    //附魔台所能达到的附魔等级,与书架数量正相关,给出了单次附魔价格
    //绿宝石附魔台:最大汲取12个书架方块,达到15级的附魔等级,花费5200点经验
    private static final Map<Integer,Integer> LEVEL_TO_XP_COST = Map.ofEntries(
            Map.entry(1,100),
            Map.entry(2,200),
            Map.entry(3,300),
            Map.entry(4,400),
            Map.entry(5,500),
            Map.entry(6,600),
            Map.entry(7,800),
            Map.entry(8,1000),
            Map.entry(9,1200),
            Map.entry(10,1600),
            Map.entry(11,2000),
            Map.entry(12,2400),
            Map.entry(13,3200),
            Map.entry(14,4000),
            Map.entry(15,5200),


            
            Map.entry(16,5600),
            Map.entry(17,5800),
            Map.entry(18,6000),
            Map.entry(19,6200),
            Map.entry(20,6400),


            Map.entry(21,6600),
            Map.entry(22,6800),
            Map.entry(23,7000),
            Map.entry(24,7200),
            Map.entry(25,7600),
            Map.entry(26,8000),
            Map.entry(27,8400),
            Map.entry(28,8800),
            Map.entry(29,9200),
            Map.entry(30,9600),
            Map.entry(31,10000)

    );

    public static Integer getKeyByValue(Integer value) {
        for (Map.Entry<Integer, Integer> entry : LEVEL_TO_XP_COST.entrySet()) {
            //遍历所有实例,找到要找的那一个value,返回它的key
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return 0; // 如果未找到
    }





    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            ItemStack itemStack = inventory.getStack(0);
            if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
                this.context.run((world, pos) -> {
                    IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIndexedEntries();
                    int ix = 0;

                    for (BlockPos blockPos : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
                        if (EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) {
                            ix++;
                        }
                    }
                    //读取的最大书架数量:12个/24个
                    ix = Math.min(this.maxLevel,ix);

                    this.random.setSeed((long)this.seed.get());

                    for (int j = 0; j < 3; j++) {
                        this.enchantmentPower[j] = calculateRequiredExperienceLevel(this.random, j, ix, itemStack);
                        this.enchantmentId[j] = -1;
                        this.enchantmentLevel[j] = -1;
                        if (this.enchantmentPower[j] < j + 1) {
                            this.enchantmentPower[j] = 0;
                        }
                    }

                    for (int jx = 0; jx < 3; jx++) {
                        if (this.enchantmentPower[jx] > 0) {
                            List<EnchantmentLevelEntry> list = this.generateEnchantments(world.getRegistryManager(), itemStack, jx, this.enchantmentPower[jx]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(this.random.nextInt(list.size()));
                                this.enchantmentId[jx] = indexedIterable.getRawId(enchantmentLevelEntry.enchantment);
                                this.enchantmentLevel[jx] = enchantmentLevelEntry.level;
                            }
                        }
                    }
                    this.enchantmentPower[0]= LEVEL_TO_XP_COST.getOrDefault(this.enchantmentPower[0],0);
                    this.enchantmentPower[1]= LEVEL_TO_XP_COST.getOrDefault(this.enchantmentPower[1],0);
                    this.enchantmentPower[2]= LEVEL_TO_XP_COST.getOrDefault(this.enchantmentPower[2],0);
                    this.sendContentUpdates();
                });
            }
            else if(itemStack.isOf(Items.GOLDEN_APPLE)){
                this.enchantmentPower[0] = 500;
                this.enchantmentPower[1] = 500;
                this.enchantmentPower[2] = 500;
            }
            else {
                for (int i = 0; i < 3; i++) {
                    this.enchantmentPower[i] = 0;
                    this.enchantmentId[i] = -1;
                    this.enchantmentLevel[i] = -1;
                }
            }
        }
    }


    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.enchantmentPower.length) {
            ItemStack itemStack = this.inventory.getStack(0);
            ItemStack itemStack2 = this.inventory.getStack(1);
            int i = id + 1;
            if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.isInCreativeMode()) {
                return false;
            } else if (this.enchantmentPower[id] <= 0
                    || itemStack.isEmpty()
                    || player.totalExperience < this.enchantmentPower[id] && !player.getAbilities().creativeMode) {
                return false;
            } else {
                this.context.run((world, pos) -> {
                    ItemStack itemStack3 = itemStack;
                    if(itemStack.isOf(Items.GOLDEN_APPLE)){
                        //扣除金苹果附魔所需的500经验值
                        player.addExperience(-this.enchantmentPower[id]);
                        player.enchantmentTableSeed = this.random.nextInt();
                        appleEnchantment(player, itemStack2, i, world, pos, itemStack3);
                        this.inventory.setStack(0, Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack());
                    }
                    int actualLevel = getKeyByValue(this.enchantmentPower[id]);
                    List<EnchantmentLevelEntry> list = this.generateEnchantments(world.getRegistryManager(), itemStack, id, actualLevel );
                    if (!list.isEmpty()) {

                        player.addExperience(-this.enchantmentPower[id]);
                        player.enchantmentTableSeed = this.random.nextInt();

                        if (itemStack.isOf(Items.BOOK)) {
                            itemStack3 = itemStack.withItem(Items.ENCHANTED_BOOK);
                            this.inventory.setStack(0, itemStack3);
                        }

                        for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
                            itemStack3.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
                        }

                        itemStack2.decrementUnlessCreative(i, player);
                        if (itemStack2.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }

                        player.incrementStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayerEntity) {
                            Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemStack3, i);
                        }

                        this.inventory.markDirty();
                        this.seed.set(player.getEnchantmentTableSeed());
                        this.onContentChanged(this.inventory);
                        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                    }
                });
                return true;
            }
        } else {
            Util.error(player.getName() + " pressed invalid button id: " + id);
            return false;
        }
    }


    private void appleEnchantment(PlayerEntity player, ItemStack itemStack2, int i, World world, BlockPos pos, ItemStack itemStack3) {
        itemStack2.decrementUnlessCreative(i, player);
        if (itemStack2.isEmpty()) {
            this.inventory.setStack(1, ItemStack.EMPTY);
        }

        player.incrementStat(Stats.ENCHANT_ITEM);
        if (player instanceof ServerPlayerEntity) {
            Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemStack3, i);
        }
        this.inventory.markDirty();
        this.seed.set(player.getEnchantmentTableSeed());
        this.onContentChanged(this.inventory);
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
    }








    private List<EnchantmentLevelEntry> generateEnchantments(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level) {
        this.random.setSeed((long)(this.seed.get() + slot));
        Optional<RegistryEntryList.Named<Enchantment>> optional = registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (optional.isEmpty()) {
            return List.of();
        } else {
            List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, level, ((RegistryEntryList.Named)optional.get()).stream());
            if (stack.isOf(Items.BOOK) && list.size() > 1) {
                list.remove(this.random.nextInt(list.size()));
            }

            return list;
        }
    }

    public int getLapisCount() {
        ItemStack itemStack = this.inventory.getStack(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public int getSeed() {
        return this.seed.get();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocksRegistry.EMERALD_ENCHANTING_TABLE);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.isOf(Items.LAPIS_LAZULI)) {
                if (!this.insertItem(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.get(0).hasStack() || !this.slots.get(0).canInsert(itemStack2)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.decrement(1);
                this.slots.get(0).setStack(itemStack3);
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }
}