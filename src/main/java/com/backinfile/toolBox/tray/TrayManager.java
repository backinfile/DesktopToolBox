package com.backinfile.toolBox.tray;

import com.backinfile.toolBox.Log;
import com.backinfile.toolBox.Res;
import com.backinfile.toolBox.actor.Pet;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

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
                    popupMenu.setLocation(e.getX(), e.getY());
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
                ImageIcon icon = new ImageIcon(Res.IMG_Check);
                JMenuItem item = new JMenuItem("移动到屏幕中央", icon);
                item.setIcon(null);
                item.addActionListener(e -> {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    Pet.getInstance().setLocationAlign(screenSize.width / 2, screenSize.height / 2);
                });
                popupMenu.add(item);
            }

            popupMenu.addSeparator();

            // 测试按钮
            JMenuItem testMenuItem = new JMenuItem("test");

            // 退出按钮
            JMenuItem exitMenuItem = new JMenuItem(Res.STR_EXIT);
            exitMenuItem.addActionListener(e -> {
                System.exit(0);
            });

            popupMenu.add(testMenuItem);
            popupMenu.add(exitMenuItem);
        }
        return popupMenu;
    }
}
