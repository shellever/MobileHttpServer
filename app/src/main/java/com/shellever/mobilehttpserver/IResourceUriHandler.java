package com.shellever.mobilehttpserver;


public interface IResourceUriHandler {
    boolean accept(String uri);
    void handle(String uri, HttpContext mHttpContext);
}
