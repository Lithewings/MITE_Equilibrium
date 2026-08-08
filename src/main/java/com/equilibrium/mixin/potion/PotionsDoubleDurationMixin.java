package com.equilibrium.mixin.potion;

import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Potions.class)
//对夜视、速度、力量

//1. 常量 3600
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	night_vision	夜视	3600
//1	invisibility	隐身	3600
//2	leaping	跳跃提升	3600
//3	fire_resistance	防火	3600
//4	swiftness	速度	3600
//5	water_breathing	水下呼吸	3600
//6	strength	力量	3600
//7	wind_charged	充能风	3600
//8	weaving	编织	3600
//9	oozing	渗浆	3600
//10	infested	寄生	3600
//2. 常量 9600
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	long_night_vision	夜视	9600
//1	long_invisibility	隐身	9600
//2	long_leaping	跳跃提升	9600
//3	long_fire_resistance	防火	9600
//4	long_swiftness	速度	9600
//5	long_water_breathing	水下呼吸	9600
//6	long_strength	力量	9600
//3. 常量 1800
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	strong_leaping	跳跃提升	1800
//1	strong_swiftness	速度	1800
//2	slowness	缓慢	1800
//3	long_poison	中毒	1800
//4	long_regeneration	生命恢复	1800
//5	strong_strength	力量	1800
//6	weakness	虚弱	1800
//7	slow_falling	缓降	1800
//4. 常量 400
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	strong_slowness	缓慢	400
//1	turtle_master (缓慢)	缓慢	400
//2	turtle_master (抗性)	抗性提升	400
//3	strong_turtle_master (缓慢)	缓慢	400
//4	strong_turtle_master (抗性)	抗性提升	400
//5. 常量 800
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	long_turtle_master (缓慢)	缓慢	800
//1	long_turtle_master (抗性)	抗性提升	800
//6. 常量 4800
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	long_slowness	缓慢	4800
//1	long_weakness	虚弱	4800
//2	long_slow_falling	缓降	4800
//7. 常量 900
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	poison	中毒	900
//1	regeneration	生命恢复	900
//8. 常量 1（ICONST_1，用于瞬间效果）
//出现序号 (ordinal)	药水名称	效果类型	原持续时间（ticks）
//0	healing	瞬间治疗	1
//1	strong_healing	瞬间治疗	1
//2	harming	瞬间伤害	1
//3	strong_harming	瞬间伤害	1
//9. 其他唯一常量
//常量值	出现序号	药水名称	效果类型	原持续时间（ticks）
//432	0	strong_poison	中毒	432
//450	0	strong_regeneration	生命恢复	450
//6000	0	luck	幸运	6000




public abstract class PotionsDoubleDurationMixin {

    // ======================== 3600 → 7200 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 0))
    private static int doubleNightVision(int value) { return 7200; }          // 夜视

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 1))
    private static int doubleInvisibility(int value) { return 7200; }         // 隐身

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 2))
    private static int doubleLeaping(int value) { return 7200; }              // 跳跃提升

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 3))
    private static int doubleFireResistance(int value) { return 7200; }       // 防火

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 4))
    private static int doubleSwiftness(int value) { return 7200; }            // 速度

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 5))
    private static int doubleWaterBreathing(int value) { return 7200; }       // 水下呼吸

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 6))
    private static int doubleStrength(int value) { return 7200; }             // 力量

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 7))
    private static int doubleWindCharged(int value) { return 7200; }          // 充能风

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 8))
    private static int doubleWeaving(int value) { return 7200; }              // 编织

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 9))
    private static int doubleOozing(int value) { return 7200; }               // 渗浆

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 3600, ordinal = 10))
    private static int doubleInfested(int value) { return 7200; }             // 寄生

    // ======================== 9600 → 19200 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 0))
    private static int doubleLongNightVision(int value) { return 19200; }     // 延长夜视

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 1))
    private static int doubleLongInvisibility(int value) { return 19200; }    // 延长隐身

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 2))
    private static int doubleLongLeaping(int value) { return 19200; }         // 延长跳跃

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 3))
    private static int doubleLongFireResistance(int value) { return 19200; }  // 延长防火

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 4))
    private static int doubleLongSwiftness(int value) { return 19200; }       // 延长速度

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 5))
    private static int doubleLongWaterBreathing(int value) { return 19200; }  // 延长水下呼吸

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 9600, ordinal = 6))
    private static int doubleLongStrength(int value) { return 19200; }        // 延长力量

    // ======================== 1800 → 3600 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 0))
    private static int doubleStrongLeaping(int value) { return 3600; }        // 强效跳跃

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 1))
    private static int doubleStrongSwiftness(int value) { return 3600; }      // 强效速度

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 2))
    private static int doubleSlowness(int value) { return 3600; }             // 缓慢

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 3))
    private static int doubleLongPoison(int value) { return 3600; }           // 延长中毒

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 4))
    private static int doubleLongRegeneration(int value) { return 3600; }     // 延长生命恢复

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 5))
    private static int doubleStrongStrength(int value) { return 3600; }       // 强效力量

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 6))
    private static int doubleWeakness(int value) { return 3600; }             // 虚弱

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1800, ordinal = 7))
    private static int doubleSlowFalling(int value) { return 3600; }          // 缓降

    // ======================== 400 → 800 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 400, ordinal = 0))
    private static int doubleStrongSlowness(int value) { return 800; }        // 强效缓慢

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 400, ordinal = 1))
    private static int doubleTurtleMasterSlowness(int value) { return 800; }  // 神龟缓慢

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 400, ordinal = 2))
    private static int doubleTurtleMasterResistance(int value) { return 800; }// 神龟抗性

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 400, ordinal = 3))
    private static int doubleStrongTurtleMasterSlowness(int value) { return 800; }// 强效神龟缓慢

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 400, ordinal = 4))
    private static int doubleStrongTurtleMasterResistance(int value) { return 800; }// 强效神龟抗性

    // ======================== 800 → 1600 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 800, ordinal = 0))
    private static int doubleLongTurtleMasterSlowness(int value) { return 1600; }// 延长神龟缓慢

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 800, ordinal = 1))
    private static int doubleLongTurtleMasterResistance(int value) { return 1600; }// 延长神龟抗性

    // ======================== 4800 → 9600 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 4800, ordinal = 0))
    private static int doubleLongSlowness(int value) { return 9600; }         // 延长缓慢

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 4800, ordinal = 1))
    private static int doubleLongWeakness(int value) { return 9600; }         // 延长虚弱

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 4800, ordinal = 2))
    private static int doubleLongSlowFalling(int value) { return 9600; }      // 延长缓降

    // ======================== 900 → 1800 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 900, ordinal = 0))
    private static int doublePoison(int value) { return 1800; }               // 中毒

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 900, ordinal = 1))
    private static int doubleRegeneration(int value) { return 1800; }         // 生命恢复

    // ======================== 432 → 864 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 432, ordinal = 0))
    private static int doubleStrongPoison(int value) { return 864; }          // 强效中毒

    // ======================== 450 → 900 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 450, ordinal = 0))
    private static int doubleStrongRegeneration(int value) { return 900; }    // 强效生命恢复

    // ======================== 6000 → 12000 ========================
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 6000, ordinal = 0))
    private static int doubleLuck(int value) { return 12000; }                // 幸运

    // ======================== 1 (ICONST_1) → 1 ========================
//    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1, ordinal = 0))
//    private static int doubleHealing(int value) { return 1; }                // 瞬间治疗
//
//    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1, ordinal = 1))
//    private static int doubleStrongHealing(int value) { return 1; }          // 强效瞬间治疗
//
//    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1, ordinal = 2))
//    private static int doubleHarming(int value) { return 1; }                // 瞬间伤害
//
//    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1, ordinal = 3))
//    private static int doubleStrongHarming(int value) { return 1; }          // 强效瞬间伤害
}

