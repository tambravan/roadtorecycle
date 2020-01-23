package com.app.roadtorecycle.appmanager;

import android.os.Environment;

import com.app.roadtorecycle.utills.Constants;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class OkHttpSingleton {
    private static OkHttpClient okHttpClient;

    private OkHttpSingleton() {
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS); // connect timeout
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);    // socket timeout
            //createCacheForOkHTTP();
        }
        return okHttpClient;
    }

    private static void createCacheForOkHTTP() {
        Cache cache = new Cache(getCacheDirectory(), 1024 * 1024 * 10);
        okHttpClient.setCache(cache);
    }

    //returns the file to store cached details
    private static File getCacheDirectory() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "SalesApp" + File.separator);
        root.mkdirs();
        final String fname = Constants.OK_HTTP_CACHE_DIRECTORY_NAME;
        return new File(root, fname);
    }
}
