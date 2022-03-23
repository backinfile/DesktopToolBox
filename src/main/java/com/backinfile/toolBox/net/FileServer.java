package com.backinfile.toolBox.net;

import com.backinfile.toolBox.Config;
import com.backinfile.toolBox.Log;
import com.backinfile.toolBox.Utils2;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public enum FileServer {
    Instance;

    private Thread fileServerThread;
    private ServerSocket socket;
    private String path;

    public String start(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return "文件夹不存在";
        }
        if (!file.canRead()) {
            return "权限不足";
        }
        this.path = path;

        if (socket != null) {
            stop();
        }

        try {
            socket = new ServerSocket(Config.FILE_SERVER_PORT);
            Log.game.info("start listen:{}", socket.getInetAddress());
        } catch (IOException e) {
            return "端口被占用了";
        }

        fileServerThread = new Thread(this::run);
        fileServerThread.start();
        return "";
    }

    public void stop() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileServerThread != null) {
            fileServerThread.interrupt();
            fileServerThread = null;
        }
    }

    private void run() {
        while (socket != null && !socket.isClosed()) {
            Socket client = null;
            try {
                client = socket.accept();
                Log.game.info("accept client:{}", client.getRemoteSocketAddress());
            } catch (IOException e) {
                Log.game.error("", e);
                continue;
            }

            try {
                handle(new Request(client.getInputStream()), new Response(client.getOutputStream()));
            } catch (Exception e) {
                Log.game.error("", e);
            }
        }
    }

    private void handle(Request request, Response response) {
        Log.game.info("handle request url:{}", request.getUrl());
        File file = new File(path, request.getUrl());
        if (!"GET".equalsIgnoreCase(request.getMethod()) || !Utils2.isFileOk(file) || request.getUrl().contains("..")) {
            response.setStatus(404).send("");
            Log.game.info("response 404");
            return;
        }

        if (file.isDirectory()) {
            HashMap<String, Boolean> nameToURLMap = new HashMap<>();
            File[] list = file.listFiles();
            if (list != null) {
                for (File fileInDir : list) {
                    if (Utils2.isFileOk(fileInDir)) {
                        String name = fileInDir.getName();
                        nameToURLMap.put(name, fileInDir.isDirectory());
                    }
                }
            }
            StringBuilder htmlContent = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : nameToURLMap.entrySet()) {
                htmlContent.append(entry.getValue() ? "[目录]" : "[文件]");
                htmlContent.append("<a href=\"");
                htmlContent.append(new File(request.getUrl(), entry.getKey()).getPath());
                htmlContent.append("\">");
                htmlContent.append(entry.getKey());
                htmlContent.append("</a>");
                htmlContent.append("<br/>");
            }
            if (nameToURLMap.isEmpty()) {
                htmlContent.append("文件夹为空");
            }
            Log.game.info("response folder");
            response.setHeaders("Content-Type", "text/html; charset=utf-8").send(htmlContent.toString());
            return;
        }

        // 文件下载开始
        Log.game.info("response file");
        response.sendFile(file);
    }

    // http://127.0.0.1:8080/
    public static void main(String[] args) {
        FileServer.Instance.start("D:\\");
    }
}
