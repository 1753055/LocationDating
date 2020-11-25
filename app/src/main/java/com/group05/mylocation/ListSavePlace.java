package com.group05.mylocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group05.mylocation.Adapter.LocationHistoryAdapter;
import com.group05.mylocation.Interface.OnFragmentManager;
import com.group05.mylocation.Modal.LocationHistory;

import java.util.ArrayList;

public class ListSavePlace extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    TextView txtMsg;
    ListView myListView;
    private ArrayList<LocationHistory> mLocationList;
    private LocationHistoryAdapter locationHistoryAdapter;

    private OnFragmentManager onFragmentManager;
    //TextView

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_save_place, container, false);
        super.onCreate(savedInstanceState);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = myAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        myListView = v.findViewById(R.id.lvPlace);
        mLocationList = new ArrayList<>();
        LocationHistory initLoc = new LocationHistory();
        mLocationList.add(initLoc);

        db.collection(("User/"+uid+"/SavedPlace"))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mLocationList.clear();
                        for (QueryDocumentSnapshot ds: queryDocumentSnapshots){
                            mLocationList.add(ds.toObject(LocationHistory.class));
                            locationHistoryAdapter = new LocationHistoryAdapter(mLocationList);
                            myListView.setAdapter(locationHistoryAdapter);
                        }
                    }
                });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {// move camera to selected location
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationHistory item = mLocationList.get(mLocationList.size()-position-1);
                onFragmentManager.onClickLocationHistory(item.getLatitude(), item.getLongitude(), item.getCity());
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