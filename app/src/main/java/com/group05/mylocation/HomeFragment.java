package com.group05.mylocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.group05.mylocation.Modal.LocationHistory;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfMeasurement;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/////////////////////////////////////
// classes needed to initialize map
// classes needed to add the location component
// classes needed to add a marker
// classes to calculate a route
//import retrofit2.Callback;
// classes needed to launch navigation UI
//autocomplete
public class HomeFragment extends Fragment
        implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener{
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1Ijoia2hhbmhkdXkiLCJhIjoiY2thdjVndWJhMWdkcjJ4cWZzd2p2dGthdiJ9.lkspJEIi9KP1ug0Hc28VCQ";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private Button SearchFab;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables location point
    private Point oriPoint;
    private Point desPoint;
    // variables needed to initialize navigation
    private ImageView button;
    private ImageButton btnClose;
    private ImageView savePlace;

    //vars
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionsGranted = false;
    private PlaceAutocompleteAdapterNew placeAutocompleteAdapter;
    private PlacesClient placesClient;
    AutocompleteSessionToken autocompleteSessionToken;
    //widgets
    GoogleMap ggmap;
    AutoCompleteTextView search;
    ImageView mGps;
    Fragment searchRes;
    Button btnDirect;
    Location place1, place2;
    MarkerOptions markerPlace1, markerPlace2;
    LinearLayout placeinfo;
    ImageView moreInfo;
    ImageView closeMoreInfo;

    TextView placeName;
    TextView placeCountry;
    TextView distance;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private double historyLat ;
    private  double historyLon;
    private String historyAddress;
    HomeFragment(){}
    HomeFragment(String historyAddress, double a, double b){
        this.historyAddress = historyAddress;
        historyLat = a;
        historyLon = b;
    }
    public static HomeFragment getInstance(String address, double a, double b){
        return new HomeFragment(address, a , b);
    }
    public void moveCamera(){
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(historyLat,
                                historyLon))
                        .zoom(14)
                        .build()), 4000);

        com.mapbox.mapboxsdk.annotations.MarkerOptions options = new com.mapbox.mapboxsdk.annotations.MarkerOptions();
        options.title(historyAddress);
        options.position(new LatLng(historyLat,historyLon));
        mapboxMap.addMarker(options);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(this.getContext(), "pk.eyJ1Ijoia2hhbmhkdXkiLCJhIjoiY2thdjVndWJhMWdkcjJ4cWZzd2p2dGthdiJ9.lkspJEIi9KP1ug0Hc28VCQ");
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        button = v.findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean simulateRoute = false;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();
                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(getActivity(), options);
            }
        });

        savePlace = v.findViewById(R.id.savePlace);
        savePlace.setVisibility(View.INVISIBLE);

        mGps = v.findViewById(R.id.gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                                        locationComponent.getLastKnownLocation().getLongitude()))
                                .zoom(14)
                                .build()), 4000);
            }
        });
        moreInfo = v.findViewById(R.id.moreInfo);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeinfo.setVisibility(View.VISIBLE);
            }
        });

        closeMoreInfo = v.findViewById(R.id.closePlaceInfo);
        closeMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeinfo.setVisibility(View.GONE);
            }
        });

        v.findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10).country("VN")
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
        placeinfo = v.findViewById(R.id.informationPlace);
        placeinfo.setVisibility(View.GONE);

        placeName = v.findViewById(R.id.placeName);
        placeCountry = v.findViewById(R.id.placeCountry);
        distance = v.findViewById(R.id.placeDistance);
        return v;
    }
    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);

                addUserLocations();

                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        getActivity().getResources(), R.drawable.map_default_map_marker));

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        savePlace.setVisibility(View.VISIBLE);
                        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                locationComponent.getLastKnownLocation().getLatitude());

                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
                        if (source != null) {
                            source.setGeoJson(Feature.fromGeometry(destinationPoint));
                        }
                        getRoute(originPoint, destinationPoint);
                        button.setEnabled(true);
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> address =  geocoder.getFromLocation(destinationPoint.latitude(), destinationPoint.longitude(), 1);
                            String realaddress = address.get(0).getAddressLine(0);
                            String knownName = address.get(0).getFeatureName();
                            String countryName = address.get(0).getCountryName();
                            double tempDistance = TurfMeasurement.distance(originPoint, destinationPoint);
                            tempDistance = Math.round(tempDistance);
                            placeName.setText(realaddress);
                            placeCountry.setText(countryName);
                            distance.setText("Distance: " + String.valueOf(tempDistance) + "km");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final String[] namePlace = new String[1];
                        savePlace.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder ad = new AlertDialog.Builder(HomeFragment.this.getActivity());

                                ad.setTitle("Enter name");
                                ad.setMessage("");

                                final EditText input = new EditText(HomeFragment.this.getActivity());
                                ad.setView(input);

                                ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int which) {
                                        String val = input.getText().toString();
                                        //String msg = String.format("Hello %s!", val);
                                        namePlace[0] = val;
                                        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                                                .accessToken("pk.eyJ1Ijoia2hhbmhkdXkiLCJhIjoiY2thdjVndWJhMWdkcjJ4cWZzd2p2dGthdiJ9.lkspJEIi9KP1ug0Hc28VCQ")
                                                .query(Point.fromLngLat(point.getLongitude(),point.getLatitude()))
                                                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                                                .build();
                                        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
                                            @Override
                                            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                                                List<CarmenFeature> res = response.body().features();

                                                if(res.size()>0){
                                                    Log.d(TAG, res.toString());
                                                    LocationHistory sp = new LocationHistory(point.getLongitude(), point.getLatitude(), res.get(0).placeName(), namePlace[0]);
                                                    db.collection("User").document(currentFirebaseUser.getUid().toString()).collection("SavedPlace").document(namePlace[0]).set(sp);
                                                    Toast.makeText(HomeFragment.this.getActivity(), "Save place succeed", Toast.LENGTH_SHORT).show();
                                                } else{
                                                    List<Address> tmplist = null;
                                                    try {
                                                        tmplist = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                                                    } catch (Exception e) {
                                                        try {
                                                            tmplist = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                                                        } catch (IOException ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    }
                                                    LocationHistory sp = new LocationHistory(point.getLongitude(), point.getLatitude(), tmplist.get(0).getAddressLine(0), namePlace[0]);
                                                    db.collection("User").document(currentFirebaseUser.getUid().toString()).collection("SavedPlace").document(namePlace[0]).set(sp);
                                                    Toast.makeText(HomeFragment.this.getActivity(), "Save place succeed", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                                                Log.d(TAG, "no address found mapbox");
                                            }
                                        });

                                    }
                                });

                                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int which) {
                                        dlg.cancel();
                                    }
                                });

                                ad.show();
                                //SavePlaceClass sp = new SavePlaceClass(Double.toString(point.getLongitude()), Double.toString(point.getLatitude()));
                                //db.collection("User").document(currentFirebaseUser.getUid().toString()).collection("SavedPlace").document(namePlace[0]).set(sp);
                            }
                        });

                        return false;
                    }
                });
            }
        });
    }
//    @Override
//    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
//        this.mapboxMap = mapboxMap;
//        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//
//                enableLocationComponent(style);
//                addDestinationIconSymbolLayer(style);
//
//                addUserLocations();
//
//                // Add the symbol layer icon to map for future use
//                style.addImage(symbolIconId, BitmapFactory.decodeResource(
//                        getActivity().getResources(), R.drawable.map_default_map_marker));
//
//                // Create an empty GeoJSON source using the empty feature collection
//                setUpSource(style);
//
//                // Set up a new symbol layer for displaying the searched location's feature coordinates
//                setupLayer(style);
//                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
//                    @Override
//                    public boolean onMapClick(@NonNull LatLng point) {
//                        savePlace.setVisibility(View.VISIBLE);
//                        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//                        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                                locationComponent.getLastKnownLocation().getLatitude());
//
//                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//                        if (source != null) {
//                            source.setGeoJson(Feature.fromGeometry(destinationPoint));
//                        }
//                        getRoute(originPoint, destinationPoint);
//                        button.setEnabled(true);
//                        button.setBackgroundResource(R.color.mapboxBlue);
//
//                        final String[] namePlace = new String[1];
//                        savePlace.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                AlertDialog.Builder ad = new AlertDialog.Builder(HomeFragment.this.getActivity());
//
//                                ad.setTitle("Enter name");
//                                ad.setMessage("");
//
//                                final EditText input = new EditText(HomeFragment.this.getActivity());
//                                ad.setView(input);
//
//                                ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dlg, int which) {
//                                        String val = input.getText().toString();
//                                        //String msg = String.format("Hello %s!", val);
//                                        namePlace[0] = val;
//                                        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
//                                                .accessToken("pk.eyJ1Ijoia2hhbmhkdXkiLCJhIjoiY2thdjVndWJhMWdkcjJ4cWZzd2p2dGthdiJ9.lkspJEIi9KP1ug0Hc28VCQ")
//                                                .query(Point.fromLngLat(point.getLongitude(),point.getLatitude()))
//                                                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
//                                                .build();
//                                        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
//                                            @Override
//                                            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
//                                                List<CarmenFeature> res = response.body().features();
//
//                                                Log.d(TAG, res.get(0).placeName());
//                                                LocationHistory sp = new LocationHistory(point.getLongitude(), point.getLatitude(), res.get(0).placeName(), namePlace[0]);
//                                                db.collection("User").document(currentFirebaseUser.getUid().toString()).collection("SavedPlace").document(namePlace[0]).set(sp);
//                                                Toast.makeText(HomeFragment.this.getActivity(), "Save place succeed", Toast.LENGTH_SHORT).show();
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
//                                                Log.d(TAG, "no address found mapbox");
//                                            }
//                                        });
//
//                                    }
//                                });
//
//                                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dlg, int which) {
//                                        dlg.cancel();
//                                    }
//                                });
//
//                                ad.show();
//                                //SavePlaceClass sp = new SavePlaceClass(Double.toString(point.getLongitude()), Double.toString(point.getLatitude()));
//                                //db.collection("User").document(currentFirebaseUser.getUid().toString()).collection("SavedPlace").document(namePlace[0]).set(sp);
//                            }
//                        });
//
//                        return false;
//                    }
//                });
//            }
//        });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

// Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

// Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
// Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                    Point destinationPoint = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(), ((Point) selectedCarmenFeature.geometry()).latitude());
                    Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude());

                    GeoJsonSource source2 = mapboxMap.getStyle().getSourceAs("destination-source-id");
                    if (source2 != null) {
                        source2.setGeoJson(Feature.fromGeometry(destinationPoint));
                    }
                    getRoute(originPoint, destinationPoint);
                    button.setEnabled(true);
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> address =  geocoder.getFromLocation(destinationPoint.latitude(), destinationPoint.longitude(), 1);
                        String realaddress = address.get(0).getAddressLine(0);
                        String knownName = address.get(0).getFeatureName();
                        String countryName = address.get(0).getCountryName();
                        double tempDistance = TurfMeasurement.distance(originPoint, destinationPoint);
                        placeName.setText(realaddress);
                        placeCountry.setText(countryName);
                        distance.setText("Distance: " + String.valueOf(tempDistance) + "km");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this.getContext())) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this.getContext(), loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this.getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this.getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this.getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            this.getActivity().finish();
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this.getContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }
        getRoute(originPoint, destinationPoint);
        button.setEnabled(true);

        return false;
    }
}
