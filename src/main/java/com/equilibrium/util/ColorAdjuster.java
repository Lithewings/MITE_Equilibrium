package com.equilibrium.util;

public class ColorAdjuster {
    private static int color;

    // 设置颜色
    public static void setColor(int color) {
        ColorAdjuster.color = color;
    }

    // 增加红色通道的值
    public static int increaseRedComponent(int redIncrement) {
        int red = (color >> 16) & 0xFF;
        red = Math.min(255, red + redIncrement);
        return (color & 0x00FFFF) | (red << 16);
    }

    // 增加绿色通道的值
    public static int increaseGreenComponent(int greenIncrement) {
        int green = (color >> 8) & 0xFF;
        green = Math.min(255, green + greenIncrement);
        return (color & 0xFF00FF) | (green << 8);
    }

    // 增加蓝色通道的值
    public static int increaseBlueComponent(int blueIncrement) {
        int blue = color & 0xFF;
        blue = Math.min(255, blue + blueIncrement);
        return (color & 0xFFFF00) | blue;
    }

    // 调整颜色
    public static int adjustColor(int color, int redIncrement, int greenIncrement, int blueIncrement) {
        setColor(color);

        int newRed = increaseRedComponent(redIncrement);
        int newGreen = increaseGreenComponent(greenIncrement);
        int newBlue = increaseBlueComponent(blueIncrement);

        return newRed | newGreen | newBlue;
    }

    public static String getRGB(int color){
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return "red: "+red+"green: "+green+"blue: "+blue+" = "+String.format("#%06X", color);
    }
    public static int setRGB(int red,int green,int blue){

        return  (red << 16) | (green << 8) | blue;
    }
}


