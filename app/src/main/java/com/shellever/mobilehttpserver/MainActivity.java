package com.shellever.mobilehttpserver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


//
// 在模拟器上运行的时候，需要先telnet 127.0.0.1 5554
// 然后再将本地ip映射到虚拟机ip，执行命令redir add tcp:8088:8088
//
// 09/29/2016: 自动获取WiFi连接下分配的IP地址
public class MainActivity extends AppCompatActivity {

    private TextView address;
    private ImageView image;

    private WebConfig mWebConfig;
    private SimpleHttpServer mHttpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = (TextView) findViewById(R.id.address);
        image = (ImageView) findViewById(R.id.image);

        startServer();
    }

    private String getIpAddress() {
        WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);// 获取WiFi服务
        if(!mWifiManager.isWifiEnabled()){      // 判断WiFi是否开启，未开启则自动打开
            mWifiManager.setWifiEnabled(true);  // 更改WiFi状态，故需要添加CHANGE_WIFI_STATE权限
        }
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = mWifiInfo.getIpAddress();
        return String.format(Locale.getDefault(),"%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    private void startServer() {
        mWebConfig = new WebConfig();
        mWebConfig.setHost(getIpAddress());             // 自动获取WiFi下分配的IP地址 "192.168.1.102"
        mWebConfig.setPort(9527);
        mWebConfig.setMaxParallels(50);
        String url = getIpAddress() + ":9527";
        address.setText(url);
        mHttpServer = new SimpleHttpServer(mWebConfig);
        // 注册ResourceHandler
        mHttpServer.registerResourceHandler(new ResourceInAssetsHandler(this));
        mHttpServer.registerResourceHandler(new UploadImageHandler(){
            @Override
            protected void onImageLoaded(String path) {
                showImage(path);
            }
        });
        mHttpServer.startAsync();
    }

    private void showImage(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                image.setImageBitmap(bitmap);
                Toast.makeText(MainActivity.this, "image received and shown", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHttpServer.stopAsync();
    }
}

// testing
// Firefox Add-on: HttpRequester
// Browser: 192.168.1.102:9527/upload_image/    (POST)


// testing
// https://m.uc123.com/
// Browser: 192.168.1.102:9527/static/muc123.html


// testing
// Browser: 192.168.1.102:9527/static/1
// Browser: 192.168.1.102:9527/upload_image/1
// D/SPY: a remote peer accepted.../192.168.1.100:5945
// D/SPY: /static/1
// D/SPY: header line = Host: 192.168.1.102:9527
// D/SPY: header line = User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:48.0) Gecko/20100101 Firefox/48.0
// D/SPY: header line = Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
// D/SPY: header line = Accept-Language: en-US,zh-CN;q=0.7,en;q=0.3
// D/SPY: header line = Accept-Encoding: gzip, deflate
// D/SPY: header line = Connection: keep-alive
// D/SPY: header line = Upgrade-Insecure-Requests: 1
// D/SPY: header line = Cache-Control: max-age=0
// D/SPY: a remote peer accepted.../192.168.1.100:5956
// D/SPY: /upload_image/1
// D/SPY: header line = Host: 192.168.1.102:9527
// D/SPY: header line = User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:48.0) Gecko/20100101 Firefox/48.0
// D/SPY: header line = Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
// D/SPY: header line = Accept-Language: en-US,zh-CN;q=0.7,en;q=0.3
// D/SPY: header line = Accept-Encoding: gzip, deflate
// D/SPY: header line = Connection: keep-alive
// D/SPY: header line = Upgrade-Insecure-Requests: 1



///*
// testing
// Browser: 192.168.1.102:9527
// D/SPY: a remote peer accepted.../192.168.1.100:33028
// D/SPY: a remote peer accepted.../192.168.1.100:33029
// D/SPY: header line = GET / HTTP/1.1
// D/SPY: header line = Host: 192.168.1.102:9527
// D/SPY: header line = Connection: keep-alive
// D/SPY: header line = Cache-Control: max-age=0
// D/SPY: header line = Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
// D/SPY: header line = Upgrade-Insecure-Requests: 1
// D/SPY: header line = User-Agent: Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.15319.202 Safari/537.36
// D/SPY: header line = Accept-Encoding: gzip, deflate
// D/SPY: header line = Accept-Language: zh-CN,zh;q=0.8
//*/



/*
// testing
// cmd
// telnet 192.168.1.102 9527
// D/SPY: a remote peer accepted.../192.168.1.100:28206
 */

