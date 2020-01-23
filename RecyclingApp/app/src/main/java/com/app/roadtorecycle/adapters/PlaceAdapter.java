package com.app.roadtorecycle.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.roadtorecycle.appmanager.DataManager;
import com.app.roadtorecycle.models.PlacesModel;
import com.app.roadtorecycle.R;

import java.util.ArrayList;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlacesModel> placesModelArrayList = new ArrayList<>();

    public PlaceAdapter(Context context, ArrayList<PlacesModel> placesModels) {
        this.context = context;
        this.placesModelArrayList = placesModels;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.centerNameAddress.setText(placesModelArrayList.get(position).getCenterName() + " located at " + placesModelArrayList.get(position).getCenterAddress());
        holder.centerHours.setText("This Place is open from " + placesModelArrayList.get(position).getCenterHours());
        holder.centerHours.setVisibility(View.GONE);
        holder.centerUrl.setText(placesModelArrayList.get(position).getCenterUrl());
        holder.centerPhone.setText(placesModelArrayList.get(position).getCenterPhone());
        holder.distance.setText("Distance from you: " + placesModelArrayList.get(position).getCenterDistanceMiles());
        holder.openingRecycle.setText(placesModelArrayList.get(position).getCenterOpeningDays());

    }

    @Override
    public int getItemCount() {
        return placesModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView distance, centerPhone, centerUrl, centerNameAddress, centerHours, openingHours, openingRecycle;
        ImageButton mapRedirect;
        ViewHolder(View itemView) {
            super(itemView);
            mapRedirect = itemView.findViewById(R.id.mapRedirect);
            centerHours = itemView.findViewById(R.id.centerHours);
            centerNameAddress = itemView.findViewById(R.id.centerNameAddress);
            centerPhone = itemView.findViewById(R.id.centerPhone);
            distance = itemView.findViewById(R.id.distance);
            centerUrl = itemView.findViewById(R.id.centerUrl);
            openingHours = itemView.findViewById(R.id.openingHours);
            openingRecycle = itemView.findViewById(R.id.openingRecycle);

            openingHours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (openingRecycle.getVisibility() == View.GONE) {
                        openingRecycle.setVisibility(View.VISIBLE);
                    } else {
                        openingRecycle.setVisibility(View.GONE);
                    }
                }
            });

            mapRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleMapIntent(placesModelArrayList.get(getAdapterPosition()));
                }
            });

        }
    }

    private void googleMapIntent(PlacesModel placesModel){

        String url = "http://maps.google.com/maps?saddr="+ DataManager.getInstance(context).GetFullAddress(context,placesModel.getCurrentLatitude(),placesModel.getCurrentLongitude())+"&daddr="+placesModel.getCenterAddress();
//        String url = "http://maps.google.co.in/maps?q=" + placesModel.getCenterAddress();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        context.startActivity(intent);
    }
}
