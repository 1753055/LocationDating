package com.group05.mylocation.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.group05.mylocation.Modal.LocationHistory;
import com.group05.mylocation.R;

import java.util.ArrayList;

public class LocationHistoryAdapter extends BaseAdapter {

    private ArrayList<LocationHistory> list;

    public LocationHistoryAdapter() {
    }

    public LocationHistoryAdapter(ArrayList<LocationHistory> listProduct) {
        this.list = listProduct;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(getCount() - position -1);
    }

    @Override
    public long getItemId(int position) {
        return list.indexOf(list.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = View.inflate(parent.getContext(), R.layout.location_history_item, null);
        }else{
            view = convertView;
        }
        LocationHistory locationHistory = (LocationHistory) getItem(position);
        if(locationHistory.getCountry() == null){
            ((TextView) view.findViewById(R.id.location_history_item)).setText(locationHistory.getCity() + "\n"+locationHistory.getAddress().toString());
        }
        else ((TextView) view.findViewById(R.id.location_history_item)).setText(locationHistory.getAddress().toString());
        ((TextView) view.findViewById(R.id.date)).setText(locationHistory.getDate().toString());
        return view;
    }
}
