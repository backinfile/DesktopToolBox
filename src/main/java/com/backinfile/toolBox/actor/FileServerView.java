package com.backinfile.toolBox.actor;

import com.backinfile.support.Time2;
import com.backinfile.support.Utils;
import com.backinfile.toolBox.GBC;
import com.backinfile.toolBox.manager.FileServerManager;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;

public class FileServerView extends JFrame {
    private static final int TEXT_FIELD_LENGTH = 60;
    private static final int TEXT_FIELD_ROW = 8;


    private JFileChooser fileChooser = null;
    private final JTextField filePathField;
    private final JButton startBtn;
    private final JTextArea logArea;
    private final LinkedList<String> logAreaTextList = new LinkedList<>();

    public FileServerView() {
        setTitle("文件共享");
        setResizable(false);

        filePathField = new JTextField(TEXT_FIELD_LENGTH);
        filePathField.setText(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());


        JButton chooseFolderBtn = new JButton("...");
        chooseFolderBtn.addActionListener(e -> {
            File file = chooseDirectory();
            if (file != null) {
                filePathField.setText(file.getAbsolutePath());
            }
        });

        logArea = new JTextArea(TEXT_FIELD_ROW, TEXT_FIELD_LENGTH);
        logArea.setAutoscrolls(true);
        logArea.setLineWrap(true);
        logArea.setEditable(false);
        JPanel logAreaPanel = new JPanel(new BorderLayout());
        logAreaPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        startBtn = new JButton("启动");
        startBtn.addActionListener(e -> {
            changeServerState(!FileServerManager.isServerOpened());
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(new JLabel("分享路径:"), new GBC(0, 0).setFill(GBC.BOTH));
        panel.add(filePathField, new GBC(1, 0, 3, 1).setFill(GBC.BOTH));
        panel.add(chooseFolderBtn, new GBC(4, 0).setFill(GBC.BOTH));
        panel.add(startBtn, new GBC(0, 1, 5, 1).setFill(GBC.BOTH).setInsets(1));
        panel.add(logAreaPanel, new GBC(0, 2, 5, TEXT_FIELD_ROW).setFill(GBC.BOTH).setInsets(1));
        add(panel);
        pack();
        panel.setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                changeServerState(false);
            }
        });

        // 注册日志事件
        FileServerManager.setServerLogListener(log -> {
//            logAreaTextList.add(log);
//            if (logAreaTextList.size() > TEXT_FIELD_ROW) {
//                logAreaTextList.removeFirst();
//            }
//            StringJoiner sj = new StringJoiner("\n");
//            for (String str : logAreaTextList) {
//                sj.add(str);
//            }

            String oldLog = logArea.getText();
            if (Utils.isNullOrEmpty(oldLog)) {
                logArea.setText(log);
            } else {
                logArea.setText(oldLog + "\n" + log);
            }
        });

        resetStartBtnStatus();
    }

    // state == true 启动
    private void changeServerState(boolean state) {
        if (!state) {
            startBtn.setText("启动");
            startBtn.setEnabled(true);
            FileServerManager.closeServer();
        } else {
            startBtn.setText("启动中...");
            startBtn.setEnabled(false);
            FileServerManager.openServer(filePathField.getText());

            Timer timer = new Timer((int) Time2.SEC / 2, event -> this.resetStartBtnStatus());
            timer.setRepeats(false);
            timer.start();
        }
    }


    private void resetStartBtnStatus() {
        startBtn.setEnabled(true);
        if (FileServerManager.isServerOpened()) {
            startBtn.setText("关闭");
        } else {
            startBtn.setText("启动");
        }
    }

    private File chooseDirectory() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        int state = fileChooser.showOpenDialog(this);
        if (state == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}
