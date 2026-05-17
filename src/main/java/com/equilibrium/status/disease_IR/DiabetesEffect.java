package com.equilibrium.status.disease_IR;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;

public class DiabetesEffect {

    
    public static int getProgress(int diabetes){
        int progress = -1;
        if(diabetes>=90000)
            progress=3;
        else if (diabetes>=72000)
            progress=2;
        else if(diabetes>=60000)
            progress=1;
        else if(diabetes>=48000)
            progress=0;
        return progress;
    }
    public static void tryApplyDiabetesEffect(PlayerEntity playerEntity, int diabetes){

        StatusEffectInstance BLINDNESS= new StatusEffectInstance(StatusEffects.BLINDNESS,1200, 255, false, false, false);
        StatusEffectInstance NAUSEA= new StatusEffectInstance(StatusEffects.NAUSEA, 600, 255, false, false, false);
        StatusEffectInstance WEAKNESS= new StatusEffectInstance(StatusEffects.WEAKNESS, 600, 255, false, false, false);
        StatusEffectInstance POISON= new StatusEffectInstance(StatusEffects.POISON, 80, 0, false, false, false);


        int progress = getProgress(diabetes);
        ArrayList<StatusEffectInstance> effectInstanceList = new ArrayList<>();

        //覆盖式施加效果
        if(progress==3){
            effectInstanceList.add(BLINDNESS);
            effectInstanceList.add(WEAKNESS);
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        }
        else if (progress == 2) {
            effectInstanceList.add(WEAKNESS);
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        } else if (progress == 1) {
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        } else if (progress == 0) {
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        }
        effectInstanceList.forEach(effect-> playerEntity.setStatusEffect(effect,null));
    }
}
