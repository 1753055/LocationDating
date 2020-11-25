package com.group05.mylocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.group05.mylocation.Adapter.Menu_Dating_TabPagerAdapter;

public class Menu_Dating extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dating, container, false);
        tabLayout = v.findViewById(R.id.datingTab);
        viewPager = v.findViewById(R.id.viewpagerDating);

        Menu_Dating_TabPagerAdapter adapter = new Menu_Dating_TabPagerAdapter(getActivity().getSupportFragmentManager(), 3);
        
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

}