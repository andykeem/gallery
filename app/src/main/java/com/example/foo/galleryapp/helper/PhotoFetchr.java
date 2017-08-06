package com.example.foo.galleryapp.helper;

import android.net.Uri;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by foo on 8/1/17.
 */

public class PhotoFetchr {

    // https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=your_key&per_page=10&format=json&nojsoncallback=1
    protected static final String TAG = "PhotoFetchr";
    protected static final String ENDPOINT = "https://api.flickr.com";
    protected static final String REST_PATH = "services/rest";
    protected static final String API_KEY = "2720839ea1270631cf82867ae309ef70";
    protected static final String FORMAT_JSON = "json";
    protected static final String METHOD_PHOTOS_GET_RECENT = "flickr.photos.getRecent";
    protected static final String METHOD_PHOTOS_SEARCH = "flickr.photos.search";

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

    protected Uri.Builder getBaseUriBuilder() {
        return Uri.parse(ENDPOINT).buildUpon()
                .appendEncodedPath(REST_PATH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", FORMAT_JSON)
                .appendQueryParameter("nojsoncallback", "1");
    }

    public List<Photo> fetchPhotos() {
        List<Photo> photos = new ArrayList<>();
        Uri uri = getBaseUriBuilder()
                .appendQueryParameter("method", METHOD_PHOTOS_GET_RECENT)
                .build();
        String url = uri.toString();
        String resp = getUrlContents(url);
        if (!TextUtils.isEmpty(resp)) {
            photos = getPhotos(resp);
        }
        return photos;
    }

    protected List<Photo> getPhotos(String json) {
        List<Photo> photos = new ArrayList<>();
        JSONTokener tokener = new JSONTokener(json);
        try {
            JSONObject jo = (JSONObject) tokener.nextValue();
            JSONObject jphotos = jo.getJSONObject("photos");
            JSONArray jphoto = jphotos.getJSONArray("photo");
            int len = jphoto.length();
            for (int i = 0; i < len; i++) {
                JSONObject item = (JSONObject) jphoto.get(i);
                String title = item.getString("title");
                Photo photo = new Photo();
                photo.setTitle(title);
                photos.add(photo);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return photos;
    }

    protected String getSearchUrlByQuery(String query) {
        return getBaseUriBuilder()
                .appendQueryParameter("method", METHOD_PHOTOS_SEARCH)
                .appendQueryParameter("text", query)
                .build()
                .toString();
    }

    public List<Photo> searchPhotosByQuery(String query) {
        List<Photo> photos = new ArrayList<>();
        String url = getSearchUrlByQuery(query);
        String resp = getUrlContents(url);
        if (!TextUtils.isEmpty(resp)) {
            photos = parseSearchResultJson(resp);
        }
        return photos;
    }

    protected List<Photo> parseSearchResultJson(String json) {
        List<Photo> photos = new ArrayList<>();
        try {
            JSONObject jobj = (JSONObject) new JSONTokener(json).nextValue();
            JSONObject jphotos = jobj.getJSONObject("photos");
            JSONArray jphoto = jphotos.getJSONArray("photo");
            int numPhotos = jphoto.length();
            for (int i = 0; i < numPhotos; i++) {
                JSONObject item = (JSONObject) jphoto.get(i);
                String title = item.getString("title");
                Photo photo = new Photo();
                photo.setTitle(title);
                photos.add(photo);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return photos;
    }
}
