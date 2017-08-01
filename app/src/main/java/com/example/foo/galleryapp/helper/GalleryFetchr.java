package com.example.foo.galleryapp.helper;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by foo on 8/1/17.
 */

public class GalleryFetchr {

    protected static final String TAG = "GalleryFetchr";

    public String getUrlContents(String url) {
        String resp = null;
        byte[] bytes = getUrlBytes(url);
        resp = new String(bytes);
        Log.d(TAG, "resp: " + resp);
        return resp;
    }

    public byte[] getUrlBytes(String spec) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(spec);
            conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (HttpURLConnection.HTTP_OK != responseCode) {
                String msg = String.format("Failed HTTP requests - response code: %d", responseCode);
                new Exception(msg);
                return null;
            }
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead = in.read(bytes);
            while (bytesRead != -1) {
                out.write(bytes, 0, bytesRead);
                bytesRead = in.read(bytes);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } catch (MalformedURLException mue) {
            Log.e(TAG, mue.getMessage(), mue);
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage(), ioe);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
        return null;
    }
}
