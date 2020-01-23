package com.app.roadtorecycle.appmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.roadtorecycle.models.PlacesModel;
import com.app.roadtorecycle.models.ProductModel;
import com.app.roadtorecycle.models.SubProductModel;
import com.app.roadtorecycle.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataManager {
    @SuppressLint("StaticFieldLeak")
    private static final DataManager ourInstance = new DataManager();
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static DataManager getInstance(Context context) {
        mContext = context;
        setContext(context);
        return ourInstance;
    }

    private DataManager() {
    }


    private Dialog mDialog;
    private boolean isProgressDialogRunning;
    private ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
    private ArrayList<SubProductModel> subProductModelArrayList = new ArrayList<>();
    private String zipCode;
    private ProductModel productModel;
    private SubProductModel subProductModel;
    private ArrayList<PlacesModel> placesModelArrayList = new ArrayList<>();
    private PlacesModel placesModel;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        DataManager.mContext = mContext;
    }


    public ArrayList<ProductModel> getProductModelArrayList() {
        return productModelArrayList;
    }

    public void setProductModelArrayList(ArrayList<ProductModel> productModelArrayList) {
        this.productModelArrayList = productModelArrayList;
    }

    public void addProductModelArrayList(ProductModel productModelArrayList) {
        this.productModelArrayList.add(productModelArrayList);
    }

    public void clearProductModelArrayList() {
        this.productModelArrayList.clear();
    }

    public void showProgressMessage(Activity dialogActivity) {
        try {
            if (isProgressDialogRunning) {
                hideProgressMessage();
            }
            isProgressDialogRunning = true;
            mDialog = new Dialog(dialogActivity);
            mDialog.setContentView(R.layout.dialog_loading);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            TextView textView = mDialog.findViewById(R.id.textView);
            textView.setText(Html.fromHtml("Loading"));
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.dimAmount = 0.8f;
            mDialog.getWindow().setAttributes(lp);
            mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hideProgressMessage() {
        isProgressDialogRunning = true;
        try {
            if (mDialog != null)
                mDialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void noInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet!")
                .setMessage("Internet Connection unavailable. Please try again later")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public ArrayList<SubProductModel> getSubProductModelArrayList() {
        return subProductModelArrayList;
    }

    public void setSubProductModelArrayList(ArrayList<SubProductModel> subProductModelArrayList) {
        this.subProductModelArrayList = subProductModelArrayList;
    }

    public void addSubProductModelArrayList(SubProductModel subProductModelArrayList) {
        this.subProductModelArrayList.add(subProductModelArrayList);
    }

    public void clearSubProductModelArrayList() {
        this.subProductModelArrayList.clear();
    }

    public ProductModel getProductModel() {
        return productModel;
    }

    public void setProductModel(ProductModel productModel) {
        this.productModel = productModel;
    }

    public SubProductModel getSubProductModel() {
        return subProductModel;
    }

    public void setSubProductModel(SubProductModel subProductModel) {
        this.subProductModel = subProductModel;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public ArrayList<PlacesModel> getPlacesModelArrayList() {
        return placesModelArrayList;
    }

    public void setPlacesModelArrayList(ArrayList<PlacesModel> placesModelArrayList) {
        this.placesModelArrayList = placesModelArrayList;
    }

    public void addPlacesModelArrayList(PlacesModel placesModelArrayList) {
        this.placesModelArrayList.add(placesModelArrayList);
    }

    public void clearPlacesModelArrayList() {
        this.placesModelArrayList.clear();
    }

    public PlacesModel getPlacesModel() {
        return placesModel;
    }

    public void setPlacesModel(PlacesModel placesModel) {
        this.placesModel = placesModel;
    }


    public double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (round(dist, 2));
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public String GetFullAddress(Context context, String lat, String lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        String address = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                address = String.valueOf(addresses.get(0).getAddressLine(0)) + " " + addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return address;
    }

}
