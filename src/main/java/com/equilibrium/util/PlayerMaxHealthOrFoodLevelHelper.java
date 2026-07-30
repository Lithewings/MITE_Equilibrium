package com.equilibrium.util;


import net.minecraft.world.entity.player.Player;

public class PlayerMaxHealthOrFoodLevelHelper {


    //返回玩家这里的经验最大值
    public static int getMaxHealthOrFoodLevel(Player player) {
        return player.experienceLevel >= 35 ? 20 : 6 + (int)(( player.experienceLevel/ 5) * 2);
    }

}
