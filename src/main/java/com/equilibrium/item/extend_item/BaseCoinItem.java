package com.equilibrium.item.extend_item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class BaseCoinItem extends Item {
    private final int experienceCost;

    private final Item convertItem;

    public int getExperienceCost() {
        return experienceCost;
    }

    public Item getConvertItem() {
        return convertItem;
    }
    public static TypedActionResult<ItemStack> onUseCrystalItem(ItemStack itemStack , PlayerEntity player,World world,int experience,Item convertItem){
        // 播放玻璃破碎的声音
        player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3F, 1.0F);
        // 返回成功，表示已处理

        //增加经验
        player.addExperience(experience);

        // 获取玩家眼前的方向和位置
        Vec3d eyePos = player.getEyePos();  // 玩家眼睛的位置
        Vec3d lookDir = player.getRotationVector();  // 玩家视线方向

        // 计算在玩家眼前的一定距离处的位置
        double distance = 0.5;  // 控制粒子生成的距离
        Vec3d particlePos = eyePos.add(lookDir.multiply(distance));

        // 创建物品材质的破碎粒子
        ItemStackParticleEffect particleEffect = new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack);

        // 生成青金石物品的破碎粒子
        for (int i = 0; i < 10; i++) {
            double xOffset = (Math.random() - 0.5) * 0.85;  // 随机偏移
            double yOffset = (Math.random() - 0.5) * 0.85;
            double zOffset = (Math.random() - 0.5) * 0.85;

            // 使用 `ITEM` 粒子类型生成青金石物品的破碎效果
            world.addParticle(particleEffect,
                    particlePos.x + xOffset, particlePos.y + yOffset, particlePos.z + zOffset,
                    0, 0, 0);  // 可根据需要调整粒子速度
        }
        //消耗一个晶体
        itemStack.setCount(itemStack.getCount()-1);
        //向玩家返还物品
        player.getInventory().offerOrDrop(convertItem.getDefaultStack());
        return TypedActionResult.success(itemStack);
    }



    //物品破碎,同时给玩家经验值
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        return onUseCrystalItem(itemStack,user,world,this.experienceCost,convertItem);
    }

    //注意:让自动合成器合成该物品是无法扣除玩家的经验值的,需要在合成器那边注入mixin逻辑
    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        player.addExperience(-this.experienceCost);
    }

    public BaseCoinItem(Settings settings, int experienceCost, Item convertItem) {
        super(settings);
        this.experienceCost=experienceCost;
        this.convertItem=convertItem;
    }



    /**
     * Called by the client to append tooltips to an item. Subclasses can override
     * this and add custom tooltips to {@code tooltip} list.
     *
     * @param stack
     * @param context
     * @param tooltip the list of tooltips to show
     * @param type
     */
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("每个"+this.experienceCost+"XP").formatted(Formatting.DARK_GRAY));
    }
}
