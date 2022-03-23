package com.backinfile.toolBox.actor;

import com.backinfile.toolBox.manager.GBC;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

public class FileServerView extends JFrame {
    private JFileChooser fileChooser = null;

    public FileServerView() {
        setTitle("文件共享");

        JButton startBtn = new JButton("启动");


        JTextField filePathField = new JTextField(40);
        filePathField.setText(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());

        JButton chooseFolderBtn = new JButton("...");
        chooseFolderBtn.addActionListener(e -> {
            File file = chooseDirectory();
            if (file != null) {
                filePathField.setText(file.getAbsolutePath());
            }
        });

        JTextArea logArea = new JTextArea(5, 40);
        logArea.setLineWrap(false);
        logArea.setEditable(false);
        logArea.setText("line 1\nline 2");

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(filePathField, new GBC(0, 0, 4, 1).setFill(GBC.HORIZONTAL));
        panel.add(chooseFolderBtn, new GBC(4, 0).setFill(GBC.BOTH));
        panel.add(startBtn, new GBC(0, 1, 5, 1).setFill(GBC.BOTH));
        panel.add(logArea, new GBC(0, 2, 5, 5).setFill(GBC.BOTH));
        add(panel);
        pack();
        //JOptionPane.showMessageDialog
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
