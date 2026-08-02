package com.equilibrium.item.tools_attribute;

import net.minecraft.world.entity.player.Player;

public class ExtraDamageFromExperienceLevel {
    public static final float CEILING = 2F;
    public static float getDamageLevel(Player player){
        if(player.experienceLevel<=5)
            return (float) Math.min((1+player.experienceLevel * 0.05), CEILING);
        else
            return (float) Math.min((1.25+player.experienceLevel * 0.01), CEILING);
    }
    public static float getDamageLevel(int experienceLevel){
        if(experienceLevel<=5)
            return (float) Math.min((1+experienceLevel * 0.05), CEILING);
        else
            return (float) Math.min((1.25+experienceLevel * 0.01), CEILING);
    }
}
