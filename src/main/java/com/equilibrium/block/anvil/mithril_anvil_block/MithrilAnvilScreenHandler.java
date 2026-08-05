package com.equilibrium.block.anvil.mithril_anvil_block;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.tags.ModItemTags;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static com.equilibrium.block.anvil.mithril_anvil_block.MithrilAnvilBlock.*;
import static com.equilibrium.util.AnvilPhase.getPhaseFromDurability;


public class MithrilAnvilScreenHandler extends ItemCombinerMenu {
    public static final int INPUT_1_ID = 0;
    public static final int INPUT_2_ID = 1;
    public static final int OUTPUT_ID = 2;

    private static final Logger LOGGER = LogUtils.getLogger();

    private int repairItemUsage;
    @Nullable
    private String newItemName;
    private final DataSlot levelCost = DataSlot.standalone();


    public MithrilAnvilScreenHandler(int syncId, Inventory inventory) {
        this(syncId, inventory, ContainerLevelAccess.NULL);
    }

    public MithrilAnvilScreenHandler(int syncId, Inventory inventory, ContainerLevelAccess context) {
        super(ModBlockScreenTypesRegister.MITHRIL_ANVIL_SCREEN_TYPE,syncId,inventory,context);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, stack -> true).withSlot(1, 76, 47, stack -> true).withResultSlot(2, 134, 47).build();
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return true;
    }

    @Override
    protected boolean mayPickup(Player player, boolean present) {

        boolean preCondition =  (player.hasInfiniteMaterials() || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
        //以下条件不可以满足,否则不接受本次铁砧操作
        boolean additionalCondition = (this.inputSlots.getItem(0).is(Items.BUCKET)&&this.inputSlots.getItem(0).getCount()>1);

        boolean shouldRejectForIronAnvil = shouldRejectForIronAnvil(this.inputSlots.getItem(0),this.inputSlots.getItem(1));

        return (preCondition && !additionalCondition && !shouldRejectForIronAnvil);

    }

    private boolean shouldRejectForIronAnvil(ItemStack input1, ItemStack input2) {
        boolean shouldReject1 = input1.is(ModItemTags.MITHRIL_ANVIL_REJECTION);
        boolean shouldReject2 = input2.is(ModItemTags.MITHRIL_ANVIL_REJECTION);
        return shouldReject1 || shouldReject2;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        //每次使用,耐久减1
        this.access.execute((world, pos) -> {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.hasProperty(MITHRIL_ANVIL_DURABILITY_PROPERTY)) {
                        //铁砧目前的耐久
                        int i = blockState.getValue(MITHRIL_ANVIL_DURABILITY_PROPERTY);
                        //铁砧破坏进度
                        int phase = getPhaseFromDurability(MITHRIL_ANVIL_MAX_DURABILITY,blockState.getValue(MITHRIL_ANVIL_DURABILITY_PROPERTY));
                        //将耐久-1写入方块状态中,并更新外观状态
                        world.setBlockAndUpdate(pos,blockState
                                .setValue(MithrilAnvilBlock.FACING, blockState.getValue(MithrilAnvilBlock.FACING))
                                .setValue(MITHRIL_ANVIL_DURABILITY_PROPERTY,Math.clamp(i-1,0, MITHRIL_ANVIL_MAX_DURABILITY))
                                .setValue(MITHRIL_ANVIL_STAGE,Math.clamp(phase,0,2))
                        );
                    }
                    else
                        OnServerInitialize.LOGGER.error("No such Property called"+ MITHRIL_ANVIL_DURABILITY_PROPERTY + "or"+ MITHRIL_ANVIL_STAGE +"at the Anvil");
                }
        );
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.inputSlots.getItem(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.shrink(this.repairItemUsage);
                getSetStack(itemStack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.levelCost.set(0);
        this.access.execute((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.hasInfiniteMaterials()) {
                //耐久为1时,直接损坏
                if(blockState.getValue(MITHRIL_ANVIL_DURABILITY_PROPERTY)==0) {
                    world.removeBlock(pos, false);
                    world.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                }
                world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
            }




        });


    }

    private void getSetStack(ItemStack itemStack) {
        this.inputSlots.setItem(1, itemStack);
    }


    @Override
    public void createResult() {
        ItemStack itemStack = this.inputSlots.getItem(0);
        this.levelCost.set(1);
        int i = 0;
        long l = 0L;
        int j = 0;
        if (!itemStack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemStack)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = this.inputSlots.getItem(1);
            ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemStack2));
            l += (long)itemStack.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue()
                    + (long)itemStack3.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue();
            this.repairItemUsage = 0;
            if (!itemStack3.isEmpty()) {
                boolean bl = itemStack3.has(DataComponents.STORED_ENCHANTMENTS);
                if (itemStack2.isDamageableItem() && itemStack2.getItem().isValidRepairItem(itemStack, itemStack3)) {
                    int k = Math.min(itemStack2.getDamageValue(), itemStack2.getMaxDamage() / 4);
                    if (k <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    int m;
                    for (m = 0; k > 0 && m < itemStack3.getCount(); m++) {
                        int n = itemStack2.getDamageValue() - k;
                        itemStack2.setDamageValue(n);
                        i++;
                        k = Math.min(itemStack2.getDamageValue(), itemStack2.getMaxDamage() / 4);
                    }

                    this.repairItemUsage = m;
                } else {
                    if (!bl && (!itemStack2.is(itemStack3.getItem()) || !itemStack2.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    if (itemStack2.isDamageableItem() && !bl) {
                        int kx = itemStack.getMaxDamage() - itemStack.getDamageValue();
                        int m = itemStack3.getMaxDamage() - itemStack3.getDamageValue();
                        int n = m + itemStack2.getMaxDamage() * 12 / 100;
                        int o = kx + n;
                        int p = itemStack2.getMaxDamage() - o;
                        if (p < 0) {
                            p = 0;
                        }

                        if (p < itemStack2.getDamageValue()) {
                            itemStack2.setDamageValue(p);
                            i += 2;
                        }
                    }

                    ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(itemStack3);
                    boolean bl2 = false;
                    boolean bl3 = false;

                    for (Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
                        Holder<Enchantment> registryEntry = (Holder<Enchantment>)entry.getKey();
                        int q = builder.getLevel(registryEntry);
                        int r = entry.getIntValue();
                        r = q == r ? r + 1 : Math.max(r, q);
                        Enchantment enchantment = registryEntry.value();
                        boolean bl4 = enchantment.canEnchant(itemStack);
                        if (this.player.getAbilities().instabuild || itemStack.is(Items.ENCHANTED_BOOK)) {
                            bl4 = true;
                        }

                        for (Holder<Enchantment> registryEntry2 : builder.keySet()) {
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.areCompatible(registryEntry, registryEntry2)) {
                                bl4 = false;
                                i++;
                            }
                        }

                        if (!bl4) {
                            bl3 = true;
                        } else {
                            bl2 = true;
                            if (r > enchantment.getMaxLevel()) {
                                r = enchantment.getMaxLevel();
                            }

                            builder.set(registryEntry, r);
                            int s = enchantment.getAnvilCost();
                            if (bl) {
                                s = Math.max(1, s / 2);
                            }

                            i += s * r;
                            if (itemStack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }

                    if (bl3 && !bl2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }
                }
            }

            if (this.newItemName != null && !StringUtil.isBlank(this.newItemName)) {
                if (!this.newItemName.equals(itemStack.getHoverName().getString())) {
                    j = 1;
                    i += j;
                    itemStack2.set(DataComponents.CUSTOM_NAME, Component.literal(this.newItemName));
                }
            } else if (itemStack.has(DataComponents.CUSTOM_NAME)) {
                j = 1;
                i += j;
                itemStack2.remove(DataComponents.CUSTOM_NAME);
            }

            int t = (int)Mth.clamp(l + (long)i, 0L, 2147483647L);
            this.levelCost.set(t);
            if (i <= 0) {
                itemStack2 = ItemStack.EMPTY;
            }

            if (j == i && j > 0 && this.levelCost.get() >= 40) {
                this.levelCost.set(39);
            }

//            if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
//                itemStack2 = ItemStack.EMPTY;
//            }

            if (!itemStack2.isEmpty()) {
                int kxx = itemStack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                if (kxx < itemStack3.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
                    kxx = itemStack3.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                }

                if (j != i || j == 0) {
                    kxx = getNextCost(kxx);
                }

                itemStack2.set(DataComponents.REPAIR_COST, kxx);
                EnchantmentHelper.setEnchantments(itemStack2, builder.toImmutable());
            }

            this.resultSlots.setItem(0, itemStack2);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        }
    }

    //修复等级要求最大为52级
    public static int getNextCost(int cost) {
        return (int)Math.min((long)cost * 2L + 1L, 51L);
    }

    public boolean setNewItemName(String newItemName) {
        String string = sanitize(newItemName);
        if (string != null && !string.equals(this.newItemName)) {
            this.newItemName = string;
            if (this.getSlot(2).hasItem()) {
                ItemStack itemStack = this.getSlot(2).getItem();
                if (StringUtil.isBlank(string)) {
                    itemStack.remove(DataComponents.CUSTOM_NAME);
                } else {
                    itemStack.set(DataComponents.CUSTOM_NAME, Component.literal(string));
                }
            }

            this.createResult();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private static String sanitize(String name) {
        String string = StringUtil.filterText(name);
        return string.length() <= 50 ? string : null;
    }

    public int getLevelCost() {
        return this.levelCost.get();
    }
}
