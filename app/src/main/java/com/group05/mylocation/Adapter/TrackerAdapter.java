package com.group05.mylocation.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.group05.mylocation.R;
import com.group05.mylocation.Tracker;

import java.util.ArrayList;

public class TrackerAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Tracker> list;
    public TrackerAdapter(Context context,int textViewResourceId, ArrayList<Tracker>list){
        super(context,textViewResourceId,list);
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount(){ return list.size();}
    public Tracker getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=((Activity) context).getLayoutInflater();
        View row=inflater.inflate(R.layout.tracker,null);
        TextView name=(TextView) row.findViewById(R.id.tracker_name);
        name.setText(list.get(position).getName());


        return row;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
//        TextView label =(TextView) super.getDropDownView(position,convertView,parent);
//        label.setText(list.get(position).getName());
//        return label;
        LayoutInflater inflater=((Activity) context).getLayoutInflater();
        View row=inflater.inflate(R.layout.tracker,null);
        TextView name=(TextView) row.findViewById(R.id.tracker_name);
        name.setText(list.get(position).getName());

        return row;
    }
}
