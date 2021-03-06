package com.example.muf.SetZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.muf.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SetZoneActivity extends AppCompatActivity {
    private static final String TAG = SetZoneActivity.class.getSimpleName();
    private static final int GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE = 101;

    public static final int DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY; //???????????? HIGH_ACRRURACY ?????? ????????? ????????? ?????????
    public static final long DEFAULT_LOCATION_REQUEST_INTERVAL = 20000L;
    public static final long DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 10000L; //?????? ????????? ???????????? ????????? 10~20?????? ????????? ???

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location user_location;
    private Location dest_location;
    private double distance;
    private double limitdistance;
    private static double latitude, longitude;
    private static double destlatitude, destlongitude;
    private String placename;
    private String placeenglishname;
    private int flag = -1; //0 : No zone, 1 : Set zone
    private GoogleMap mygoogleMap;
    private Marker currentMarker = null;
    private SupportMapFragment supportMapFragment;
    private Button button;
    private FirebaseFirestore firebaseFirestore;
    private LocationList locationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationList = new LocationList();
        setContentView(R.layout.activity_set_zone);
        Intent intent = getIntent();
        String ename = intent.getStringExtra("locationname");
        Log.d("locationname ??????", ename);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // HomeActivity?????? ?????? ename?????? ?????? ??????
        firebaseFirestore.collection("LocationLists").document(ename).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    locationList = document.toObject(LocationList.class);
                    destlatitude = locationList.getLatitude();
                    destlongitude = locationList.getLongitude();
                    placename = locationList.getZonename().get("kname");
                    placeenglishname = locationList.getZonename().get("ename");
                    limitdistance = locationList.getDistance();
                    checkLocationPermission(); //??? ???????????? ???????????? ????????? latitude??? longitude??? ?????????
                    Log.d("aaaaaa", "onComplete: "+ destlatitude + " " +destlongitude);
                }
            }
        });

        //?????? ????????? ????????? ????????? ????????? ?????? ??? ????????????
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mygoogleMap = googleMap;
                LatLng latLng = new LatLng(destlatitude, destlongitude);
                Log.d("third latlng", ":" + latitude + "," + longitude);
                mygoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        //?????? ?????? ?????????
        button = findViewById(R.id.set_location_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HomeActivity??? flag intent ??????
                Intent intent = new Intent();
                intent.putExtra("flag", flag);
                intent.putExtra("name", placename);
                intent.putExtra("englishname", placeenglishname);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    //?????? ???????????? ?????? ????????? ????????? ??? ?????? ????????? ?????? ????????? ??????
    private void checkLocationPermission() {
        //FINE ??? GPS ????????? ????????? CORASE??? ???????????? ????????? ??????????????? FINE??? ???????????? ???
        int accessLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessLocation == PackageManager.PERMISSION_GRANTED) {
            checkLocationSetting(); //?????? ?????? ????????? ?????? ???????????? checkLocationSetting ??????
        } else { //?????? ?????? ????????? ?????? ????????? ????????? ?????? ??????
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE) { //???????????? ?????? ?????? ????????? ???????????? ??????
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationSetting();
                    } else { //???????????? ?????? ?????? ????????? ???????????? ??????
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("?????? ????????? ??????????????????.");
                        builder.setMessage("[??????] ???????????? ?????? ????????? ???????????? ?????????.");

                        builder.setPositiveButton("???????????? ??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish(); //????????? ???????????? ?????? setting?????? ??????????????? ???????????? ????????? ???????????? ?????? ???????????? SetZone ???????????? ??????
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    break;
                }
            }
        }
    }

    //??????????????? ???????????? ????????? ???????????? ????????? ???????????? ?????????
    private void checkLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(DEFAULT_LOCATION_REQUEST_PRIORITY);
        locationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_FAST_INTERVAL);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true);
        settingsClient.checkLocationSettings(builder.build()) //???????????? ????????? ???????????? setting??? ???????????? check
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SetZoneActivity.this);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //?????? ????????? ?????? ????????? ???????????? ?????? ?????? ????????? ???????????????
                            //????????? ?????? ???????????? ???????????? checkLocationSetting ????????? ????????? ???????????? ???????????? ????????? ???????????? X
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                })
                .addOnFailureListener(SetZoneActivity.this, new OnFailureListener() {
                    //??????????????? GPS????????? ???????????? ??????
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: //????????? ?????? ????????? ????????? ?????? -> GPS ?????? ?????????
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    //?????? onActivityResult??? ???????????? GPS????????? ????????? ????????? ??????
                                    rae.startResolutionForResult(SetZoneActivity.this, GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.w(TAG, "unable to start resolution for result due to " + sie.getLocalizedMessage());
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: //????????? ?????? ????????? ???????????? ?????? ex) GPS????????? ?????? ????????????
                                String errorMessage = "location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE) { //???????????? ????????? ?????? GPS????????? ????????? ????????? ?????? checkLocationSetting ??????
            if (resultCode == RESULT_OK) {
                checkLocationSetting();
            } else { //???????????? ???????????? ?????? GPS????????? ????????? ?????? ????????? SetZone ???????????? ??????
                finish();
            }
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            latitude = locationResult.getLastLocation().getLatitude();
            longitude = locationResult.getLastLocation().getLongitude();
            fusedLocationProviderClient.removeLocationUpdates(locationCallback); //???????????? ????????? ????????? ???????????? ???

            Log.d("Fourth latlng", ":" + latitude + "," + longitude);
            //distance ????????? ?????? user??? inha location setting
            user_location = new Location("user");
            user_location.setLatitude(latitude);
            user_location.setLongitude(longitude);

            dest_location = new Location("inha");
            dest_location.setLatitude(destlatitude);
            dest_location.setLongitude(destlongitude);

            distance = user_location.distanceTo(dest_location);

            if(distance > 0 && distance < limitdistance) flag = 1; //Set zone;
            else flag = 0;
            flag = 1;

            if(currentMarker != null) currentMarker.remove();

            LatLng currentLatLng = new LatLng(latitude, longitude);
            LatLng inhaLatLng = new LatLng(destlatitude, destlongitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title("??? ??????");
            markerOptions.draggable(true);
            currentMarker = mygoogleMap.addMarker(markerOptions);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            mygoogleMap.moveCamera(cameraUpdate);
            CircleOptions SetCircle = new CircleOptions().center(inhaLatLng)
                    .radius(500)
                    .strokeWidth(5)
                    .strokeColor(Color.parseColor("#0054FF"))
                    .fillColor(Color.parseColor("#882478FF"));
            CircleOptions NoCircle = new CircleOptions().center(inhaLatLng)
                    .radius(500)
                    .strokeWidth(5)
                    .strokeColor(Color.parseColor("#FF1212"))
                    .fillColor(Color.parseColor("#88FF3636"));

            if(flag == 0) mygoogleMap.addCircle(NoCircle);
            else if (flag == 1) mygoogleMap.addCircle(SetCircle);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.i(TAG, "onLocationAvailability - " + locationAvailability);
        }
    };
}