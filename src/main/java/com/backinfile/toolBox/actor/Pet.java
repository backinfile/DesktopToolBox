package com.backinfile.toolBox.actor;

import com.backinfile.toolBox.LocalData;
import com.backinfile.toolBox.Res;
import com.backinfile.toolBox.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Pet extends JDialog implements ActionListener {
    private static Pet instance;

    public static Pet getInstance() {
        return instance;
    }

    // 0,0表示在原位置
    public float eyeOffsetX = 0;
    public float eyeOffsetY = 0;

    private final float eyeOffsetDistance;

    public Pet() {
        instance = this;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setSize(Res.CUBE_SIZE, Res.CUBE_SIZE);

        if (LocalData.instance().locationX >= 0) {
            setLocationAlign(LocalData.instance().locationX, LocalData.instance().locationY);
        } else {
            setLocationAlign(screenSize.width / 2, screenSize.height / 2);
        }

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setType(Type.UTILITY);
        setAlwaysOnTop(true);
        setVisible(true);

        eyeOffsetDistance = screenSize.height / 2f;


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocationAlign(x, y);
            }
        });


        // 更新鼠标
        new Timer(Res.UpdateTimeUnit, e -> {
            if (isVisible()) {
                if (updateEyeOffset()) {
                    repaint();
                }
            }
        }).start();
    }

    public void setLocationAlign(int x, int y) {
        setLocation(x - getWidth() / 2, y - getHeight() / 2);

        LocalData.instance().locationX = x;
        LocalData.instance().locationY = y;
        LocalData.instance().saveLater();
    }

    private boolean updateEyeOffset() {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        Point location = getLocationOnScreen();

        float oldEyeOffsetX = eyeOffsetX;
        float oldEyeOffsetY = eyeOffsetY;
        eyeOffsetX = Utils.invLerp(location.x, mouseLocation.x, eyeOffsetDistance);
        eyeOffsetY = Utils.invLerp(location.y, mouseLocation.y, eyeOffsetDistance);

        if (Math.abs(oldEyeOffsetX - eyeOffsetX) + Math.abs(oldEyeOffsetY - eyeOffsetY) >= 0.01f) {
//            Log.game.info("mouse {} location: {}", mouseLocation.toString(), location.toString());
//            Log.game.info("update eye offset {},{}", eyeOffsetX, eyeOffsetY);
            return true;
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int size = Res.CUBE_SIZE;

        g2d.setColor(new Color(0.7f, 0.1f, 0.1f, 1f));
        g2d.fillRect(0, 0, size, size);
        g2d.setColor(Color.BLACK);

        int offsetX = Math.round(eyeOffsetX * size / 10);
        int offsetY = Math.round(eyeOffsetY * size / 10);
        drawEye(g2d, size * 5 / 16 + offsetX, size * 3 / 8 + offsetY, size * 3 / 16);
        drawEye(g2d, size * 11 / 16 + offsetX, size * 3 / 8 + offsetY, size * 3 / 16);
//        Log.game.info("paint");
    }

    private void drawEye(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x - size * 5 / 8, y - size / 12, size * 5 / 4, size / 6);
        g2d.fillRect(x - size / 2, y + size / 2, size, size);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
