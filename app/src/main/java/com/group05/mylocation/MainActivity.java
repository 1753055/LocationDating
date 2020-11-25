package com.group05.mylocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group05.mylocation.Interface.OnFragmentManager;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentManager {
    private DrawerLayout drawer;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    int PERMISSION_ID = 44;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef;
    FirebaseAuth fAuth;
    StorageReference storageReference;

    ImageView imgHeader;
    TextView fullname;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent (MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);



        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //setup tab pager TIMELINE fragment

        /////////////////////////

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            toolbar.setTitle("Home");
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid().toString();

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");

        DocumentReference docRef = db.collection("User").document(uId);
        docRef.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                imgHeader = (ImageView) navigationView.findViewById(R.id.imgProfileHeader);
                fullname = (TextView) navigationView.findViewById(R.id.emailHeader);
                email = (TextView) navigationView.findViewById(R.id.fullnameHeader);
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgHeader);
                    }
                });
                if (document.exists()){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            User user = User.MapToUser(document.getData(), document.getId());
                            email.setText(user.getEmail());
                            fullname.setText(user.getFullName());
                        }
                    },1000);

                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        startService();
    }

    public void startService(){

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                Intent serviceIntent = new Intent(this, LocationHistoryService.class);
//                serviceIntent.putExtra("context", getBaseContext());
                ContextCompat.startForegroundService(this, serviceIntent);
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//
//                    MainActivity.this.startForegroundService(serviceIntent);
//                }else{
//                    startService(serviceIntent);
//                }
            } else {
                Toast.makeText(this, " Please Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }


    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                toolbar.setTitle("Home");
                break;
            case R.id.nav_timeline:
                Menu_Timeline timelineFrag = new Menu_Timeline();
                FragmentManager manager3 = getSupportFragmentManager();
                manager3.beginTransaction().replace(R.id.fragment_container, timelineFrag).commit();
                toolbar.setTitle("Home > My timeline");
                break;
            case R.id.nav_statistic:
                Menu_Statistic statisticFrag = new Menu_Statistic();
                FragmentManager manager4 = getSupportFragmentManager();
                manager4.beginTransaction().replace(R.id.fragment_container, statisticFrag).commit();
                toolbar.setTitle("Home > Statistic");
                break;
            case R.id.nav_connection:
                Menu_Connection connectionFrag = new Menu_Connection();
                FragmentManager manager5 = getSupportFragmentManager();
                manager5.beginTransaction().replace(R.id.fragment_container, connectionFrag).commit();
                toolbar.setTitle("Home > Connection");
                break;
            case R.id.nav_dating:
                Menu_Dating datingFrag = new Menu_Dating();
                FragmentManager manager7 = getSupportFragmentManager();
                manager7.beginTransaction().replace(R.id.fragment_container, datingFrag).commit();
                toolbar.setTitle("Home > Dating");
                break;
            case R.id.nav_signout:
                Intent signoutIntent = new Intent(MainActivity.this,LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(signoutIntent);
                break;
            case R.id.nav_devices:
                Menu_Tracking trackingFrag = new Menu_Tracking();
                FragmentManager manager6 = getSupportFragmentManager();
                manager6.beginTransaction().replace(R.id.fragment_container, trackingFrag).commit();
                toolbar.setTitle("Home > Devices");
                break;
                case R.id.nav_account:
                ProfileUser menuFrag = new ProfileUser();
                FragmentManager manager2 = getSupportFragmentManager();
                manager2.beginTransaction().replace(R.id.fragment_container, menuFrag).commit();
                toolbar.setTitle("Home > Account");
                break;
            case R.id.nav_saveplace:
                ListSavePlace savePlaceFrag = new ListSavePlace();
                FragmentManager manager23 = getSupportFragmentManager();
                manager23.beginTransaction().replace(R.id.fragment_container, savePlaceFrag).commit();
                toolbar.setTitle("Home > Saved place");
                break;
            case R.id.nav_about:
                About about = new About();
                FragmentManager manager8 = getSupportFragmentManager();
                manager8.beginTransaction().replace(R.id.fragment_container, about).commit();
                toolbar.setTitle("Home > About us");
                break;

        }
        drawer.closeDrawer(Gravity.LEFT);
        return true;
    }

    @Override
    public void onClickLocationHistory(double lat, double lon, String address) {
//        HomeFragment f1 = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.nav_home);
        HomeFragment f1 = HomeFragment.getInstance(address, lat, lon);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, f1).commit();
        toolbar.setTitle("Home");
        Log.d("dinh", "dfdashfd");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                f1.moveCamera();
            }
        }, 2000);

    }


}

