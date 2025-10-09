package com.example.lab5_starter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityViewHolder> {

    private ArrayList<City> cityList;
    private OnCityClickListener listener;


    public interface OnCityClickListener {
        void onCityClick(City city);
    }


    public CityRecyclerAdapter(ArrayList<City> cityList, OnCityClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.cityName.setText(city.getName());
        holder.provinceName.setText(city.getProvince());

        holder.itemView.setOnClickListener(v -> listener.onCityClick(city));
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }


    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityName, provinceName;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.textCityName);
            provinceName = itemView.findViewById(R.id.textCityProvince);
        }
    }
}