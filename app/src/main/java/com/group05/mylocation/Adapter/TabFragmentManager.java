package com.group05.mylocation.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.group05.mylocation.Fragments.Chats;

public class TabFragmentManager extends FragmentPagerAdapter
{

    public TabFragmentManager(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                Chats chatsFragment = new Chats();
                return chatsFragment;
//            case 1:
//                ContactsFragment contactsFragment = new ContactsFragment();
//                return contactsFragment;


            default:
                return null;
        }
    }


    @Override
    public int getCount()
    {
        return 4;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Chats";

            case 1:
                return "Contacts";


            default:
                return null;
        }
    }
}