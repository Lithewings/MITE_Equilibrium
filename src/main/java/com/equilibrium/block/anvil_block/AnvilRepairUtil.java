package com.equilibrium.block.anvil_block;

import com.equilibrium.item.fishrod.AnvilRepairRuleView;
import net.minecraft.item.ItemStack;

public class AnvilRepairUtil {


    public static final int VANILLA_REPAIR_TIMES = 4;



    public static int repairTimes(ItemStack stack) {
        if (stack.getItem() instanceof AnvilRepairRuleView view) {
            return Math.max(1, view.anvilRepairTimes());
        }
        return VANILLA_REPAIR_TIMES;
    }


    public static int repairCapacity(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) {
            return 0;
        }
        int times = repairTimes(stack);
        return times <= 1 ? maxDamage : maxDamage / times;
    }

    //防止耐久溢出
    public static int nextRepairAmount(ItemStack stack) {
        return Math.min(stack.getDamage(), repairCapacity(stack));
    }
}
