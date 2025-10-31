package com.equilibrium.util;

import java.util.HashMap;
import java.util.Map;

public class XpHashMap {

    private static final Map<Integer, Integer> defaultEntityXpMap =
                    Map.of(1,10,
                            2,50,
                            3,100,
                            4,200,
                            5,500);







    private static final HashMap<Integer, Integer> entityXpMap = new HashMap<>();

    public static int getXpForLevel(int monsterLevel) {
        return entityXpMap.getOrDefault(monsterLevel, 0);
    }

    public static void setXpForLevel(int monsterLevel, int xp) {
        entityXpMap.put(monsterLevel, xp);
    }

    public static boolean hasXpForLevel(int monsterLevel) {
        return entityXpMap.containsKey(monsterLevel);
    }

    public static void removeXpForLevel(int monsterLevel) {
        entityXpMap.remove(monsterLevel);
    }

    public static void clearAllXpSettings() {
        entityXpMap.clear();
    }

    public static HashMap<Integer, Integer> getAllXpSettings() {
        return new HashMap<>(entityXpMap);
    }

    public static int getSize() {
        return entityXpMap.size();
    }

    public static boolean isEmpty() {
        return entityXpMap.isEmpty();
    }
}