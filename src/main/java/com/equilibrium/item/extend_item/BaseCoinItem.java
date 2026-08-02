package com.equilibrium.item.extend_item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    public static InteractionResultHolder<ItemStack> onUseCrystalItem(ItemStack itemStack , Player player,Level world,int experience,Item convertItem){
        // 播放玻璃破碎的声音
        player.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3F, 1.0F);
        // 返回成功，表示已处理

        //增加经验
        player.giveExperiencePoints(experience);

        // 获取玩家眼前的方向和位置
        Vec3 eyePos = player.getEyePosition();  // 玩家眼睛的位置
        Vec3 lookDir = player.getLookAngle();  // 玩家视线方向

        // 计算在玩家眼前的一定距离处的位置
        double distance = 0.5;  // 控制粒子生成的距离
        Vec3 particlePos = eyePos.add(lookDir.scale(distance));

        // 创建物品材质的破碎粒子
        ItemParticleOption particleEffect = new ItemParticleOption(ParticleTypes.ITEM, itemStack);

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
        player.getInventory().placeItemBackInInventory(convertItem.getDefaultInstance());
        return InteractionResultHolder.success(itemStack);
    }



    //物品破碎,同时给玩家经验值
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(user.experienceLevel<=50) {
            return onUseCrystalItem(itemStack, user, world, this.experienceCost, convertItem);
        }else {
            //大于50级就不可以使用硬币了
            user.displayClientMessage(Component.nullToEmpty("通过打碎硬币获得的经验等级已达到上限(要求:玩家等级<=50级)"), true);
            return InteractionResultHolder.fail(itemStack);
        }


    }

    //注意:让自动合成器合成该物品是无法扣除玩家的经验值的,需要在合成器那边注入mixin逻辑
    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        player.giveExperiencePoints(-this.experienceCost);
    }

    public BaseCoinItem(Properties settings, int experienceCost, Item convertItem) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.literal("每个"+this.experienceCost+"XP").withStyle(ChatFormatting.DARK_GRAY));
    }
}
