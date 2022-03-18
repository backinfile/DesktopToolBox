package com.backinfile.toolBox;

import java.awt.*;

public class Res {
    public static final String STR_TITLE = "桌面宠物";
    public static final String STR_PROJ_NAME = "桌面宠物";
    public static final String STR_EXIT = "退出" + STR_PROJ_NAME;

    public static final int CUBE_SIZE = 60;
    public static final int MENU_ICON_SIZE = 10;

    public static final int UpdateTimeUnit = 1000 / 60;

    public static Image IMG_Check;
    public static Image IMG_ICON;


    public static void init() {
        IMG_ICON = Utils.loadImage("icon.png");
        IMG_Check = Utils.loadImage("check.png").getScaledInstance(MENU_ICON_SIZE, MENU_ICON_SIZE, Image.SCALE_SMOOTH);

    }
//        BufferedImage image = new BufferedImage(CUBE_SIZE, CUBE_SIZE, BufferedImage.TYPE_INT_ARGB);

}
