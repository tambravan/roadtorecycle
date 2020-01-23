package com.app.roadtorecycle.appmanager;

import android.content.Context;
import android.util.Log;

import com.app.roadtorecycle.utills.Constants;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okio.Buffer;

public class JSONHttpClient {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static <S> void PostObject(final String url, final S object, Callback callback) {
        RequestBody requestBody = null;
        if (object != null) {
            String json = new GsonBuilder().create().toJson(object);
            requestBody = RequestBody.create(JSON, json);
        } else {
            requestBody = RequestBody.create(JSON, "{}");
        }


        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static <S> void PostObjectWithHeader(final String url, final S object, Callback callback, Context context) {
        RequestBody requestBody = null;
        if (object != null) {
            String json = new GsonBuilder().create().toJson(object);
            try {
                JSONObject jsonObject = new JSONObject(json);


                requestBody = RequestBody.create(JSON, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            requestBody = RequestBody.create(JSON, "{}");
        }
        Log.d("Krupa Testing", url);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("", "")
                .build();
        Log.d("Krupa Testing", bodyToString(request));
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static <S> void PostObjectWithHeaderDB(final String url, final S object, Callback callback, String dbname) {
        RequestBody requestBody = null;
        if (object != null) {
            String json = new GsonBuilder().create().toJson(object);
            json = json.replace("}", ",\"database_name\":\"" + dbname + "\"}");

            Log.d("lll", json);

            requestBody = RequestBody.create(JSON, json);
        } else {
            requestBody = RequestBody.create(JSON, "{}");
        }
        Log.d("Krupa Testing", url);


        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("", "")
                .build();
        Log.d("Krupa Testing", bodyToString(request));
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }



    public static <S> void PostStringWithHeader(final String url, final String object, Callback callback, Context context) {
        RequestBody requestBody = null;
        requestBody = new FormEncodingBuilder()
                .add("kk", object)
                .build();
        Log.d("Krupa Testing", url);

        String apiKey = "";
        Log.d("db", apiKey);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("", "")
                .build();
        Log.d("Krupa Testing", bodyToString(request));
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void PostJSON(final String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);

        Log.d("hiii", url);
        Log.d("hiii", json);


        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void HttpDelete(final String url, Callback callback) {
        // String apiKey = Utilities.readPreferences(Const.SHARED_PREFERENCE_API_KEY, "").toString();
        Request request = new Request.Builder()
                .url(url)
                // .header("X-Api-Key", apiKey)
                .delete()
                .build();
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static <S> void HttpPut(final String url, final S object, Callback callback) {
        //String apiKey = Utilities.readPreferences(Const.SHARED_PREFERENCE_API_KEY, "").toString();
        RequestBody requestBody = null;
        if (object != null) {
            String json = new GsonBuilder().create().toJson(object);
            requestBody = RequestBody.create(JSON, json);
        } else {
            requestBody = RequestBody.create(JSON, "{}");
        }
        Request request = new Request.Builder()
                .url(url)
                //.header("X-Api-Key", apiKey)
                .put(requestBody)
                .build();
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void HttpGet(final String url, Callback callback) {
        //String apiKey = Utilities.readPreferences(Const.SHARED_PREFERENCE_API_KEY, "").toString();
        Request request = new Request.Builder()
                .url(url)
                //.header("X-Api-Key", apiKey)
                .build();
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void HttpGetWithHeader(final String url, Callback callback, Context context) {
        RequestBody requestBody = null;
        try {
        JSONObject jsonObject = new JSONObject();
//            SharedPreferences sp = context.getSharedPreferences(Const.SHAREDPREFERENCE, Context.MODE_PRIVATE);
//            String water = sp.getString("water_section", "");
//            jsonObject.put("database_name", Const.getDbName(context));
//            if (url.equals(ApiConfig.GET_ALL_CUSTOMER) && water.equals("1")) {
//                jsonObject.put("sales_id", Const.getSalesID(context));
//            }


        requestBody = RequestBody.create(JSON, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .header("", "")
                .build();
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void PostMultipart(final String url, File file, String filePartName, String contentType, Callback callback) {
        //String apiKey = Utilities.readPreferences(Const.SHARED_PREFERENCE_API_KEY, "").toString();
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(filePartName, file.getName(),
                        RequestBody.create(MediaType.parse(contentType), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                // .header("X-Api-Key", apiKey)
                .post(requestBody)
                .build();

        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(callback);
    }
    public static void PostFormData(String url, Map<String,String> params, Callback responseCallback) {
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("Authorization", Constants.AUTH_KEY)
                .build();
        OkHttpSingleton.getOkHttpClient().newCall(request).enqueue(responseCallback);
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {

            return "did not work";
        }
    }

}
