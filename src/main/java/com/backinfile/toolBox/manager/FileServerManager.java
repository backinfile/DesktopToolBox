package com.backinfile.toolBox.manager;

import com.backinfile.toolBox.Res;
import com.backinfile.toolBox.actor.FileServerView;

public enum FileServerManager {
    Instance;

    private FileServerView fileServerView;

    public void open() {
        if (fileServerView == null) {
            fileServerView = new FileServerView();
        }
        fileServerView.setLocation(Res.SCREEN_SIZE.width / 2 - fileServerView.getWidth() / 2, Res.SCREEN_SIZE.height / 2 - fileServerView.getHeight() / 2);
        fileServerView.setVisible(true);
        fileServerView.toFront();

    }
}
