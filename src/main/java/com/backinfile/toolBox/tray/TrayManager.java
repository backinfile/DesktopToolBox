package com.backinfile.toolBox.tray;

import com.backinfile.support.Time2;
import com.backinfile.toolBox.LocalData;
import com.backinfile.toolBox.Log;
import com.backinfile.toolBox.Res;
import com.backinfile.toolBox.actor.Pet;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

public enum TrayManager {
    Instance;

    private JDialog hideDialog = null;
    private TrayIcon trayIcon = null;

    private Timer winkTimer = null;
    private boolean winking = false;


    private JPopupMenu popupMenu = null;
    private JCheckBoxMenuItem showPetMenu = null;

    public void show() {
        Log.game.info("show Tray");

        SystemTray tray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(Res.IMG_ICON, Res.STR_PROJ_NAME);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = getPopupMenu();
                    popupMenu.setLocation(e.getX() - popupMenu.getWidth(), e.getY() - popupMenu.getHeight());
                    popupMenu.setInvoker(getHideDialog());
                    popupMenu.setVisible(true);
                }

                if (e.getButton() == MouseEvent.BUTTON1) {
                    Pet.getInstance().shake();
                }
            }
        });
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void wink(boolean wink) {
        winking = wink;

        if (winking && winkTimer == null) {
            boolean[] winkData = new boolean[]{false};
            winkTimer = new Timer((int) Time2.SEC / 2, e -> {
                if (!winking) {
                    trayIcon.setImage(Res.IMG_ICON);
                    winkTimer.stop();
                    winkTimer = null;
                    return;
                }

                trayIcon.setImage(winkData[0] ? Res.IMG_ICON : Res.IMG_EMPTY);
                winkData[0] = !winkData[0];
            });
            winkTimer.start();
        }
    }

    public JDialog getHideDialog() {
        if (hideDialog != null) {
            hideDialog.setVisible(false);
            hideDialog.dispose();
            hideDialog = null;
        }
        hideDialog = new JDialog();
        hideDialog.setUndecorated(true);
        hideDialog.setSize(1, 1);
        hideDialog.setBackground(new Color(0, 0, 0, 0));
        hideDialog.setVisible(true);
        return hideDialog;
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            // 控制宠物
            {
                // 控制显示
                {
                    showPetMenu = new JCheckBoxMenuItem("显示宠物");
                    showPetMenu.addActionListener(e -> {
                        Pet.getInstance().setPetVisibleState(!LocalData.instance().showPet);
                    });
                    popupMenu.add(showPetMenu);
                }
                // 移动
                {
                    JMenuItem item = new JMenuItem("移动到屏幕中央");
                    item.addActionListener(e -> {
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        Pet.getInstance().setLocationAlignCenter(screenSize.width / 2, screenSize.height / 2);
                    });
                    popupMenu.add(item);
                }
                popupMenu.addSeparator();
            }


            // 工具
            {
                // 系统工具
                {
                    String[] systemTools = new String[]{
                            "计算器", "calc",
                            "远程连接", "mstsc",
                            "截图工具", "SnippingTool",
                    };
                    JMenu systemMenu = new JMenu("系统工具");
                    popupMenu.add(systemMenu);

                    for (int i = 0; i < systemTools.length; i += 2) {
                        JMenuItem menuItem = new JMenuItem(systemTools[i]);
                        String name = systemTools[i + 1];
                        menuItem.addActionListener(e -> {
                            try {
                                Runtime.getRuntime().exec(name);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        });
                        systemMenu.add(menuItem);
                    }
                }
            }
            popupMenu.addSeparator();

            // 退出按钮
            JMenuItem exitMenuItem = new JMenuItem(Res.STR_EXIT);
            exitMenuItem.addActionListener(e -> {
                System.exit(0);
            });

            popupMenu.add(exitMenuItem);
        }


        // 更新数据
        showPetMenu.setState(LocalData.instance().showPet);

        return popupMenu;
    }
}
