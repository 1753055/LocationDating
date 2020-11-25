package com.group05.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class Menu_Account extends Fragment {
    Button btnProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account, container, false);

        btnProfile = (Button) v.findViewById(R.id.btnViewProfile);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu_Account.this.getActivity(), ProfileUser.class);
                startActivityForResult(intent, 1000);
            }
        });

        return v;
    }

}
