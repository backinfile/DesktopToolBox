package com.backinfile.toolBox.net;

import com.backinfile.support.Time2;
import com.backinfile.support.func.Action1;
import com.backinfile.toolBox.Config;
import com.backinfile.toolBox.Log;
import com.backinfile.toolBox.Utils2;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class FileServer {
    private Thread fileServerThread;
    private ServerSocket socket;
    private String path;
    private final List<Action1<String>> logOutput = new ArrayList<>();

    public String start(String path) {
        File file = new File(path);
        if (!file.isAbsolute() || !file.exists()) {
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

        if (socket != null && !socket.isClosed()) {
            log("启动成功 文件路径" + file.getAbsolutePath());
            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                log("本地访问: http://127.0.0.1:" + Config.FILE_SERVER_PORT);
                log("局域网内部访问: http://" + ip + ":" + Config.FILE_SERVER_PORT);
            } catch (UnknownHostException e) {
                Log.game.error("", e);
            }
        } else {
            log("启动失败");
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
                client.setSoTimeout((int) Time2.SEC);
                Log.game.info("accept client:{}", client.getRemoteSocketAddress());
                handle(client, new Request(client.getInputStream()), new Response(client.getOutputStream()));
            } catch (Exception e) {
                Log.game.error("", e);
            } finally {
                if (client != null && !client.isClosed()) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        Log.game.error("", e);
                    }
                }
            }
        }
        log("已关闭");
    }

    private void handle(Socket client, Request request, Response response) {
        Log.game.info("handle request url:{}", request.getUrl());
        File requestFile = new File(path, request.getUrl());
        if (!"GET".equalsIgnoreCase(request.getMethod()) || !Utils2.isFileOk(requestFile) || request.getUrl().contains("..")) {
            response.setStatus(404).send("");
            Log.game.info("response 404");
            return;
        }

        if (requestFile.isDirectory()) {
            List<File> requestFileList = new ArrayList<>();
            File[] list = requestFile.listFiles();
            if (list != null) {
                for (File fileInDir : list) {
                    if (Utils2.isFileOk(fileInDir)) {
                        requestFileList.add(fileInDir);
                    }
                }
            }
            requestFileList.sort((a, b) -> {
                if (a.isDirectory() && !b.isDirectory()) {
                    return -1;
                }
                if (!a.isDirectory() && b.isDirectory()) {
                    return 1;
                }
                return a.getName().compareTo(b.getName());
            });

            StringBuilder htmlContent = new StringBuilder();
            for (File file : requestFileList) {
                String fileName = file.getName();

                htmlContent.append(file.isDirectory() ? "[目录]" : "[文件]");
                htmlContent.append("<a href=\"");
                htmlContent.append(new File(request.getUrl(), fileName).getPath());
                htmlContent.append("\">");
                htmlContent.append(fileName);
                htmlContent.append("</a>");

                if (!file.isDirectory()) {
                    htmlContent.append("<font color=\"#AAA\">[");
                    htmlContent.append(Utils2.getFileSize(file));
                    htmlContent.append("]</font>");
                }

                htmlContent.append("<br/>");
            }
            if (requestFileList.isEmpty()) {
                htmlContent.append("文件夹为空");
            }
            Log.game.info("response folder");
            response.setHeaders("Content-Type", "text/html; charset=utf-8").send(htmlContent.toString());
            return;
        }

        // 文件下载开始
        Log.game.info("response requestFile");
        log(client.getRemoteSocketAddress() + "下载了" + request.getUrl());
        response.sendFile(requestFile);
    }

    public void clearLogOutput() {
        logOutput.clear();
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    public void addLogOutput(Action1<String> callback) {
        if (!logOutput.contains(callback)) {
            logOutput.add(callback);
        }
    }

    private void log(String log) {
        for (Action1<String> callback : logOutput) {
            callback.invoke(log);
        }
    }


    // http://127.0.0.1:8080/
    public static void main(String[] args) {
        new FileServer().start("D:\\");
    }

}
