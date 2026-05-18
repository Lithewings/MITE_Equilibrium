package com.equilibrium.server_and_client.server.event;

import com.equilibrium.item.Metal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

import static com.equilibrium.item.food.WaterBowl.vanillaBowlItemUse;

public class OnItemUseEvent {
    public static final List<Item> CRYSTAL_LIST = List.of(Items.REDSTONE,Items.LAPIS_LAZULI,Items.QUARTZ,Items.EMERALD,Items.DIAMOND);
    public static final Map<Item,Integer> CRYSTAL_XP_MAP = Map.of(Items.REDSTONE,10,Items.LAPIS_LAZULI,25,Items.QUARTZ,50,Items.EMERALD,250,Items.DIAMOND,500);



    private static void onUseItemEffect(ItemStack itemStack, PlayerEntity player, World world, int experience) {
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
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

        // 生成物品的破碎粒子
        for (int i = 0; i < 10; i++) {
            double xOffset = (Math.random() - 0.5) * 0.85;  // 随机偏移
            double yOffset = (Math.random() - 0.5) * 0.85;
            double zOffset = (Math.random() - 0.5) * 0.85;

            // 使用 `ITEM` 粒子类型生成物品的破碎效果
            world.addParticle(particleEffect,
                    particlePos.x + xOffset, particlePos.y + yOffset, particlePos.z + zOffset,
                    0, 0, 0);  // 可根据需要调整粒子速度
        }
        //消耗一个物品
        itemStack.setCount(itemStack.getCount() - 1);
    }
    // 定义事件处理方法
    public static TypedActionResult<ItemStack> onUseItem(PlayerEntity player, World world, Hand hand) {
        // 获取玩家手中的物品
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.HAY_BLOCK)) {
            // 50级可以完整分解干草块
            if (player.experienceLevel >= 50) {
                return onUseHayBlockItem(itemStack, player, world, 0);
            }
        }

        // 判断是否为青金石等晶体
        if (player.experienceLevel <= 50 && CRYSTAL_LIST.contains(itemStack.getItem())) {
            return onUseCrystalItem(itemStack, player, world, CRYSTAL_XP_MAP.getOrDefault(itemStack.getItem(), 0));
        }
        else if(player.experienceLevel > 50 && CRYSTAL_LIST.contains(itemStack.getItem())){
            player.sendMessage(Text.of("通过打碎晶体获得的经验等级已达到上限(要求:玩家等级<=50级)"), true);
            return TypedActionResult.pass(itemStack);
        }


        if (itemStack.getItem() == Metal.ancient_metal) {
            //对于远古金属,无条件加经验
            return onUseCrystalItem(itemStack, player, world, 250);
        }






        if (itemStack.getItem() == Items.BOWL) {
            return vanillaBowlItemUse(world, player, hand, itemStack);
        }

        // 其他物品时不做处理
        return TypedActionResult.pass(itemStack);
    }
    public static TypedActionResult<ItemStack> onUseCrystalItem(ItemStack itemStack, PlayerEntity player, World world, int experience) {
        // 播放玻璃破碎的声音
        player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
        onUseItemEffect(itemStack, player, world, experience);
        return TypedActionResult.success(itemStack);

    }
    public static TypedActionResult<ItemStack> onUseHayBlockItem(ItemStack itemStack, PlayerEntity player, World world, int experience) {
        // 播放干草块破碎的声音
        player.playSound(SoundEvents.BLOCK_GRASS_BREAK, 1.0F, 1.0F);
        //物品使用效果(物品-1)
        onUseItemEffect(itemStack, player, world, experience);
        player.getInventory().offerOrDrop(new ItemStack(Items.WHEAT, 9));
        return TypedActionResult.success(itemStack);

    }

}
