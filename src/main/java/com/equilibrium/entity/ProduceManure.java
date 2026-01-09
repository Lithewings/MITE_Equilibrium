package com.equilibrium.entity;

import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.event.GameEvent;

public interface ProduceManure {
     static void produceManure(AnimalEntity entity){
        if (!entity.getWorld().isClient && entity.isAlive() && !entity.isBaby()) {
            //肥料制造机器
            entity.dropItem(FoodOrFarmItems.MANURE);
            entity.emitGameEvent(GameEvent.ENTITY_PLACE);
        }
    };




}
