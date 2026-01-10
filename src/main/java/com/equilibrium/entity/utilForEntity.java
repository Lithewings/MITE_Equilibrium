package com.equilibrium.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class utilForEntity {
    public static boolean forPlayerIsEnchantedItemCauseDamage(DamageSource source) {
        // 获取直接攻击者（可能是箭实体）和间接攻击者（可能是玩家）



        // 对于箭伤害，需要特殊处理
        if (source.isOf(DamageTypes.ARROW)) {
            // 如果是玩家射出的箭
            if ( source.getAttacker() instanceof PlayerEntity player) {
                ItemStack weapon = player.getMainHandStack();

                // 如果玩家主手是弓或弩，检查附魔
                if (weapon.getItem() == Items.BOW || weapon.getItem() == Items.CROSSBOW) {
                    return weapon.hasEnchantments();
                } else {
                    // 如果玩家主手不是弓或弩，可能是切换了物品，无法准确判断
                    return false;
                }
            }
            return false;
        } else {
            // 对于非箭伤害
            if (source.getAttacker() instanceof PlayerEntity player) {
                ItemStack weapon = player.getMainHandStack();
                return weapon.hasEnchantments();
            }
            return false;
        }
    }
}
