package com.backinfile.toolBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class Utils {
    public static Point sub(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    public static Image loadImage(String path) {
        try {
            InputStream stream = Utils.class.getClassLoader().getResourceAsStream(path);
            BufferedImage image = ImageIO.read(stream);
            return image;
        } catch (Exception e) {
            Log.game.error("loadImage", e);
        }
        return null;
    }

    public static float invLerp(float ori, float target, float maxDistance) {
        float subValue = target - ori;
        float absValue = Math.abs(subValue);
        float signumValue = Math.signum(subValue);
        if (absValue >= maxDistance) {
            return signumValue;
        }
        if (absValue < 0.0001f) {
            return 0;
        }
        return absValue * signumValue / maxDistance;
    }
}
