package com.shellever.mobilehttpserver;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class StreamToolkit {
    public static String readLine(InputStream in){
        try {
            StringBuilder sb = new StringBuilder();
            int c1 = 0;
            int c2 = 0;
            while (c2 != -1 && !(c1 == '\r' && c2 == '\n')) {
                c1 = c2;
                c2 = in.read();
                sb.append((char) c2);
            }

            if (sb.length() == 0) {
                return null;
            }

            if (sb.length() > 2) {    // remove \r\n in headline
                return sb.toString().substring(0, sb.length() - 2);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readRawFromStream(InputStream fis) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[10 * 1024];        // 10kB
            int nRead;
            while ((nRead = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, nRead);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

