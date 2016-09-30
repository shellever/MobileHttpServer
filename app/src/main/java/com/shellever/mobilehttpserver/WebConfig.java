package com.shellever.mobilehttpserver;

/**
 * Created by linuxfor on 9/28/2016.
 */
public class WebConfig {
    private String host;        // 主机地址
    private int port;           // 监听端口
    private int maxParallels;   // 最大监听数，即并发数

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxParallels() {
        return maxParallels;
    }

    public void setMaxParallels(int maxParallels) {
        this.maxParallels = maxParallels;
    }
}
