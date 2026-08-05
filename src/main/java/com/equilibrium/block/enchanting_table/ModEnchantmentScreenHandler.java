package com.equilibrium.block.enchanting_table;

import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.block.miscellaneous.MiscellaneousBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;

import java.util.List;
import java.util.Map;
import java.util.Optional;



public class ModEnchantmentScreenHandler extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_LAPIS_SLOT_TEXTURE = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    private final Container inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ModEnchantmentScreenHandler.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess context;
    private final RandomSource random = RandomSource.create();
    private final DataSlot seed = DataSlot.standalone();
    public final int[] enchantmentPower = new int[3];
    public final int[] enchantmentId = new int[]{-1, -1, -1};
    public final int[] enchantmentLevel = new int[]{-1, -1, -1};
    public final int maxLevel;

    public ModEnchantmentScreenHandler(int syncId, Inventory playerInventory, int maxLevel) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL, maxLevel);
    }

    public ModEnchantmentScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context, int maxLevel) {
        super(ModBlockScreenTypesRegister.EMERALD_ENCHANTING_TABLE, syncId);
        this.context = context;
        this.maxLevel = maxLevel;
        this.addSlot(new Slot(this.inventory, 0, 15, 47) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 35, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.LAPIS_LAZULI);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, ModEnchantmentScreenHandler.EMPTY_LAPIS_SLOT_TEXTURE);
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

        this.addDataSlot(DataSlot.shared(this.enchantmentPower, 0));
        this.addDataSlot(DataSlot.shared(this.enchantmentPower, 1));
        this.addDataSlot(DataSlot.shared(this.enchantmentPower, 2));
        this.addDataSlot(this.seed).set(playerInventory.player.getEnchantmentSeed());
        this.addDataSlot(DataSlot.shared(this.enchantmentId, 0));
        this.addDataSlot(DataSlot.shared(this.enchantmentId, 1));
        this.addDataSlot(DataSlot.shared(this.enchantmentId, 2));
        this.addDataSlot(DataSlot.shared(this.enchantmentLevel, 0));
        this.addDataSlot(DataSlot.shared(this.enchantmentLevel, 1));
        this.addDataSlot(DataSlot.shared(this.enchantmentLevel, 2));
    }


    public int calculateRequiredExperienceLevel(RandomSource random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getEnchantmentValue();
        if (i <= 0) {
            return 0;
        } else {
            if (bookshelfCount > 24) {
                bookshelfCount = 24;
            }


            int max = (int) (bookshelfCount*1.25F);  // 0 ~ 30
            int lower = max / 3;                     // 0 ~ lower 区间
            int upper = 2 * max / 3;                 // lower ~ upper 区间

            // 槽位0：0 到 lower 随机
            int levels_0 = 1 + random.nextInt(2)+ random.nextInt(lower + 1);
            // 槽位1：lower 到 upper 随机（确保 upper >= lower）
            int levels_1 = (int) (2 + random.nextInt(3)+ random.triangle((upper - lower)*1.5F,2));
            // 槽位2：固定为 max
            //举例,12个书架最大可达到3+12=15级附魔等级,需要花费5200点经验
            //24个满书架最大可达到31级附魔等级.需要花费10000点经验
            int levels_2 = Math.max(0,random.nextInt(4)-2 + max);

            switch (slotIndex){
                case 0 -> {
                    return levels_0;
                }
                case 1->{
                    return levels_1;
                }
                case 2->{
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
            Map.entry(15,4800),
            Map.entry(16,5200),

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
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {

        // 自定义右键点击青金石槽消耗一个青金石并扣除经验,至少有附魔时才生效
        if (slotIndex == 1 && button == 1 && actionType == ClickType.PICKUP && (this.enchantmentId[0]!=-1 || this.enchantmentId[1]!=-1||this.enchantmentId[2]!=-1)) {
            // 注意 actionType 可能为 PICKUP 或 QUICK_MOVE 等，右键通常对应 PICKUP
            ItemStack lapisStack = this.inventory.getItem(1);
            if (!lapisStack.isEmpty() && lapisStack.is(Items.LAPIS_LAZULI)) {
                // 检查玩家经验是否足够
                if (player.totalExperience >= 150 || player.isCreative()) {
                    // 消耗一个青金石
                    lapisStack.consume(1,player);
                    if (lapisStack.isEmpty()) {
                        this.inventory.setItem(1, ItemStack.EMPTY);
                    }
                    // 扣除经验（非创造模式）
                    if (!player.isCreative()) {
                        player.giveExperiencePoints(-150);
                    }

                    this.inventory.setChanged();
                    player.enchantmentSeed = this.random.nextInt();
                    this.seed.set(player.getEnchantmentSeed());
                    this.slotsChanged(this.inventory);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, player.level().random.nextFloat() * 0.1F + 0.9F);

                    return;
                }
            }
        }
        // 其他情况按原版处理
        super.clicked(slotIndex, button, actionType, player);
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (inventory == this.inventory) {
            ItemStack itemStack = inventory.getItem(0);
            if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
                this.context.execute((world, pos) -> {
                    IdMap<Holder<Enchantment>> indexedIterable = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
                    int ix = 0;

                    for (BlockPos blockPos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                        if (EnchantingTableBlock.isValidBookShelf(world, pos, blockPos)) {
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
                            List<EnchantmentInstance> list = this.generateEnchantments(world.registryAccess(), itemStack, jx, this.enchantmentPower[jx]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentInstance enchantmentLevelEntry = (EnchantmentInstance)list.get(this.random.nextInt(list.size()));
                                this.enchantmentId[jx] = indexedIterable.getId(enchantmentLevelEntry.enchantment);
                                this.enchantmentLevel[jx] = enchantmentLevelEntry.level;
                            }
                        }
                    }
                    this.enchantmentPower[0]= LEVEL_TO_XP_COST.getOrDefault(this.enchantmentPower[0],0);
                    this.enchantmentPower[1]= LEVEL_TO_XP_COST.getOrDefault(this.enchantmentPower[1],0);
                    this.enchantmentPower[2]= LEVEL_TO_XP_COST.getOrDefault(this.enchantmentPower[2],0);
                    this.broadcastChanges();
                });
            }
            else if(itemStack.is(Items.GOLDEN_APPLE)){
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
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < this.enchantmentPower.length) {
            ItemStack itemStack = this.inventory.getItem(0);
            ItemStack itemStack2 = this.inventory.getItem(1);
            int i = id + 1;
            if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.hasInfiniteMaterials()) {
                return false;
            } else if (this.enchantmentPower[id] <= 0
                    || itemStack.isEmpty()
                    || player.totalExperience < this.enchantmentPower[id] && !player.getAbilities().instabuild) {
                return false;
            } else {
                this.context.execute((world, pos) -> {
                    ItemStack itemStack3 = itemStack;
                    if(itemStack.is(Items.GOLDEN_APPLE)){
                        //扣除金苹果附魔所需的500经验值
                        player.giveExperiencePoints(-this.enchantmentPower[id]);
                        player.enchantmentSeed = this.random.nextInt();
                        appleEnchantment(player, itemStack2, i, world, pos, itemStack3);
                        this.inventory.setItem(0, Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance());
                    }
                    int actualLevel = getKeyByValue(this.enchantmentPower[id]);
                    List<EnchantmentInstance> list = this.generateEnchantments(world.registryAccess(), itemStack, id, actualLevel );
                    if (!list.isEmpty()) {

                        player.giveExperiencePoints(-this.enchantmentPower[id]);
                        player.enchantmentSeed = this.random.nextInt();

                        if (itemStack.is(Items.BOOK)) {
                            itemStack3 = itemStack.transmuteCopy(Items.ENCHANTED_BOOK);
                            this.inventory.setItem(0, itemStack3);
                        }

                        for (EnchantmentInstance enchantmentLevelEntry : list) {
                            itemStack3.enchant(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
                        }

                        itemStack2.consume(i, player);
                        if (itemStack2.isEmpty()) {
                            this.inventory.setItem(1, ItemStack.EMPTY);
                        }

                        player.awardStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, itemStack3, i);
                        }

                        this.inventory.setChanged();
                        this.seed.set(player.getEnchantmentSeed());
                        this.slotsChanged(this.inventory);
                        world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                    }
                });
                return true;
            }
        } else {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + id);
            return false;
        }
    }


    private void appleEnchantment(Player player, ItemStack itemStack2, int i, Level world, BlockPos pos, ItemStack itemStack3) {
        itemStack2.consume(i, player);
        if (itemStack2.isEmpty()) {
            this.inventory.setItem(1, ItemStack.EMPTY);
        }

        player.awardStat(Stats.ENCHANT_ITEM);
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, itemStack3, i);
        }
        this.inventory.setChanged();
        this.seed.set(player.getEnchantmentSeed());
        this.slotsChanged(this.inventory);
        world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
    }








    private List<EnchantmentInstance> generateEnchantments(RegistryAccess registryManager, ItemStack stack, int slot, int level) {
        this.random.setSeed((long)(this.seed.get() + slot));
        Optional<HolderSet.Named<Enchantment>> optional = registryManager.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (optional.isEmpty()) {
            return List.of();
        } else {
            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, stack, level, ((HolderSet.Named)optional.get()).stream());
            if (stack.is(Items.BOOK) && list.size() > 1) {
                list.remove(this.random.nextInt(list.size()));
            }

            return list;
        }
    }

    public int getLapisCount() {
        ItemStack itemStack = this.inventory.getItem(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public int getSeed() {
        return this.seed.get();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.context.execute((world, pos) -> this.clearContainer(player, this.inventory));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, EnchantingTableBlocks.EMERALD_ENCHANTING_TABLE.get())
                ||
               stillValid(this.context, player, EnchantingTableBlocks.DIAMOND_ENCHANTING_TABLE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 1) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.is(Items.LAPIS_LAZULI)) {
                if (!this.moveItemStackTo(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemStack2)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.shrink(1);
                this.slots.get(0).setByPlayer(itemStack3);
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTake(player, itemStack2);
        }

        return itemStack;
    }
}