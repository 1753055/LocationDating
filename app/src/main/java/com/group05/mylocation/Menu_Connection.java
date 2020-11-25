package com.group05.mylocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.group05.mylocation.Adapter.Menu_Connection_TabPagerAdapter;
import com.group05.mylocation.Adapter.Menu_Dating_TabPagerAdapter;

public class Menu_Connection extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.connection, container, false);
        tabLayout = v.findViewById(R.id.datingTab);
        viewPager = v.findViewById(R.id.viewpagerConnection);

        Menu_Connection_TabPagerAdapter adapter = new Menu_Connection_TabPagerAdapter(getActivity().getSupportFragmentManager(), 2);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

}