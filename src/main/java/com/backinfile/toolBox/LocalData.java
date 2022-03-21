package com.backinfile.toolBox;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.backinfile.support.Time2;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class LocalData {
    private static LocalData localData = null;
    private static final String LOCAL_DATA_PATH = "\\" + Res.PROJ_NAME + "\\save.json";

    public static LocalData instance() {
        if (localData == null) {
            load();
        }
        return localData;
    }

    // 需要保存的数据
    public int locationX = -1;
    public int locationY = -1;
    public boolean showPet = true;


    // 其他数据
    @JSONField(serialize = false, deserialize = false)
    private long saveDataTimer = 0;

    public static void load() {
        File file = getLocalSaveFile();
        if (file.exists()) {
            localData = (LocalData) JSON.parseObject(Utils2.readAllFileText(file), LocalData.class);
            if (localData != null) {
                Log.res.info("read local data");
            }
        } else {
            // 初始化
            localData = new LocalData();
            localData.save();
        }

        // 开启自动保存
        new Timer((int) Time2.SEC, e -> {
            long timer = LocalData.instance().saveDataTimer;
            if (timer > 0 && System.currentTimeMillis() > timer) {
                LocalData.instance().save();
                LocalData.instance().saveDataTimer = 0;
            }
        }).start();
    }

    private static File getLocalSaveFile() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File defaultDirectory = fileSystemView.getDefaultDirectory();
        File file = new File(defaultDirectory, LOCAL_DATA_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public void save() {
        File file = getLocalSaveFile();
        Utils2.writeFile(file, JSON.toJSONString(this));
        Log.res.info("save local data");
    }

    public void saveLater() {
        if (saveDataTimer <= 0) {
            saveDataTimer = System.currentTimeMillis() + Time2.SEC;
        }
    }
}