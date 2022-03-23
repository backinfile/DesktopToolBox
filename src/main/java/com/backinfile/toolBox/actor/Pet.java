package com.backinfile.toolBox.actor;

import com.backinfile.support.Time2;
import com.backinfile.support.Utils;
import com.backinfile.toolBox.Config;
import com.backinfile.toolBox.LocalData;
import com.backinfile.toolBox.Res;
import com.backinfile.toolBox.Utils2;

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

    private Timer shakeTimer = null;

    private final float eyeOffsetDistance;
    private final int move_edge_left = Res.CUBE_SIZE / 2;
    private final int move_edge_right = Res.SCREEN_SIZE.width - Res.CUBE_SIZE / 2;
    private final int move_edge_top = Res.CUBE_SIZE / 2;
    private final int move_edge_bottom = Res.SCREEN_SIZE.height - Res.CUBE_SIZE / 2;

    public Pet() {
        instance = this;
        setTitle(Res.PROJ_NAME);
        setSize(Res.CUBE_SIZE, Res.CUBE_SIZE);

        // 加载时读取上一次位置信息
        if (LocalData.instance().locationX >= 0) {
            setLocationAlignCenter(LocalData.instance().locationX, LocalData.instance().locationY);
        } else {
            setLocationAlignCenter(Res.SCREEN_SIZE.width / 2, Res.SCREEN_SIZE.height / 2);
        }

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setType(Type.UTILITY);
        setAlwaysOnTop(true);
        setVisible(LocalData.instance().showPet);

        eyeOffsetDistance = Res.SCREEN_SIZE.height / 2f;


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocationAlignCenter(Utils2.clamp(x, move_edge_left, move_edge_right), Utils2.clamp(y, move_edge_top, move_edge_bottom));
            }
        });


        // 更新鼠标
        new Timer(Config.TIME_DELTA, e -> {
            if (isVisible()) {
                if (updateEyeOffset()) {
                    repaint();
                }
            }
        }).start();
    }

    public void setPetVisibleState(boolean curState) {
        setVisible(curState);
        LocalData.instance().showPet = curState;
        LocalData.instance().save();
    }


    public void shake() {
        if (!isVisible()) {
            return;
        }
        if (shakeTimer == null) {
            final long endTime = Time2.getCurMillis() + Time2.SEC / 3;
            final Point startLocation = getLocationOnScreen();
            shakeTimer = new Timer(Config.TIME_DELTA, e -> {
                if (Time2.getCurMillis() > endTime || !isVisible()) {
                    setLocation(startLocation);
                    shakeTimer.stop();
                    shakeTimer = null;
                } else {
                    setLocation(getShakePoint(startLocation));
                }
            });
            shakeTimer.start();
        }
    }

    private Point getShakePoint(Point point) {
        final int distance = 3;
        Point result = new Point(point);
        result.x += Utils.nextInt(-distance, distance + 1);
        return result;
    }


    public void setLocationAlignCenter(int x, int y) {
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
        eyeOffsetX = Utils2.getClampRate(location.x, mouseLocation.x, eyeOffsetDistance);
        eyeOffsetY = Utils2.getClampRate(location.y, mouseLocation.y, eyeOffsetDistance);

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
