package com.app.roadtorecycle.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.roadtorecycle.activities.CategoryActivity;
import com.app.roadtorecycle.activities.SubCategoryActivity;
import com.app.roadtorecycle.appmanager.DataManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.app.roadtorecycle.R;
import com.app.roadtorecycle.models.ProductModel;

import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ProductModel> productModelArrayList = new ArrayList<>();

    public ProductAdapter(Context context, ArrayList<ProductModel> productModelArrayList) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if (isOdd(productModelArrayList.size())) {
            if (position == 9) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels / 8;
                params.setMargins(width, 0, 0, 0);
                holder.catIbBtn.setLayoutParams(params);
            } else {
                holder.catIbBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                holder.catBtn.setText(productModelArrayList.get(position).getProductName());
            }
        } else {
            holder.catIbBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.catBtn.setText(productModelArrayList.get(position).getProductName());
        }
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions().error(R.drawable.app_icon))
                .load(productModelArrayList.get(position).getProductImg())
                .into(holder.catIbBtn);

        holder.catIbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataManager.getInstance(context).isNetworkAvailable(context)) {
                    DataManager.getInstance(context).setProductModel(productModelArrayList.get(position));
                    CategoryActivity.searchItemEd.setText("");
                    context.startActivity(new Intent(context, SubCategoryActivity.class));
                } else {
                    DataManager.getInstance(context).noInternetDialog(context);
                }
            }
        });
    }

    boolean isOdd(int val) {
        return (val & 0x01) != 0;
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button catBtn;
        ImageButton catIbBtn;

        ViewHolder(View itemView) {
            super(itemView);
            catBtn = itemView.findViewById(R.id.catBtn);
            catIbBtn = itemView.findViewById(R.id.catIbBtn);

        }
    }
}
