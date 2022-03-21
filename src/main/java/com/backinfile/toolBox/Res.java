package com.backinfile.toolBox;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Res {
    public static final String STR_TITLE = "桌面宠物";
    public static final String STR_PROJ_NAME = "桌面宠物";
    public static final String STR_EXIT = "退出";
    public static final String PROJ_NAME = "DesktopToolBox";

    public static Dimension SCREEN_SIZE;
    public static final int CUBE_SIZE = 60;
    public static final int MENU_ICON_SIZE = 10;

    public static Image IMG_EMPTY;
    public static Image IMG_Check;
    public static Image IMG_ICON;


    public static void init() {
        SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();


        IMG_EMPTY = newEmptyImage();
        IMG_ICON = Utils2.loadImage("icon.png");
        IMG_Check = Utils2.loadImage("check.png").getScaledInstance(MENU_ICON_SIZE, MENU_ICON_SIZE, Image.SCALE_SMOOTH);

    }

    private static Image newEmptyImage() {
        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        return image;
    }
}
