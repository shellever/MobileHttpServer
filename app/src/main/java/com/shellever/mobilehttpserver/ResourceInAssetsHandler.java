package com.shellever.mobilehttpserver;

import android.content.Context;

import java.io.IOException;
import java.io.PrintStream;


public class ResourceInAssetsHandler implements IResourceUriHandler {

    private static final String acceptPrefix = "/static/";
    private Context mContext;

    public ResourceInAssetsHandler(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean accept(final String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void handle(String uri, HttpContext mHttpContext) {
//        test(mHttpContext);
        int startIndex = acceptPrefix.length();
        String assetsPath = uri.substring(startIndex);
        try {
            byte[] raw = StreamToolkit.readRawFromStream(mContext.getAssets().open(assetsPath));
            if (raw == null){
                raw = StreamToolkit.readRawFromStream(mContext.getAssets().open("default.html"));
                if (raw == null){
                    test(mHttpContext);
                    return;
                }
            }
            PrintStream out = new PrintStream(mHttpContext.getUnderlySocket().getOutputStream());
            out.println("HTTP/1.1 200 OK");
            out.println("Content-length: " + raw.length);
            if(assetsPath.endsWith(".html")){
                out.println("Content-Type: text/html; charset=utf-8");
            }else if(assetsPath.endsWith(".js")){
                out.println("Content-Type: application/x-javascript");
            }else if(assetsPath.endsWith(".css")){
                out.println("Content-Type: text/css");
            }else if(assetsPath.endsWith(".jpg")){
                out.println("Content-Type: image/jpg");
            }else if(assetsPath.endsWith(".png")){
                out.println("Content-Type: image/png");
            }else if(assetsPath.endsWith(".json")){
                out.println("Content-Type: application/json");
            }
            out.println();      // \r\n alone in a line between header and body
            out.write(raw);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void test(HttpContext mHttpContext) {
        try {
            PrintStream out = new PrintStream(mHttpContext.getUnderlySocket().getOutputStream());
            out.println("HTTP/1.1 200 OK");
            out.println();
            out.println("from resource in assets handler");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
