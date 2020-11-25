package com.group05.mylocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class tabpagerAdapter_Timeline extends FragmentPagerAdapter {
    String[] tabarray = new String[] {"Day","Places"};
    Integer tabNum;

    public tabpagerAdapter_Timeline(@NonNull FragmentManager fm, int behavior) {
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
                Timeline_Day day = new Timeline_Day();
                return day;
            case 1:
                Timeline_Places places = new Timeline_Places();
                return places;
        }
        return null;

    }

    @Override
    public int getCount() {
        return tabNum;
    }
}
