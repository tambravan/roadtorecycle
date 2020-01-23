package com.app.roadtorecycle.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.roadtorecycle.activities.CategoryActivity;
import com.app.roadtorecycle.activities.ItemDetailActivity;
import com.app.roadtorecycle.appmanager.DataManager;
import com.app.roadtorecycle.models.SubProductModel;
import com.app.roadtorecycle.R;

import java.util.ArrayList;


public class SubProductAdapter extends RecyclerView.Adapter<SubProductAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SubProductModel> subProductModelArrayList;

    public SubProductAdapter(Context context, ArrayList<SubProductModel> subProductModels) {
        this.context = context;
        this.subProductModelArrayList = subProductModels;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.subCatTxt.setText(subProductModelArrayList.get(position).getProductName());
        holder.subCatTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataManager.getInstance(context).isNetworkAvailable(context)) {
                    DataManager.getInstance(context).setSubProductModel(subProductModelArrayList.get(position));
                    CategoryActivity.searchItemEd.setText("");
                    context.startActivity(new Intent(context, ItemDetailActivity.class));
                    Log.e("onClick: ", "name" + context.getClass().getName());
                   /* if (!context.getClass().getName().equalsIgnoreCase("com.app.recycle.activities.CategoryActivity")) {
                        ((Activity) context).finish();
                    }*/
                } else {
                    DataManager.getInstance(context).noInternetDialog(context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subProductModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView subCatTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            subCatTxt = itemView.findViewById(R.id.subCatTxt);

        }
    }
}
