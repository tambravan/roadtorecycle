package com.app.roadtorecycle.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.roadtorecycle.adapters.ProductAdapter;
import com.app.roadtorecycle.adapters.SubProductAdapter;
import com.app.roadtorecycle.appmanager.DataManager;
import com.app.roadtorecycle.appmanager.JSONHttpClient;
import com.app.roadtorecycle.models.ProductModel;
import com.app.roadtorecycle.models.SubProductModel;
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

public class CategoryActivity extends AppCompatActivity implements Callback {
    ProductAdapter productAdapter;
    SubProductAdapter subProductAdapter;
    RecyclerView productRecyclerView, productRecyclerViewSearch;
    public static EditText searchItemEd;
    TextView catgoryHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // perform whatever you want on back arrow click
            }
        });
        searchItemEd = findViewById(R.id.searchItemEd);
        catgoryHeader = findViewById(R.id.catgoryHeader);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerViewSearch = findViewById(R.id.productRecyclerViewSearch);
        subProductAdapter = new SubProductAdapter(this, DataManager.getInstance(CategoryActivity.this).getSubProductModelArrayList());

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(CategoryActivity.this);
        productRecyclerViewSearch.setLayoutManager(layoutManager1);
        productRecyclerViewSearch.setAdapter(subProductAdapter);
        searchItemEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItemEd.setFocusable(true);
                searchItemEd.requestFocus();
                searchItemEd.requestFocusFromTouch();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        searchItemEd.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });

        searchItemEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    productRecyclerView.setVisibility(View.VISIBLE);
                    catgoryHeader.setVisibility(View.VISIBLE);
                    productRecyclerViewSearch.setVisibility(View.GONE);
                } else {
                    catgoryHeader.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.GONE);
                    productRecyclerViewSearch.setVisibility(View.VISIBLE);
                    searchItem(s.toString());

                }
            }
        });

        if (searchItemEd.getText().length() == 0) {
            if (DataManager.getInstance(CategoryActivity.this).isNetworkAvailable(CategoryActivity.this)) {
                DataManager.getInstance(CategoryActivity.this).showProgressMessage(CategoryActivity.this);
                Map<String, String> map = new LinkedHashMap<>();
                map.put("zipcode", DataManager.getInstance(CategoryActivity.this).getZipCode());
                JSONHttpClient.PostFormData(Constants.SEARCH_ZIP_CODE, map, this);
            } else {
                DataManager.getInstance(CategoryActivity.this).noInternetDialog(CategoryActivity.this);
            }
        }

    }

    private void searchItem(String searchString) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("zipcode", DataManager.getInstance(CategoryActivity.this).getZipCode());
        map.put("item_name", searchString);
        JSONHttpClient.PostFormData(Constants.SEARCH_ITEM, map, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onFailure(Request request, IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CategoryActivity.this, "Server Error. Please try again later", Toast.LENGTH_SHORT).show();

                DataManager.getInstance(CategoryActivity.this).hideProgressMessage();
            }
        });
    }

    boolean isOdd(int val) {
        return (val & 0x01) != 0;
    }

    @Override
    public void onResponse(Response response) throws IOException {

        if (response.request().urlString().startsWith(Constants.SEARCH_ZIP_CODE)) {

            String body = response.body().string();
            try {
                JSONObject jsonObject = new JSONObject(body);
                String success = jsonObject.getString("success");
                final String msg = jsonObject.getString("message");
                if (success.equals("1")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("categoryinfo");
                    DataManager.getInstance(CategoryActivity.this).clearProductModelArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ProductModel productModel = new ProductModel(object.optString("category_id"), object.optString("category_name"), object.optString("image"));
                        DataManager.getInstance(CategoryActivity.this).addProductModelArrayList(productModel);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            productAdapter = new ProductAdapter(CategoryActivity.this, DataManager.getInstance(CategoryActivity.this).getProductModelArrayList());
                            GridLayoutManager layoutManager = new GridLayoutManager(CategoryActivity.this, 6, GridLayoutManager.VERTICAL, false);
                            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    if (isOdd(DataManager.getInstance(CategoryActivity.this).getProductModelArrayList().size())) {
                                        if (position == DataManager.getInstance(CategoryActivity.this).getProductModelArrayList().size() - 2)
                                            return 3;
                                        else
                                            return 2;
                                    }else return 2;
                                }
                            });


                            productRecyclerView.setLayoutManager(layoutManager);
                            productRecyclerView.setAdapter(productAdapter);

                            productAdapter.notifyDataSetChanged();
                        }
                    });
                    DataManager.getInstance(CategoryActivity.this).hideProgressMessage();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CategoryActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                DataManager.getInstance(CategoryActivity.this).hideProgressMessage();
            }

        } else {
            String body = response.body().string();
            try {
                JSONObject jsonObject = new JSONObject(body);
                String success = jsonObject.getString("success");
                final String msg = jsonObject.getString("message");
                if (success.equals("1")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("iteminfo");
                    DataManager.getInstance(CategoryActivity.this).clearSubProductModelArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        SubProductModel subProductModel = new SubProductModel(object.optString("item_id"), object.optString("item_name"), object.optString("category_id"));
                        DataManager.getInstance(CategoryActivity.this).addSubProductModelArrayList(subProductModel);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            productRecyclerViewSearch.setAdapter(subProductAdapter);
                            DataManager.getInstance(CategoryActivity.this).hideProgressMessage();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CategoryActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                DataManager.getInstance(CategoryActivity.this).hideProgressMessage();
            }
        }
    }
}
