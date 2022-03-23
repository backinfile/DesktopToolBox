package com.backinfile.toolBox.net;

import com.backinfile.support.SysException;
import com.backinfile.support.Utils;
import com.backinfile.toolBox.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

public class Request {

    private String url = "";
    private String params;
    private String method = "GET";

    public Request(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            if (Utils.isNullOrEmpty(line)) {
                throw new SysException("empty request");
            }
            String[] requestLine = line.split(" ");
            if (requestLine.length == 3 && requestLine[2].equals("HTTP/1.1")) {
                this.method = requestLine[0];
                String fullUrl = URLDecoder.decode(requestLine[1], Utils.UTF8);
                if (fullUrl.contains("?")) {
                    this.url = fullUrl.substring(0, fullUrl.indexOf("?"));
                    this.params = fullUrl.substring(fullUrl.indexOf("?") + 1);
                } else {
                    this.url = fullUrl;
                }
            }
        } catch (IOException e) {
            Log.game.error(e.getMessage());
        }

    }

    public String getUrl() {
        return url;
    }

    public String getParams() {
        return params;
    }

    public String getMethod() {
        return method;
    }
}
