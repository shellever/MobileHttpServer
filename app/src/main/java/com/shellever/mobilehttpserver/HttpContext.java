package com.shellever.mobilehttpserver;

import java.net.Socket;
import java.util.HashMap;


public class HttpContext {
    private Socket underlySocket;
    private HashMap<String, String> requestHeaders;

    public HttpContext(){
        requestHeaders = new HashMap<>();
    }

    public Socket getUnderlySocket() {
        return underlySocket;
    }

    public void setUnderlySocket(Socket underlySocket) {
        this.underlySocket = underlySocket;
    }

    public void addRequestHeader(String headerKey, String headerValue){
        requestHeaders.put(headerKey, headerValue);
    }

    public String getRequestHeaderValue(String headerKey){
        return requestHeaders.get(headerKey);
    }
}
