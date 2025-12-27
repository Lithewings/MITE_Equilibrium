package com.equilibrium.mixin.vanilla_blocksmixin;

import com.equilibrium.MITEequilibrium;
import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.util.ColorAdjuster;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.MITEequilibrium.GRASSBLOCK_POLLUTED;
import static com.equilibrium.network.S2CStockChangeGrassColorPacket.BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP;
import static com.equilibrium.util.ColorAdjuster.getRGB;

@Mixin(BiomeColors.class)
public class GrassBlockColorWithBiomeMixin {
    @Shadow
    public static final ColorResolver GRASS_COLOR = Biome::getGrassColorAt;


    @Inject(method = "getGrassColor",at = @At("HEAD"), cancellable = true)
    private static void getGrassColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(getModGrassColor(world, pos, GRASS_COLOR));
    }

    @Shadow
    private static int getColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
        return world.getColor(pos, resolver);
    }


    @Unique
    private static int count = 0;
    @Unique
    private static int getModGrassColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
        //服务端发送包成功后,客户端就可以查看最新的并发哈希表并get数值
        //逻辑上等同于将最新的哈希表送给客户端,这个哈希表代码一致,但分为客户端实例和服务端实例,两者同步需要服务端发送网络包

        //这里的get方法,get的是客户端的ConcurrentHashMap
        int polluteLevel = S2CStockChangeGrassColorPacket.GrassColorPayload.getPolluteLevel(pos);
        int originColor = getColor(world, pos, GRASS_COLOR);

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
