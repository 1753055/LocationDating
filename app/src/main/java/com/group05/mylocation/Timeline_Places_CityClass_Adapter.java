package com.group05.mylocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Timeline_Places_CityClass_Adapter extends BaseAdapter {

    Context myContext;
    int myLayout;
    List<Timeline_Places_CityClass> arrayCity;
    public Timeline_Places_CityClass_Adapter(Context context, int layout, List<Timeline_Places_CityClass> cityList) {
        myContext = context;
        myLayout = layout;
        arrayCity = cityList;
    }

    @Override
    public int getCount() {
        return arrayCity.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(myLayout, null);

        //mapping
        TextView txtName = (TextView) convertView.findViewById(R.id.cityName);
        TextView txtCountry = (TextView) convertView.findViewById(R.id.cityCountry);
        TextView txtLasttime = (TextView) convertView.findViewById(R.id.cityLasttime);
        ImageView img = (ImageView) convertView.findViewById(R.id.cityImage);

        txtName.setText(arrayCity.get(position).name);
        txtCountry.setText(arrayCity.get(position).country);
        txtLasttime.setText(arrayCity.get(position).lasttime);
        //set image

        //

        return convertView;
    }
}
