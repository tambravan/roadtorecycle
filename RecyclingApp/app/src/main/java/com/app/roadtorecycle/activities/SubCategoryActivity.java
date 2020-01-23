package com.app.roadtorecycle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.roadtorecycle.adapters.SubProductAdapter;
import com.app.roadtorecycle.appmanager.DataManager;
import com.app.roadtorecycle.appmanager.JSONHttpClient;
import com.app.roadtorecycle.models.SubProductModel;
import com.app.roadtorecycle.utills.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class SubCategoryActivity extends AppCompatActivity implements Callback, View.OnClickListener {
    SubProductAdapter subProductAdapter;
    RecyclerView productRecyclerView;
    TextView categoryTx;
    Button searchBtn,enterBtn;
    ImageButton catIbBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        enterBtn = toolbar.findViewById(R.id.enterBtn);
        searchBtn = toolbar.findViewById(R.id.searchBtn);

        enterBtn .setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        catIbBtn = findViewById(R.id.catIbBtn);
        categoryTx = findViewById(R.id.categoryTx);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // perform whatever you want on back arrow click
            }
        });

        categoryTx.setText(DataManager.getInstance(SubCategoryActivity.this).getProductModel().getProductName());
        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions().error(R.drawable.app_icon))
                .load(DataManager.getInstance(SubCategoryActivity.this).getProductModel().getProductImg())
                .into(catIbBtn);
        fetchData();


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enterBtn) {
            startActivity(new Intent(SubCategoryActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }else if(v.getId() == R.id.searchBtn){
            startActivity(new Intent(SubCategoryActivity.this,CategoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }
    private void fetchData() {
        if (DataManager.getInstance(SubCategoryActivity.this).isNetworkAvailable(SubCategoryActivity.this)) {
            DataManager.getInstance(SubCategoryActivity.this).showProgressMessage(SubCategoryActivity.this);
            Map<String, String> map = new LinkedHashMap<>();
            map.put("category_id", DataManager.getInstance(this).getProductModel().getProductId());
            JSONHttpClient.PostFormData(Constants.ITEM_BY_CATEGORY, map, this);
        } else {
            DataManager.getInstance(SubCategoryActivity.this).noInternetDialog(SubCategoryActivity.this);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SubCategoryActivity.this, "Server Error. Please try again later", Toast.LENGTH_SHORT).show();

                DataManager.getInstance(SubCategoryActivity.this).hideProgressMessage();
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
                DataManager.getInstance(SubCategoryActivity.this).clearSubProductModelArrayList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    SubProductModel subProductModel = new SubProductModel(object.optString("item_id"), object.optString("item_name"), object.optString("category_id"));
                    DataManager.getInstance(SubCategoryActivity.this).addSubProductModelArrayList(subProductModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        subProductAdapter = new SubProductAdapter(SubCategoryActivity.this, DataManager.getInstance(SubCategoryActivity.this).getSubProductModelArrayList());
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SubCategoryActivity.this);
                        productRecyclerView.setLayoutManager(layoutManager);
                        productRecyclerView.setAdapter(subProductAdapter);

                        DataManager.getInstance(SubCategoryActivity.this).hideProgressMessage();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SubCategoryActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            DataManager.getInstance(SubCategoryActivity.this).hideProgressMessage();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
