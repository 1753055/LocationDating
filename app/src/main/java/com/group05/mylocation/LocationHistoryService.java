package com.group05.mylocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class LocationHistoryService extends Service {

    private static final String TAG = "LocationService";
    Geocoder geocoder;
    private static DatabaseReference databaseReference;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private final static long FASTEST_INTERVAL = 5 * 1000; /* 5 sec */
    private final static long DEFAULT_STAY_TIME = 5; // minutes, stay at a location for 5 minute => history location
    private static final int NOTIFICATION_ID = 12345678;
    @Nullable
    private final IBinder mBinder = new IBinder() {
        @Nullable
        @Override
        public String getInterfaceDescriptor() throws RemoteException {
            return null;
        }

        @Override
        public boolean pingBinder() {
            return false;
        }

        @Override
        public boolean isBinderAlive() {
            return false;
        }

        @Nullable
        @Override
        public IInterface queryLocalInterface(@NonNull String descriptor) {
            return null;
        }

        @Override
        public void dump(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {

        }

        @Override
        public void dumpAsync(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {

        }

        @Override
        public boolean transact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            return false;
        }

        @Override
        public void linkToDeath(@NonNull DeathRecipient recipient, int flags) throws RemoteException {

        }

        @Override
        public boolean unlinkToDeath(@NonNull DeathRecipient recipient, int flags) {
            return false;
        }
    };
    private boolean mChangingConfiguration = false;
    private Handler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration) {
//            Log.i(TAG, "Starting foreground service");
//            /*
//            // TODO(developer). If targeting O, use the following code.
//            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
//                mNotificationManager.startServiceInForeground(new Intent(this,
//                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
//            } else {
//                startForeground(NOTIFICATION_ID, getNotification());
//            }
//             */
//            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        geocoder = new Geocoder(this, Locale.getDefault());
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "getLocation: stopping the location service.");
//            stopSelf();
//            return;
//        }
        Log.d(TAG, "getLocation: getting location information.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
//                        Log.d(TAG, "onLocationResult: got location result.");
                        final Location location = locationResult.getLastLocation();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            dbProcess(location);
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            dbProcess(mLastLocation);
            Log.d(TAG, "duplicated");
        }
    };

    private void dbProcess(final Location location){
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        List<Address> tmplist = null;
        try {
            tmplist = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (Exception e) {
            try {
                tmplist = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        final List<Address> addresses = tmplist;
        if (myAuth.getCurrentUser() != null && addresses!=null) {
            final String uid = myAuth.getCurrentUser().getUid();
            Query lastQuery = databaseReference.child("LocationHistory/" + uid).orderByKey().limitToLast(1);
            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double lastLat = 0;
                    double lastLong = 0;
                    Date lastDate = null;
                    String lastKey = null;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        lastLat = ds.child("latitude").getValue(Double.class);
                        lastLong = ds.child("longitude").getValue(Double.class);
                        lastDate = ds.child("date").getValue(Date.class);
                        lastKey = ds.getKey();
                    }
                    Location targetLocation = new Location("");//provider name is unnecessary
                    targetLocation.setLatitude(lastLat);
                    targetLocation.setLongitude(lastLong);

                    float distanceInMeters = targetLocation.distanceTo(location);
                    Date currentTime = Calendar.getInstance().getTime();
                    long stayTime = 0;

                    if (lastDate == null && distanceInMeters < 15) {

                    } else if (distanceInMeters > 15 || lastDate == null) {//save temp

                        if (lastDate != null && distanceInMeters > 15) {
                            stayTime = (currentTime.getTime() - lastDate.getTime()) / 60000;
                            if (stayTime < DEFAULT_STAY_TIME) {//remove last location
                                databaseReference.child("LocationHistory/" + uid).child(lastKey).removeValue();
                            } else { // save last loc as history loc
                                List<Address> last_addresses = null;
                                try {
                                    last_addresses = geocoder.getFromLocation(lastLat, lastLong, 1);
                                } catch (Exception ignored) {

                                }
                                //update last
                                String address = "location No Address.";
                                HashMap<String, Object> tmpUpdates = new HashMap<>();
                                if(last_addresses!= null &&last_addresses.size()>0)
                                    tmpUpdates.put("address", last_addresses.get(0).getAddressLine(0));
                                else
                                    tmpUpdates.put("address", address);
                                databaseReference.child("LocationHistory/" + uid).child(lastKey).updateChildren(tmpUpdates);
                            }
                        }
                        // make new temp loc

                        String address = "Current location No Address.";
                        String city = "No City";
                        String country = "No Country";
                        if(addresses!=null && addresses.size()>0){
                            address = "Current location " + addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            city = addresses.get(0).getLocality();
                            country = addresses.get(0).getCountryName();
                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("latitude", location.getLatitude());
                        hashMap.put("longitude", location.getLongitude());
                        hashMap.put("address", address);
                        hashMap.put("city", city);
                        hashMap.put("country", country);
                        hashMap.put("date", currentTime);
                        if (lastLat == 0.0 && lastLong == 0.0)
                            distanceInMeters = 0;
                        hashMap.put("distance", distanceInMeters);
                        databaseReference.child("LocationHistory/" + uid).push().setValue(hashMap);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    }
}