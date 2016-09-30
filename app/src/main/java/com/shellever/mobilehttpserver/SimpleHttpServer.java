package com.shellever.mobilehttpserver;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SimpleHttpServer {
    private boolean isEnable;
    private final WebConfig mWebConfig;
    private ServerSocket mServerSocket;         // ServerSocket
    private ExecutorService mThreadPool;
    private Set<IResourceUriHandler> resourceUriHandlers;

    public SimpleHttpServer(WebConfig config){
        this.mWebConfig = config;
        this.mThreadPool = Executors.newCachedThreadPool();
        this.resourceUriHandlers = new HashSet<>();
    }

    // start server (async)
    public void startAsync(){
        isEnable = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doProcessSync();
            }
        }).start();
    }

    // stop server (async)
    public void stopAsync(){
        if(!isEnable){
            return;
        }
        isEnable = false;
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServerSocket = null;
    }

    private void doProcessSync() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(mWebConfig.getHost(), mWebConfig.getPort());
            mServerSocket = new ServerSocket();
            mServerSocket.bind(socketAddress);
            while(isEnable){
                final Socket remotePeer = mServerSocket.accept();
                mThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SPY", "a remote peer accepted..." + remotePeer.getRemoteSocketAddress().toString());
                        onAcceptRemotePeer(remotePeer);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onAcceptRemotePeer(Socket remotePeer) {
        try {
//            remotePeer.getOutputStream().write("congrats, connected successful...".getBytes());
            HttpContext mHttpContext = new HttpContext();
            mHttpContext.setUnderlySocket(remotePeer);
            InputStream in = remotePeer.getInputStream();
            String headLine;
            String resourceUri = StreamToolkit.readLine(in).split(" ")[1];
            Log.d("SPY", resourceUri);
            while((headLine = StreamToolkit.readLine(in)) != null){
                if(headLine.equals("\r\n")){
                    break;
                }
                String[] pair = headLine.split(": ");
                if(pair.length > 1){
                    mHttpContext.addRequestHeader(pair[0], pair[1]);
                }
                Log.d("SPY", "header line = " + headLine);
            }

            // 将请求交于合适的处理者处理 - 请求的分发
            for(IResourceUriHandler handler: resourceUriHandlers){
                if(!handler.accept(resourceUri)){
                    continue;
                }
                handler.handle(resourceUri, mHttpContext);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                remotePeer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerResourceHandler(IResourceUriHandler handler){
        resourceUriHandlers.add(handler);
    }
}

