package com.backinfile.toolBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Utils2 {
    public static Point sub(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    public static Image loadImage(String path) {
        try {
            InputStream stream = Utils2.class.getClassLoader().getResourceAsStream(path);
            BufferedImage image = ImageIO.read(stream);
            return image;
        } catch (Exception e) {
            Log.game.error("loadImage", e);
        }
        return null;
    }

    public static float getClampRate(float ori, float target, float maxDistance) {
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


    static public short clamp(short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public long clamp(long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static boolean isFileOk(File file) {
        if (!file.exists()) {
            return false;
        }
        if (!file.canRead()) {
            return false;
        }
        if (!file.isDirectory()) {
            if (file.isHidden()) {
                return false;
            }
        }
        return true;
    }

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;

    public static String getFileSize(File file) {
        long length = file.length();
        if (length < KB) {
            return length + "B";
        }
        if (length < MB) {
            return String.format("%.2fKB", (length / KB + (length % KB) * 1f / KB));
        }
        if (length < GB) {
            return String.format("%.2fMB", (length / MB + (length % MB) * 1f / MB));
        }
        return String.format("%.2fGB", (length / GB + (length % GB) * 1f / GB));
    }
}
