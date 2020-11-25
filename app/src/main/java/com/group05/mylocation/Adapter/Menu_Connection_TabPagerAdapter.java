package com.group05.mylocation.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.group05.mylocation.Fragments.Connection;
import com.group05.mylocation.Fragments.Request;

public class Menu_Connection_TabPagerAdapter extends FragmentStatePagerAdapter {
    String[] tabarray = new String[] {"Connections","Requests"};
    Integer tabNum;
    public Menu_Connection_TabPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabNum = behavior;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabarray[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new Connection();
            case 1:
                return new Request();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNum;
    }
}
