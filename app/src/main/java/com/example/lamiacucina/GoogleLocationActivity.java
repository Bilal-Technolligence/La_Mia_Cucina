package com.example.lamiacucina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lamiacucina.model.Store;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class GoogleLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "ShowLocationNotification";
    // as Google documentation
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    LocationCallback mLocationCallback;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    //int maxId;
    Boolean running, isFirstTimeAlert = true;
    Boolean isCalled = false, refreshRequest = true;
    FirebaseDatabase firebaseDatabase;
    ValueEventListener storeList_valueEventListener = null;
    Location location;
    String MyName = "user";
    AlertDialog alertDialog;
    Spinner rangeSpinner;
    ProgressBar ShowOnLoadingLocation;
    ValueEventListener myVal = null;
    private Boolean isFirstTimeZoom = true;
    private ArrayList<Store> storesList;
    private DatabaseReference databaseReference;
    //private View loadingView;
    private SweetAlertDialog pDialog;

    @Override
    public void finish() {
        if (storeList_valueEventListener != null)
            databaseReference.removeEventListener(storeList_valueEventListener);
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_location);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("AppUsers").child("Recruiter").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            MyName = snapshot.child("Name").getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //loadingView=findViewById(R.id.loadingView);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);

        ShowOnLoadingLocation = findViewById(R.id.loadingOnLocation);
        TextView refresh = findViewById(R.id.refreshProcess);
        refresh.setOnClickListener(v -> {
            refreshRequest = true;
            refresh();
        });

        //get the spinner from the xml.
        rangeSpinner = findViewById(R.id.spinner_range);
        String[] items = new String[]{"5km", "15km", "25km", "50km"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        rangeSpinner.setAdapter(adapter);

        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshRequest = true;
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    location = locationList.get(locationList.size() - 1);
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    addYouOnMap(location);
                    if (!isCalled || refreshRequest)
                        getStores();
                }
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fm = getSupportFragmentManager();
        mapFrag = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFrag == null) {
            mapFrag = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.map, mapFrag, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFrag.getMapAsync(this);
    }

    private void getStores() {
        if (!isCalled || refreshRequest) {
            refreshRequest = false;
            databaseReference.child("AppUsers").child("Seeker").addValueEventListener(storeList_valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isCalled = true;
                        int SearchingRange = 0;

                        Double Latitude, Longitude;
                        String getprofession, isUserActive;

                        if (rangeSpinner.getSelectedItemPosition() == 0) {
                            SearchingRange = 5;
                        } else if (rangeSpinner.getSelectedItemPosition() == 1) {
                            SearchingRange = 15;
                        } else if (rangeSpinner.getSelectedItemPosition() == 2) {
                            SearchingRange = 25;
                        } else if (rangeSpinner.getSelectedItemPosition() == 3) {
                            SearchingRange = 50;
                        }

                        if (storesList != null)
                            storesList.clear();
                        if (mGoogleMap != null)
                            mGoogleMap.clear();
                        storesList = new ArrayList<>();
                        for (DataSnapshot eachUserRecord : dataSnapshot.getChildren()) {
                            getprofession = "";
                            isUserActive = "";
                            Latitude = null;
                            Longitude = null;

                            if (eachUserRecord.hasChild("Profession")) {
                                getprofession = Objects.requireNonNull(eachUserRecord.child("Profession").getValue()).toString().trim();
                            }
                            if (eachUserRecord.hasChild("isActive")) {
                                isUserActive = Objects.requireNonNull(eachUserRecord.child("isActive").getValue()).toString().trim();
                            }

                            if (eachUserRecord.hasChild("Latitude")) {
                                Latitude = (Double) Objects.requireNonNull(eachUserRecord.child("Latitude").getValue());
                            }

                            if (eachUserRecord.hasChild("Longitude")) {
                                Longitude = (Double) Objects.requireNonNull(eachUserRecord.child("Longitude").getValue());
                            }

                            SharedPreferences sharedPreferences = getSharedPreferences("Categories", Context.MODE_PRIVATE);
                            String category = sharedPreferences.getString("category", "");

                            if (Latitude == null || Longitude == null)
                                continue;

                            float[] results = new float[1];
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                    Latitude, Longitude, results);

                            if (!(results[0] / 1000 <= SearchingRange)) {
                                continue;
                            }

                            if (getprofession.equalsIgnoreCase(category.trim())
                                    && isUserActive.equals("1")) {
                                Store p = new Store();

                                /*p.setName(eachUserRecord.child("Name").getValue(String.class));
                                p.setId(eachUserRecord.child("Id").getValue(String.class));
                                p.setLatitude(Latitude);
                                p.setLongitude(Longitude);
                                p.setOrderCost(String.valueOf(results[0] / 1000));

                                if (eachUserRecord.hasChild("urlToImage")) {
                                    p.setUrl(eachUserRecord.child("urlToImage").getValue(String.class));
                                } else {
                                    p.setImage(R.drawable.profile);
                                }*/
                                storesList.add(p);
                            }
                        }

                        if (!storesList.isEmpty()) {
                            for (Store p : storesList) {
                                //Place current location marker
                                /*LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(p.getName());
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                Marker marker = mGoogleMap.addMarker(markerOptions);
                                marker.setTag(p.getId());*/
                            }
                            mGoogleMap.setOnMarkerClickListener(m -> {
                                if (Objects.requireNonNull(m.getTag()).toString().equals("CurrentUserLocation")) {
                                    new AlertDialog.Builder(GoogleLocationActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Your Location")
                                            .setMessage("This is You on Map")
                                            .setPositiveButton("OK", (dialog, which) -> dialog.cancel()).create().show();
                                    return true;
                                }

                                for (Store p : storesList) {
                                    /*if (p.getId().equals(Objects.requireNonNull(m.getTag()).toString())) {
                                        TextView tv_name1, tv_profession1, tv_phone1, tv_alter_phone1, tv_experience_num1, tv_rating_num1,
                                                tv_email_id, tv_order_cost;
                                        Button callButton, PlaceOrderButton;
                                        CircleImageView img_profile1;
                                        ImageView progressLoading;
                                        ProgressBar progressBar_Loading;
                                        RecyclerView my_recycler_view;

                                        AlertDialog.Builder builder = new AlertDialog.Builder(GoogleLocationActivity.this);
                                        ViewGroup viewGroup = findViewById(android.R.id.content);
                                        View dialogView = LayoutInflater.from(GoogleLocationActivity.this).inflate(R.layout.seeker_profile_dialog, viewGroup, false);
                                        builder.setView(dialogView);
                                        alertDialog = builder.create();

                                        tv_name1 = dialogView.findViewById(R.id.tv_name);
                                        callButton = dialogView.findViewById(R.id.call);
                                        PlaceOrderButton = dialogView.findViewById(R.id.place_order);
                                        tv_profession1 = dialogView.findViewById(R.id.tv_profession);
                                        tv_phone1 = dialogView.findViewById(R.id.tv_phone);
                                        tv_alter_phone1 = dialogView.findViewById(R.id.tv_alter_phone);
                                        tv_rating_num1 = dialogView.findViewById(R.id.tv_rating_num);
                                        tv_experience_num1 = dialogView.findViewById(R.id.tv_experience_num);
                                        img_profile1 = dialogView.findViewById(R.id.img_profile);
                                        tv_email_id = dialogView.findViewById(R.id.tv_email_id);
                                        tv_order_cost = dialogView.findViewById(R.id.tv_order_cost);
                                        progressLoading = dialogView.findViewById(R.id.progressLoading);
                                        progressBar_Loading = dialogView.findViewById(R.id.loading);
                                        my_recycler_view = dialogView.findViewById(R.id.my_recycler_view);

                                        callButton.setOnClickListener(v -> {
                                            String number = tv_phone1.getText().toString();
                                            Intent i = new Intent(Intent.ACTION_DIAL);
                                            i.setData(Uri.parse("tel:" + number));
                                            startActivity(i);
                                        });

                                        PlaceOrderButton.setOnClickListener(v -> PlaceOrder(p));
                                        float cost = Float.parseFloat(p.getOrderCost()) * 300;
                                        tv_order_cost.setText(String.valueOf((int) cost));
                                        progressBar_Loading.setVisibility(View.VISIBLE);

                                        databaseReference.child("AppUsers").child("Seeker").child(p.getId()).addValueEventListener(seeker2_valueEventListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                progressBar_Loading.setVisibility(View.VISIBLE);

                                                if (dataSnapshot1.exists()) {
                                                    tv_name1.setText(Objects.requireNonNull(dataSnapshot1.child("Name").getValue()).toString());
                                                    tv_profession1.setText(Objects.requireNonNull(dataSnapshot1.child("Profession").getValue()).toString());
                                                    tv_phone1.setText(Objects.requireNonNull(dataSnapshot1.child("Mobile").getValue()).toString());
                                                    tv_alter_phone1.setText(Objects.requireNonNull(dataSnapshot1.child("AlternateMobile").getValue()).toString());
                                                    tv_email_id.setText(Objects.requireNonNull(dataSnapshot1.child("Email").getValue()).toString());

                                                    progressLoading.setVisibility(View.VISIBLE);
                                                    if (dataSnapshot1.hasChild("urlToImage"))
                                                        Picasso.get().load(Objects.requireNonNull(dataSnapshot1.child("urlToImage").getValue()).toString()).into(img_profile1, new Callback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                progressLoading.setVisibility(View.GONE);
                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                progressLoading.setVisibility(View.GONE);
                                                                img_profile1.setImageResource(R.drawable.profile);
                                                            }
                                                        });
                                                    else {
                                                        progressLoading.setVisibility(View.GONE);
                                                        img_profile1.setImageResource(R.drawable.profile);
                                                    }

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Data does not exit", Toast.LENGTH_SHORT).show();
                                                }
                                                progressBar_Loading.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                if (progressBar_Loading.isShown())
                                                    progressBar_Loading.setVisibility(View.GONE);
                                            }
                                        });

                                        // Experience
                                        databaseReference.child("Orders").addListenerForSingleValueEvent(exp_ValueListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.getChildrenCount() > 0) {
                                                        int Exp = 0;
                                                        for (DataSnapshot experienceRef : snapshot.getChildren()) {
                                                            if (experienceRef.hasChild("OrderTo") && experienceRef.hasChild("OrderStatus")) {
                                                                if (experienceRef.child("OrderTo").getValue().toString().equals(p.getId())
                                                                        && experienceRef.child("OrderStatus").getValue().toString().equals("Completed"))
                                                                    Exp++;
                                                            }
                                                        }

                                                        if (Exp == 0) {
                                                            tv_experience_num1.setText("0");
                                                        } else {
                                                            String val = String.valueOf(Exp);
                                                            tv_experience_num1.setText(val);
                                                        }
                                                    } else
                                                        tv_experience_num1.setText("0");
                                                } else {
                                                    tv_experience_num1.setText("0");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        // AVG Rating show
                                        databaseReference.child("Orders").addListenerForSingleValueEvent(avg_Rating_ValueListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.getChildrenCount() > 0) {
                                                        int Count = 0;
                                                        double Rating = 0.0;
                                                        for (DataSnapshot experienceRef : snapshot.getChildren()) {
                                                            if (experienceRef.hasChild("OrderTo") && experienceRef.hasChild("SeekerWorkRating")) {
                                                                if (experienceRef.child("OrderTo").getValue().toString().equals(p.getId())
                                                                        && experienceRef.child("SeekerWorkRating").getValue() != null) {
                                                                    Count++;
                                                                    String rating = experienceRef.child("SeekerWorkRating").getValue().toString();
                                                                    Rating += Double.parseDouble(rating);
                                                                }
                                                            }
                                                        }

                                                        if (Count == 0) {
                                                            tv_rating_num1.setText("0.0");
                                                        } else {
                                                            Double rating = Rating / Count;
                                                            String val = String.valueOf(rating);
                                                            tv_rating_num1.setText(val);
                                                        }
                                                    } else
                                                        tv_rating_num1.setText("0.0");
                                                } else {
                                                    tv_rating_num1.setText("0.0");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        // Rating and Review show
                                        databaseReference.child("Orders").addListenerForSingleValueEvent(RatingAndReview_ValueListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.getChildrenCount() > 0) {
                                                        int Count = 0;
                                                        ReviewAndRatingSectionDataModel dm = new ReviewAndRatingSectionDataModel();
                                                        double Rating = 0.0;
                                                        for (DataSnapshot experienceRef : snapshot.getChildren()) {
                                                            if (experienceRef.hasChild("OrderTo") && experienceRef.hasChild("OrderStatus")) {
                                                                if (experienceRef.child("OrderTo").getValue(String.class).equals(p.getId())
                                                                        && experienceRef.child("OrderStatus").getValue(String.class).equals("Completed")) {
                                                                    Count++;

                                                                    dm.setHeaderTitle("Review. " + Count);
                                                                    dm.setOneItemsInSection(
                                                                            new ReviewAndRatingModel(
                                                                                    experienceRef.child("SeekerWorkRating").getValue(String.class),
                                                                                    experienceRef.child("SeekerWorkReview").getValue(String.class),
                                                                                    experienceRef.child("SeekerName").getValue(String.class),
                                                                                    experienceRef.child("RecruiterName").getValue(String.class),
                                                                                    experienceRef.child("OrderCompletedDate").getValue(String.class),
                                                                                    experienceRef.child("OrderCompletedTime").getValue(String.class)));

                                                                }
                                                            }
                                                        }

                                                        if (Count == 0) {
                                                            //tv_rating_num1.setText("0.0");
                                                        } else {
                                                            //Double rating = Rating / Count;
                                                            //String val = String.valueOf(rating);
                                                            //tv_rating_num1.setText(val);

                                                            //RecyclerView my_recycler_view = findViewById(R.id.my_recycler_view);

                                                            my_recycler_view.setHasFixedSize(true);
                                                            ReviewAndRatingRecyclerDataAdapter adapter = new ReviewAndRatingRecyclerDataAdapter(dialogView.getContext(), dm);
                                                            my_recycler_view.setLayoutManager(
                                                                    new LinearLayoutManager(dialogView.getContext(), LinearLayoutManager.HORIZONTAL, false));

                                                            my_recycler_view.setAdapter(adapter);
                                                        }
                                                    } else {
                                                        //tv_rating_num1.setText("0.0");
                                                    }
                                                } else {
                                                    //tv_rating_num1.setText("0.0");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        alertDialog.show();

                                    }*/
                                }

                                return true;
                            });

                            if (pDialog.isShowing())
                                pDialog.dismiss();

                        }
                        else {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            if (isFirstTimeAlert) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GoogleLocationActivity.this)
                                        .setCancelable(true)
                                        .setTitle("Message")
                                        .setMessage("No Available workers in your Area")
                                        .setPositiveButton("OK", (dialog, which) -> dialog.cancel());

                                AlertDialog alertDialog = builder.create();

                                if (!((GoogleLocationActivity.this)).isFinishing()) {
                                    alertDialog.show();
                                }
                                isFirstTimeAlert = false;
                            }
                        }
                    }
                    else
                    {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }
            });
        }
    }

    private void addYouOnMap(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrLocationMarker.setTag("CurrentUserLocation");

        //move map camera
        if (isFirstTimeZoom) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            isFirstTimeZoom = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        refresh();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(GoogleLocationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void refresh() {
        pDialog.show();

        isFirstTimeAlert = true;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }
}