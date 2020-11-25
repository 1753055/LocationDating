package com.group05.mylocation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchBar extends Fragment {
    ImageView menu;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle save){
        context=getActivity();
        LinearLayout layout_search = (LinearLayout) inflater.inflate(R.layout.search_bar_layout,container,false);

        return layout_search;
    }
}
