package com.group05.mylocation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group05.mylocation.Adapter.LocationHistoryAdapter;
import com.group05.mylocation.Interface.OnFragmentManager;
import com.group05.mylocation.Modal.LocationHistory;

import java.util.ArrayList;

public class Menu_Timeline extends Fragment {
    private static final String TAG = "LocationHistory";
    private ArrayList<LocationHistory> mLocationList;
    private ListView locations;
    private LocationHistoryAdapter locationHistoryAdapter;
    private OnFragmentManager onFragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timeline, container, false);
        super.onCreate(savedInstanceState);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = myAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LocationHistory/" + uid);
        locations = v.findViewById(R.id.location_list);
        mLocationList = new ArrayList<>();
        LocationHistory initLoc = new LocationHistory();
        mLocationList.add(initLoc);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLocationList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    LocationHistory locationHistory = ds.getValue(LocationHistory.class);
                    mLocationList.add(locationHistory);
                    locationHistoryAdapter = new LocationHistoryAdapter(mLocationList);
                    locations.setAdapter(locationHistoryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {// move camera to selected location
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationHistory item = mLocationList.get(mLocationList.size()-position-1);
                onFragmentManager.onClickLocationHistory(item.getLatitude(), item.getLongitude(), item.getAddress());
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            this.onFragmentManager = (OnFragmentManager) context; // gan listener vao MainActivity
        else
            throw new RuntimeException(context.toString() + " must implement onViewSelected!");
    }


}

