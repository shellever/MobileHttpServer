package com.shellever.mobilehttpserver;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UploadImageHandler implements IResourceUriHandler {

    private static final String acceptPrefix = "/upload_image/";

    public UploadImageHandler() {
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void handle(String uri, HttpContext mHttpContext) {
//        test(mHttpContext);
        Log.d("SPY", "UploadImageHandler...");
        long totalLength = Long.parseLong(mHttpContext.getRequestHeaderValue("Content-Length"));
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());//格式化时间戳
        String rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
//        File file = new File(rootPath + File.separator + timeStamp + ".jpg");
        File file = new File(rootPath + File.separator + "upload" + ".jpg");
        Log.d("SPY", "FilePath: " + file.getAbsolutePath());
        try {
            FileOutputStream out = new FileOutputStream(file);
            InputStream in = mHttpContext.getUnderlySocket().getInputStream();
            byte[] buffer = new byte[10 * 1024];        // 10kB
            int nRead;
            long mRestLength = totalLength - 2;         // \r\n
//            while((nRead = in.read(buffer)) > 0 && mRestLength > 0){
            while(mRestLength > 0 && (nRead = in.read(buffer)) > 0){
                out.write(buffer, 0, nRead);
                mRestLength -= nRead;
            }
//            in.close();           // !! socket will be closed and cause error
            out.close();

            PrintStream printer = new PrintStream(mHttpContext.getUnderlySocket().getOutputStream());
            printer.println("HTTP/1.1 200 OK");
            printer.println();
            printer.flush();
            printer.close();

            onImageLoaded(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onImageLoaded(String path){

    }

    private void test(HttpContext mHttpContext) {
        try {
            PrintStream out = new PrintStream(mHttpContext.getUnderlySocket().getOutputStream());
            out.println("HTTP/1.1 200 OK");
            out.println();
            out.println("from upload image handler");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
