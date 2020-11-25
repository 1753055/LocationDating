package com.group05.mylocation;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Timeline_Day extends Fragment {
    ListView lv;
    public Timeline_Day() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timeline__day, container, false);
        lv = v.findViewById(R.id.listPlace);
        Class_Place place1 = new Class_Place("Home","62/3 Nhat Chi Mai");
        Class_Place place2 = new Class_Place("School","227 Nguyen Van Cu");
        String place[] = {"Home, 62/3 Nhat Chi Mai ","School, 227 Nguyen Van Cu"};
        Class_Place[] places = new Class_Place[]{place1,place2};
        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1 , place);

        lv.setAdapter(arrayAdapter);
        return v;
    }
}
