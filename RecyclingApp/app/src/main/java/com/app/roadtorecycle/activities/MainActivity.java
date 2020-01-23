package com.app.roadtorecycle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.roadtorecycle.appmanager.DataManager;
import com.app.roadtorecycle.appmanager.JSONHttpClient;
import com.app.roadtorecycle.models.ProductModel;
import com.app.roadtorecycle.utills.Constants;
import com.app.roadtorecycle.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Callback {

    Button enterBtn;
    EditText zipCodeEd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zipCodeEd = findViewById(R.id.zipCodeEd);
        enterBtn = findViewById(R.id.enterBtn);

        enterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enterBtn) {
            if (TextUtils.isEmpty(zipCodeEd.getText().toString().trim())) {
                zipCodeEd.requestFocus();
                zipCodeEd.setError("Enter ZipCode");
            } else if (zipCodeEd.getText().toString().trim().length() != 5) {
                zipCodeEd.requestFocus();
                zipCodeEd.setError("Enter 5-digit ZipCode");
            } else {
                if (DataManager.getInstance(MainActivity.this).isNetworkAvailable(MainActivity.this)) {
                    DataManager.getInstance(MainActivity.this).setZipCode(zipCodeEd.getText().toString().trim());
                    DataManager.getInstance(MainActivity.this).showProgressMessage(MainActivity.this);
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("zipcode", zipCodeEd.getText().toString().trim());
                    JSONHttpClient.PostFormData(Constants.SEARCH_ZIP_CODE, map, this);
                } else {
                    DataManager.getInstance(MainActivity.this).noInternetDialog(MainActivity.this);
                }
            }
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Server Error. Please try again later", Toast.LENGTH_SHORT).show();

                DataManager.getInstance(MainActivity.this).hideProgressMessage();
            }
        });
    }

    @Override
    public void onResponse(Response response) throws IOException {
        String body = response.body().string();
        try {
            JSONObject jsonObject = new JSONObject(body);
            String success = jsonObject.getString("success");
            final String msg = jsonObject.getString("message");
            if (success.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("categoryinfo");
                DataManager.getInstance(MainActivity.this).clearProductModelArrayList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ProductModel productModel = new ProductModel(object.optString("category_id"), object.optString("category_name"), object.optString("image"));
                    DataManager.getInstance(MainActivity.this).addProductModelArrayList(productModel);
                }
                DataManager.getInstance(MainActivity.this).hideProgressMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        zipCodeEd.setText("");
                        startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage(getResources().getString(R.string.submit_text));
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                getResources().getString(R.string.submit),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mailAction();
                                    }
                                });

                        builder1.setNegativeButton(
                                getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        zipCodeEd.setText("");
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            DataManager.getInstance(MainActivity.this).hideProgressMessage();
        }

    }

    public void mailAction() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "tejas.ambravan@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.support_request));
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "This app does not yet include your zip code (" + zipCodeEd.getText().toString().trim() + "). Would you like to send a request to the admin to speed up the process of including your area?");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please add " + zipCodeEd.getText().toString().trim() + " to this "+getResources().getString(R.string.app_name)+" app!");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));

        zipCodeEd.setText("");
    }
}
