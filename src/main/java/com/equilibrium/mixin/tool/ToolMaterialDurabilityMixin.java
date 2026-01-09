package com.equilibrium.mixin.tool;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ToolMaterials.class)
public abstract class ToolMaterialDurabilityMixin {
    @Shadow
    @Final
    private TagKey<Block> inverseTag;
    @Shadow
    @Final
    private int itemDurability;
    @Shadow
    @Final
    private float miningSpeed;
    @Shadow
    @Final
    private float attackDamage;
    @Shadow
    @Final
    private int enchantability;
    @Shadow
    @Final
    private Supplier<Ingredient> repairIngredient;


    @Shadow public abstract TagKey<Block> getInverseTag();

    @Shadow public abstract float getAttackDamage();

    @Shadow public abstract int getEnchantability();

    @Inject(method = "getDurability",at = @At(value = "HEAD"),cancellable = true)
    public void getDurability(CallbackInfoReturnable<Integer> cir) {
//        LOGGER.info(String.valueOf(this.inverseTag));
        if (Objects.equals(String.valueOf(this.inverseTag), "TagKey[minecraft:block / minecraft:incorrect_for_wooden_tool]"))
            cir.setReturnValue(160);
        else if (Objects.equals(String.valueOf(this.inverseTag),"TagKey[minecraft:block / minecraft:incorrect_for_gold_tool]"))
            cir.setReturnValue(40);
        else if (Objects.equals(String.valueOf(this.inverseTag),"TagKey[minecraft:block / minecraft:incorrect_for_iron_tool]"))
            cir.setReturnValue(200);
        //钻石很脆,秘银实际替代了钻石装备和工具,钻石工具唯一的用处是升级成下界合金(然而有艾德曼合金装备的存在后,也没有必要痴迷于下届合金装备了吧)
        else if (Objects.equals(String.valueOf(this.inverseTag),"TagKey[minecraft:block / minecraft:incorrect_for_diamond_tool]"))
            cir.setReturnValue(800);
        else if (Objects.equals(String.valueOf(this.inverseTag),"TagKey[minecraft:block / minecraft:incorrect_for_netherite_tool]"))
            cir.setReturnValue(1000);
    }




}
