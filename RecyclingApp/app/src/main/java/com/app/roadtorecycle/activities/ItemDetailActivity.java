package com.app.roadtorecycle.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.roadtorecycle.adapters.PlaceAdapter;
import com.app.roadtorecycle.appmanager.DataManager;
import com.app.roadtorecycle.appmanager.JSONHttpClient;
import com.app.roadtorecycle.models.PlacesModel;
import com.app.roadtorecycle.utills.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener, Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView zipCode, specialInstruct, subCatName, curbStatus;
    Button enterBtn, searchBtn;
    RecyclerView placesRecycleView;
    PlaceAdapter placeAdapter;
    private boolean flagCurbside;
    String specialInstruction = "";
    private GoogleApiClient mGoogleApiClient;
    private String lat, lon;
    private Location locations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 1, this)
                .build();
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

        specialInstruct = findViewById(R.id.specialInstruct);
        subCatName = findViewById(R.id.subCatName);
        placesRecycleView = findViewById(R.id.placesRecycleView);
        zipCode = findViewById(R.id.zipCode);
        enterBtn = toolbar.findViewById(R.id.enterBtn);
        searchBtn = toolbar.findViewById(R.id.searchBtn);
        curbStatus = findViewById(R.id.curbStatus);
        zipCode.setText(DataManager.getInstance(ItemDetailActivity.this).getZipCode());
        subCatName.setText(DataManager.getInstance(ItemDetailActivity.this).getSubProductModel().getProductName());
        enterBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        if (DataManager.getInstance(ItemDetailActivity.this).isNetworkAvailable(ItemDetailActivity.this)) {
            DataManager.getInstance(ItemDetailActivity.this).showProgressMessage(ItemDetailActivity.this);
            Map<String, String> map = new LinkedHashMap<>();
            map.put("category_id", DataManager.getInstance(ItemDetailActivity.this).getSubProductModel().getCategoryId());
            map.put("item_id", DataManager.getInstance(ItemDetailActivity.this).getSubProductModel().getProductId());
            map.put("zipcode", DataManager.getInstance(ItemDetailActivity.this).getZipCode());
            JSONHttpClient.PostFormData(Constants.SEARCH_CENTER, map, this);
        } else {
            DataManager.getInstance(ItemDetailActivity.this).noInternetDialog(ItemDetailActivity.this);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enterBtn) {
            startActivity(new Intent(ItemDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else if (v.getId() == R.id.searchBtn) {
            startActivity(new Intent(ItemDetailActivity.this, CategoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {


        String body = response.body().string();
        try {
            JSONObject jsonObject = new JSONObject(body);
            String success = jsonObject.getString("success");
            final String msg = jsonObject.getString("message");
            if (success.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("centerinfo");
                DataManager.getInstance(ItemDetailActivity.this).clearPlacesModelArrayList();
                flagCurbside = !jsonObject.optString("curbside").equalsIgnoreCase("No");
                specialInstruction = jsonObject.optString("Special");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    PlacesModel placesModel = new PlacesModel();

                    placesModel.setCenterName(object.optString("center_name"));
                    placesModel.setCenterAddress(object.optString("address"));
                    if (object.optString("url").equalsIgnoreCase("")) {
                        placesModel.setCenterUrl("N/A");
                    } else {
                        placesModel.setCenterUrl(object.optString("url"));
                    }
                    placesModel.setCenterHours(object.optString("OpenHours"));
                    if (object.optString("PhoneNumber").equalsIgnoreCase("")) {
                        placesModel.setCenterPhone("N/A");
                    } else {
                        placesModel.setCenterPhone(object.optString("PhoneNumber"));
                    }
                    placesModel.setCenterLatitude(object.optString("latitude"));
                    placesModel.setCenterLongitude(object.optString("longitude"));
                    placesModel.setCurrentLatitude("" + locations.getLatitude());
                    placesModel.setCurrentLongitude("" + locations.getLongitude());
                    if (locations != null) {
                        double miles = DataManager.getInstance(ItemDetailActivity.this).distance(locations.getLatitude(), locations.getLongitude(),
                                Double.parseDouble(placesModel.getCenterLatitude()), Double.parseDouble(placesModel.getCenterLongitude()));
                        placesModel.setCenterDistanceMiles("" + miles + " miles");
                    } else {
                        placesModel.setCenterDistanceMiles("N/A");
                    }
                    placesModel.setFlagRecycle(true); /*object.optBoolean("PhoneNumber ")*/
                    StringBuilder builder = new StringBuilder();
                    JSONArray array = object.getJSONArray("time_shedule");
                    String seprator = "\n";

                    for (int j = 0; j < array.length(); j++) {

                        builder.append(array.getString(j));
                        builder.append(seprator);
                    }
                    placesModel.setCenterOpeningDays(builder.toString().substring(0, builder.toString().length() - seprator.length()));

                    DataManager.getInstance(ItemDetailActivity.this).addPlacesModelArrayList(placesModel);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        placeAdapter = new PlaceAdapter(ItemDetailActivity.this, DataManager.getInstance(ItemDetailActivity.this).getPlacesModelArrayList());
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ItemDetailActivity.this);
                        placesRecycleView.setLayoutManager(layoutManager);
                        placesRecycleView.setAdapter(placeAdapter);
                        if (flagCurbside) {
                            curbStatus.setText(getResources().getString(R.string.curb_text));
                        } else {
                            curbStatus.setText(getResources().getString(R.string.curb_text_not));
                        }

                        if (specialInstruction.equals("")) {
                            specialInstruct.setText(getResources().getString(R.string.special_no));
                            specialInstruct.setMovementMethod(new ScrollingMovementMethod());
                        } else {
                            specialInstruct.setMovementMethod(new ScrollingMovementMethod());
                            specialInstruct.setText(getResources().getString(R.string.special_yes) + " " + specialInstruction);
                        }

                        DataManager.getInstance(ItemDetailActivity.this).hideProgressMessage();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ItemDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            DataManager.getInstance(ItemDetailActivity.this).hideProgressMessage();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(ItemDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ItemDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        } else {
            locations = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
