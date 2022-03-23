package com.backinfile.toolBox.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Response {
    private final OutputStream outputStream;
    private final HashMap<String, String> headers = new HashMap<>();
    private int status = 200;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Response setHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Response setStatus(int statusCode) {
        this.status = statusCode;
        return this;
    }

    public void send(String data) {
        try {
            StringBuilder dataBuilder = new StringBuilder();
            dataBuilder.append("HTTP/1.1 ").append(this.status).append("\n");
            for (String key :
                    this.headers.keySet()) {
                dataBuilder.append(key).append(": ").append(this.headers.get(key)).append("\n");
            }
            dataBuilder.append("\n").append(data);
            outputStream.write(dataBuilder.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
//            String contentType = Files.probeContentType(file.toPath());
            setHeaders("Content-Type", "application/force-download");
            setHeaders("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"");
            setHeaders("Content-Length", String.valueOf(inputStream.available()));

            // 写入头
            StringBuilder headBuilder = new StringBuilder();
            headBuilder.append("HTTP/1.1 ").append(this.status).append("\n");
            for (String key :
                    this.headers.keySet()) {
                headBuilder.append(key).append(": ").append(this.headers.get(key)).append("\n");
            }
            outputStream.write(headBuilder.toString().getBytes());


            // 写入文件
            outputStream.write("\n".getBytes());
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}