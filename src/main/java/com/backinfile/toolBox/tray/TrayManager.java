package com.backinfile.toolBox.tray;

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
    private JPopupMenu popupMenu = null;

    public void show() {
        Log.game.info("show Tray");

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(Res.IMG_ICON, Res.STR_PROJ_NAME);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = getPopupMenu();
                    popupMenu.setLocation(e.getX() - popupMenu.getWidth(), e.getY() - popupMenu.getHeight());
                    popupMenu.setInvoker(getHideDialog());
                    popupMenu.setVisible(true);
                }
            }
        });
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
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
                JCheckBoxMenuItem item = new JCheckBoxMenuItem("移动到屏幕中央");
                item.addActionListener(e -> {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    Pet.getInstance().setLocationAlign(screenSize.width / 2, screenSize.height / 2);
                });
                item.setState(true);
                popupMenu.add(item);
            }

            popupMenu.addSeparator();


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

            // 退出按钮
            JMenuItem exitMenuItem = new JMenuItem(Res.STR_EXIT);
            exitMenuItem.addActionListener(e -> {
                System.exit(0);
            });

            popupMenu.add(exitMenuItem);
        }
        return popupMenu;
    }
}
