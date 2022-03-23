package com.backinfile.toolBox.manager;

import com.backinfile.support.Utils;
import com.backinfile.support.func.Action1;
import com.backinfile.toolBox.Res;
import com.backinfile.toolBox.actor.FileServerView;
import com.backinfile.toolBox.net.FileServer;

import javax.swing.*;
import java.awt.*;

public class FileServerManager {
    private static FileServerView fileServerView;
    private static final FileServer fileServer = new FileServer();

    public static void openWindow() {
        if (fileServerView == null) {
            fileServerView = new FileServerView();
            fileServerView.setLocation(Res.SCREEN_SIZE.width / 2 - fileServerView.getWidth() / 2, Res.SCREEN_SIZE.height / 2 - fileServerView.getHeight() / 2);
        }
        fileServerView.setState(Frame.NORMAL);
        fileServerView.setVisible(true);
        fileServerView.toFront();
    }

    public static void openServer(String path) {
        String result = fileServer.start(path);
        if (!Utils.isNullOrEmpty(result)) {
            if (fileServerView != null && fileServerView.isVisible()) {
                JOptionPane.showMessageDialog(fileServerView, result);
            } else {
                JOptionPane.showMessageDialog(null, result);
            }
        }
    }

    public static boolean isServerOpened() {
        return fileServer.isAlive();
    }


    public static void closeServer() {
        fileServer.stop();
    }

    public static void setServerLogListener(Action1<String> callback) {
        fileServer.clearLogOutput();
        fileServer.addLogOutput(callback);
    }
}
