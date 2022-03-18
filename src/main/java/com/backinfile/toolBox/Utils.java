package com.backinfile.toolBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

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

    public static String readAllFileText(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            Log.res.error("", e);
            return "";
        }
    }

    public static void writeFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)))) {
            writer.write(content);
        } catch (Exception e) {
            Log.res.error("", e);
        }
    }
}
