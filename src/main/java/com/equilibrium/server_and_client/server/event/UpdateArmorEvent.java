package com.equilibrium.server_and_client.server.event;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class UpdateArmorEvent {


    // Logistic 曲线参数，可根据需求调整
    private static final double K = 10.0; // 陡峭度
    private static final double M = 0.5;  // 中点

    /**
     * logisticFunction(r, k, m):
     * r: 线性比例 (0 ~ 1)
     * k: 陡峭度 (越大曲线越陡)
     * m: 中点 (0 ~ 1)
     */
    private static double logisticFunction(double r, double k, double m) {
        // 避免溢出，可做一些极值保护
        // 例如 r 超过 [0,1] 范围时先 clamp 到 [0,1]
        r = Math.max(0.0, Math.min(1.0, r));

        return 1.0 / (1.0 + Math.exp(-k * (r - m)));
    }








    //玩家护甲值下降,套装集齐效果
    public static Text updatePlayerArmor(PlayerEntity player) {
        ArrayList<ItemStack> armorItemList = new ArrayList<>();
        player.getArmorItems().forEach(element -> {
                    if (element.getItem() instanceof ArmorItem)
                        armorItemList.add(element);
                }
        );
        //空护甲时直接返回
        if (armorItemList.isEmpty()) {
            player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(0.0D);
            return Text.of("The armor equipment is empty");
        }

        //由于耐久损耗,实际获得的护甲值
        double protection = 0;
        //最大护甲值,或者说满耐久护甲值
        double maxProtection = 0;

        for (ItemStack itemStack : armorItemList) {
            if (itemStack.getItem() instanceof ArmorItem) {
                ArmorItem armorItem = (ArmorItem) itemStack.getItem();
                //最大护甲值
                int baseProtection = armorItem.getProtection();
                //加到理论最大护甲值里面
                maxProtection = maxProtection + baseProtection;
                //最大耐久
                int baseDurability = itemStack.getMaxDamage();
                //目前耐久
                int durability = baseDurability - itemStack.getDamage();
                //满耐久一定获得满护甲值
                if (durability == baseDurability) {
                    protection = protection + baseProtection;
                } else {
                    //计算线性耐久度比例
                    float linearRatio = (float) durability / baseDurability;
                    //应用 S 型曲线 (Logistic) 做非线性衰减
                    float sCurveRatio = (float) logisticFunction(linearRatio, K, M);
                    //实际获得的护甲值
                    double exactProtection = baseProtection * (sCurveRatio);
                    //加到获得的总护甲值里面
                    protection = protection + exactProtection;
                }
            }
        }
        //总护甲损耗
        double protectionReduction = maxProtection - protection;


        //设定玩家护甲
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(-protectionReduction);
        //拥有至少10点护甲时,获得抗性提升效果
        if (protection > 10) {
            boolean hasResistance = player.hasStatusEffect(StatusEffects.RESISTANCE);
            if (!hasResistance)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 0, false, false, false));
            else if (player.getStatusEffect(StatusEffects.RESISTANCE).getDuration() <= 20) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 0, false, false, false));
            }
        }

        return Text.literal(String.format(
                "满耐久护甲=%.2f, 衰减系数=%.2f%%, 实际护甲=%.2f",
                maxProtection,
                100 * (float) (1 - protection / maxProtection),
                protection));


    }
    // 定义事件处理方法




}
