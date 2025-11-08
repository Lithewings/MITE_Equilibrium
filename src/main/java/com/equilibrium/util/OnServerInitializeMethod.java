package com.equilibrium.util;

import com.equilibrium.persistent_state.StateSaverAndLoader;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class OnServerInitializeMethod {


    private static final Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");

    public static boolean alwaysFalse() {
        return false;
    }

    public static boolean alwaysTrue() {
        return true;
    }


    public static void doNothing() {
    }

    public static void doNothing(Boolean aBoolean) {
    }




    // Logistic 曲线参数，可根据需求调整
    private static final double K = 10.0; // 陡峭度
    private static final double M = 0.5;  // 中点

    /**
     * logisticFunction(r, k, m):
     *   r: 线性比例 (0 ~ 1)
     *   k: 陡峭度 (越大曲线越陡)
     *   m: 中点 (0 ~ 1)
     */
    private static double logisticFunction(double r, double k, double m) {
        // 避免溢出，可做一些极值保护
        // 例如 r 超过 [0,1] 范围时先 clamp 到 [0,1]
        r = Math.max(0.0, Math.min(1.0, r));

        return 1.0 / (1.0 + Math.exp(-k * (r - m)));
    }
    public static int isPickAxeCrafted(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        boolean isVillageCanGenerate=serverState.isPickAxeCrafted && (context.getSource().getWorld().getTimeOfDay())/24000L>=10;
        if(isVillageCanGenerate){
            if(context.getSource().getEntity().isPlayer())
                context.getSource().getEntity().sendMessage(Text.of("村庄可以生成了"));}
        else{
            if(context.getSource().getEntity().isPlayer())
                context.getSource().getEntity().sendMessage(Text.of("村庄还不能生成"));
        }
        return 1;
    }


    public static TypedActionResult<ItemStack> onUseCrystalItem(ItemStack itemStack , PlayerEntity player, World world, int experience){

        // 播放玻璃破碎的声音
        player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
        //经验球获取的声音
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
        return TypedActionResult.success(itemStack);

    }









    //玩家护甲值下降,套装集齐效果
    public static Text updatePlayerArmor(PlayerEntity player) {
        ArrayList<ItemStack> armorItemList = new ArrayList<>();
        player.getArmorItems().forEach(element->{
                    if(element.getItem() instanceof ArmorItem)
                        armorItemList.add(element);
                }
        );
        //空护甲时直接返回
        if(armorItemList.isEmpty()) {
            player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(0.0D);
            return Text.of("The armor equipment is empty");
        }

        //由于耐久损耗,实际获得的护甲值
        double protection = 0;
        //最大护甲值,或者说满耐久护甲值
        double maxProtection = 0;

        for(ItemStack itemStack : armorItemList){
            if(itemStack.getItem() instanceof ArmorItem){
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
                if (durability==baseDurability) {
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
        double protectionReduction = maxProtection-protection;


        //设定玩家护甲
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(-protectionReduction);
        //拥有至少10点护甲时,获得抗性提升效果
        if(protection>10) {
            boolean hasResistance = player.hasStatusEffect(StatusEffects.RESISTANCE);
            if (!hasResistance)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 0, false, false, false));
            else if (player.getStatusEffect(StatusEffects.RESISTANCE).getDuration()<=20) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 0, false, false, false));
            }
        }


        Text text =Text.literal(String.format(
                "满耐久护甲=%.2f, 衰减系数=%.2f%%, 实际护甲=%.2f",
                maxProtection,
                100*(float)(1-protection/maxProtection),
                protection));

//		if(!player.getWorld().isClient())
//			player.sendMessage(text);
        return text;




        // 6. 可选：发送提示给玩家
//		if(!player.getWorld().isClient())
//			player.sendMessage(Text.literal(String.format(
//					"满耐久护甲=%.2f, 衰减系数=%.2f%%, 实际护甲=%.2f",
//					maxProtection,
//					100*(float)(1-protection/maxProtection),
//					protection
//			)), true);
    }



}
