package com.equilibrium.entity;

import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Items;
import net.minecraft.world.event.GameEvent;

public interface ProduceManureOrSomething {
     static void produceManure(AnimalEntity entity){
        if (!entity.getWorld().isClient && entity.isAlive() && !entity.isBaby()) {
            //肥料制造机器
            entity.dropItem(FoodOrFarmItems.MANURE);
            entity.emitGameEvent(GameEvent.ENTITY_PLACE);
        }
    };

    static void produceFeather(AnimalEntity entity){
        if (!entity.getWorld().isClient && entity.isAlive() && !entity.isBaby()) {
            //羽毛
            entity.dropItem(Items.FEATHER);
            entity.emitGameEvent(GameEvent.ENTITY_PLACE);
        }
    };



}
