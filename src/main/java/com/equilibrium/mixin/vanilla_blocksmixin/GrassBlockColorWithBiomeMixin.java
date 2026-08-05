package com.equilibrium.mixin.vanilla_blocksmixin;

import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.util.ColorAdjuster;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class GrassBlockColorWithBiomeMixin {
    @Shadow
    public static final ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;


    @Inject(method = "getAverageGrassColor",at = @At("HEAD"), cancellable = true)
    private static void getGrassColor(BlockAndTintGetter world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(getModGrassColor(world, pos, GRASS_COLOR_RESOLVER));
    }

    @Shadow
    private static int getAverageColor(BlockAndTintGetter world, BlockPos pos, ColorResolver resolver) {
        return world.getBlockTint(pos, resolver);
    }


    @Unique
    private static int count = 0;
    @Unique
    private static int getModGrassColor(BlockAndTintGetter world, BlockPos pos, ColorResolver resolver) {
        //服务端发送包成功后,客户端就可以查看最新的并发哈希表并get数值
        //逻辑上等同于将最新的哈希表送给客户端,这个哈希表代码一致,但分为客户端实例和服务端实例,两者同步需要服务端发送网络包

        //这里的get方法,get的是客户端的ConcurrentHashMap
        int polluteLevel = S2CStockChangeGrassColorPacket.GrassColorPayload.getPolluteLevel(pos);
        int originColor = getAverageColor(world, pos, GRASS_COLOR_RESOLVER);

    // 根据污染程度直接使用ColorAdjuster调整
        int finalColor =  switch (polluteLevel) {
            case 1 -> ColorAdjuster.setRGB(160,170,90);
            case 2 -> ColorAdjuster.setRGB(163,170,88);
            case 3 -> ColorAdjuster.setRGB(168,170,86);
            case 4 -> ColorAdjuster.setRGB(172,170,84);
            case 5 -> ColorAdjuster.setRGB(176,170,82);
            case 6 -> ColorAdjuster.setRGB(178,170,81);
            case 7 -> ColorAdjuster.setRGB(180,170,80);
            default -> originColor; // 污染程度为0
        };

//        MITEequilibrium.LOGGER.info("The origin color is :"+getRGB(originColor));
//        MITEequilibrium.LOGGER.info("The final color is :"+getRGB(finalColor));
//        MITEequilibrium.LOGGER.info("The Map Length is :"+BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.size());
//        BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.forEach((blockPos, mapPolluteLevel) -> {
//            if(mapPolluteLevel!=0)
//                MITEequilibrium.LOGGER.info("The BlockPos is :"+blockPos);
//        });
        return finalColor;

    }



}
