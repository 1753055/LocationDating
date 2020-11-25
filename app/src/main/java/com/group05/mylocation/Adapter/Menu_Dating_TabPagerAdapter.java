package com.group05.mylocation.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.group05.mylocation.Fragments.Chats;
import com.group05.mylocation.Fragments.Menu_Dating_Match;
import com.group05.mylocation.Menu_Dating__Chat;

public class Menu_Dating_TabPagerAdapter extends FragmentStatePagerAdapter {
    String[] tabarray = new String[] {"Match","Chat","Settings"};
    Integer tabNum;
    public Menu_Dating_TabPagerAdapter(@NonNull FragmentManager fm, int behavior) {
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
                return new Menu_Dating_Match();
            case 1:
                return new Chats();
            case 2:
                return new Menu_Dating__Chat();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNum;
    }
}
