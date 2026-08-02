package com.equilibrium.entity;

//import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;

public interface ProduceManureOrSomething {
     static void produceManure(Animal entity){
        if (!entity.level().isClientSide && entity.isAlive() && !entity.isBaby()) {
            //肥料制造机器
//            entity.spawnAtLocation(FoodOrFarmItems.MANURE);
            entity.gameEvent(GameEvent.ENTITY_PLACE);
        }
    };

    static void produceFeather(Animal entity){
        if (!entity.level().isClientSide && entity.isAlive() && !entity.isBaby()) {
            //羽毛
            entity.spawnAtLocation(Items.FEATHER);
            entity.gameEvent(GameEvent.ENTITY_PLACE);
        }
    };



}
