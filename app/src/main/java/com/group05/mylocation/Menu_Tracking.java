package com.group05.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group05.mylocation.Adapter.TrackerAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class Menu_Tracking extends Fragment implements OnMapReadyCallback {
    Spinner spinner;
    GoogleMap ggmap;
    private FirebaseAuth myAuth;
    private DatabaseReference databaseReference;
    FirebaseFirestore db;
    private Button SaveTest;
    ArrayList<Tracker> listTracker;
    ArrayList<String> ls;
    ArrayList<Marker> markers= new ArrayList<Marker>();
    FirebaseDatabase database;
    //    DatabaseReference myRef = database.getReference("message");
    // private final Map<String, MarkerOptions> mMarkers = new ConcurrentHashMap<String, MarkerOptions>();
    private TrackerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tracking, container, false);

        myAuth=FirebaseAuth.getInstance();
        spinner = v.findViewById(R.id.spinner);


        if (myAuth == null) {
            Intent intent =new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser user=myAuth.getCurrentUser();
        //add data (list tracked devices into spinner)




        //Create spinner
        ls= new ArrayList<String>();
        listTracker=new ArrayList<Tracker>();
        initMap();
        loadInfo();
        Log.d("LAST", String.valueOf(listTracker.size()));
        Log.d("LAST", String.valueOf(markers.size()));
        //setNewTrackerAdd();
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ggmap = googleMap;
        //LatLng pp = new LatLng(11.5448729, 104.8921668);
        //MarkerOptions option = new MarkerOptions();
        //option.position(pp).title("phnom");
        //ggmap.addMarker(option);
        //ggmap.moveCamera(CameraUpdateFactory.newLatLng(pp));

    }

    private void initMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    public void loadInfo(){
        FirebaseUser user=myAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        Log.d("Hello",user.getUid());

        ArrayList<Tracker> list=new ArrayList<Tracker>();

        Log.d("Trackingssss", "onComplete: " + user.getUid());

        //Get Tracker
        db.collection("User").document(user.getUid()).collection("connection")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            database=FirebaseDatabase.getInstance();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d("Trackingssss", "onComplete: " + document.toString());
                                if (document.get("isAccepted", boolean.class) == true) {
                                    //asdfgh

                                    DatabaseReference myRef = database.getReference("LocationHistory/" + document.getId());
                                    myRef.addChildEventListener(childEventListener);
                                    Query lastQuery = myRef.orderByKey().limitToLast(1);
                                    //Get last child in LocationHistory
//                                    final int finalI = i;
                                    lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String message = dataSnapshot.getValue().toString();
                                            char[] key = new char[20];
                                            message.getChars(1, 21, key, 0);
                                            Log.d("bam", String.valueOf(key));
                                            double latitude = (double) dataSnapshot.child(String.valueOf(key)).child("latitude").getValue();
                                            double longitude = (double) dataSnapshot.child(String.valueOf(key)).child("longitude").getValue();
                                            Log.d("conca", String.valueOf(longitude) + String.valueOf(latitude));
                                            Tracker temp = new Tracker(dataSnapshot.getKey(), document.get("fullname").toString(), latitude, longitude);
                                            listTracker.add(temp);
                                            LatLng position = new LatLng(temp.getLocationX(), temp.getLocationY());
                                            Log.d("error", String.valueOf(listTracker.size()));
                                            markers.add(ggmap.addMarker(new MarkerOptions().position(position).title(temp.getName())));
                                            ggmap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 2f));
                                            adapter = new TrackerAdapter(getContext(), R.layout.tracker, listTracker);
                                            Log.d("error", String.valueOf(listTracker.size()));
                                            spinner.setAdapter(adapter);
                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Handle possible errors.
                                        }
                                    });
                                }
                            }


                            Log.d("Error","aksjhdkfhaksf");
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Here you get the current item (a User object) that is selected by its position
                                    Tracker tracker = adapter.getItem(position);
                                    Log.d("Erooadf", tracker.getName());
                                    // Here you can do the action you want to...
                                    //Toast.makeText(getContext(), "ID: " + tracker.getUid() + "\nName: " + tracker.getName()+"\nLocationX: "+ tracker.getLocationX()+"\nLocationY: "+ tracker.getLocationY(),
                                    //Toast.LENGTH_SHORT).show();
//                                    setPosition(temp);
                                    LatLng positionLat = new LatLng(tracker.getLocationX(), tracker.getLocationY());

                                    ggmap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLat, 15));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Log.d("Hello", "get failed with ", task.getException());
                        }

                    }
                });



    }


    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("hello", "onChildAdded:" + dataSnapshot.getRef().getParent().getKey());

            // A new comment has been added, add it to the displayed list
            String message =dataSnapshot.getValue().toString();
            Log.d("key", "onChildAdded:" + message);
            double latitude= (double) dataSnapshot.child("latitude").getValue();
            double longitude= (double) dataSnapshot.child("longitude").getValue();
            for(int i=0 ; i<markers.size();i++){
                if(markers.get(i).getTitle().equals(dataSnapshot.getRef().getParent().getKey())){
                    Log.d("ERROR","inside:"+ dataSnapshot.getKey()+"/"+latitude+"/"+longitude);
                    LatLng newPosition = new LatLng(latitude,longitude);
                    markers.get(i).setPosition(newPosition);
                    adapter.getItem(i).setLocationX(latitude);
                    adapter.getItem(i).setLocationY(longitude);
                }
            }

            // ...
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };

    public void setPosition(Tracker tracker){
        LatLng position= new LatLng(tracker.getLocationX(),tracker.getLocationY());
        Marker temp= ggmap.addMarker(new MarkerOptions().position(position).title(tracker.getName()));
        markers.add(temp);
        //ggmap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15));

    }

    private ArrayList<String> listProcess(ArrayList<String> ls, int flat){
        ArrayList<String> result= new ArrayList<>();
        if(ls!=null) {
            for (String l : ls) {
                result.add(l.split("-")[flat]);
            }

        }
        return result;
    }

}