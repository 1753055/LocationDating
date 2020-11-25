package com.group05.mylocation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Timeline_Places extends Fragment {
    ListView lvCity;
    ArrayList<Timeline_Places_CityClass> arrayCity;
    public Timeline_Places() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timeline__places, container, false);

        lvCity = v.findViewById(R.id.listviewCity);
        arrayCity = new ArrayList<Timeline_Places_CityClass>();
        //add data
        arrayCity.add(new Timeline_Places_CityClass("Da Lat city","Viet Nam","3/5/2020"));
        arrayCity.add(new Timeline_Places_CityClass("Ho Chi Minh city", "Viet Nam", "19/01/2020"));
        arrayCity.add(new Timeline_Places_CityClass("Ho Chi Minh city", "Viet Nam", "19/01/2020"));
        arrayCity.add(new Timeline_Places_CityClass("Ho Chi Minh city", "Viet Nam", "19/01/2020"));
        arrayCity.add(new Timeline_Places_CityClass("Ho Chi Minh city", "Viet Nam", "19/01/2020"));

        Timeline_Places_CityClass_Adapter adapter = new Timeline_Places_CityClass_Adapter(
                this.getContext(),
                R.layout.fragment_timeline__places___citylistview,
                arrayCity
        );

        lvCity.setAdapter(adapter);
        return v;
    }
}
