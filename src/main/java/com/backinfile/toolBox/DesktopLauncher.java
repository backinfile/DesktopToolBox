package com.backinfile.toolBox;


import com.alibaba.fastjson.parser.ParserConfig;
import com.backinfile.toolBox.actor.Pet;
import com.backinfile.toolBox.manager.TrayManager;

import javax.swing.*;
import java.util.Properties;

public class DesktopLauncher {

    public static void main(String[] args) {
        ParserConfig.getGlobalInstance().setSafeMode(true);

        // 初始化资源
        Res.init();

        // 切换默认样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Log.game.error("", e);
        }

        Properties properties = System.getProperties();

        // 本地数据
        LocalData.load();

        // 显示宠物
        new Pet();

        // 显示托盘
        TrayManager.show();
    }

}