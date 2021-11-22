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

    public static final int DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY; //정확도는 HIGH_ACRRURACY 보다 낮지만 전력이 저략됨
    public static final long DEFAULT_LOCATION_REQUEST_INTERVAL = 20000L;
    public static final long DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 10000L; //위치 정보를 불러오는 간격을 10~20초로 설정한 것

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
        Log.d("locationname 체크", ename);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // HomeActivity에게 받은 ename으로 문서 접근
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
                    checkLocationPermission(); //이 메소드가 호출되면 내위치 latitude와 longitude가 설정됨
                    Log.d("aaaaaa", "onComplete: "+ destlatitude + " " +destlongitude);
                }
            }
        });

        //초기 구글맵 선택한 장소로 카메라 이동 후 마커생성
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

        //확인 버튼 누르면
        button = findViewById(R.id.set_location_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HomeActivity로 flag intent 전달
                Intent intent = new Intent();
                intent.putExtra("flag", flag);
                intent.putExtra("name", placename);
                intent.putExtra("englishname", placeenglishname);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    //앱이 사용자의 위치 정보에 접근할 수 있는 권한이 부여 됐는지 체크
    private void checkLocationPermission() {
        //FINE 즉 GPS 권한이 있으면 CORASE도 자동으로 권한이 부여되므로 FINE만 확인하면 됨
        int accessLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessLocation == PackageManager.PERMISSION_GRANTED) {
            checkLocationSetting(); //위치 정보 권한이 허용 되었으면 checkLocationSetting 시작
        } else { //위치 정보 권한이 허용 돼있지 않으면 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE) { //사용자가 위치 권한 허용을 선택했을 경우
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationSetting();
                    } else { //사용자가 위치 권한 거부를 선택했을 경우
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("위치 권한이 꺼져있습니다.");
                        builder.setMessage("[권한] 설정에서 위치 권한을 허용해야 합니다.");

                        builder.setPositiveButton("설정으로 가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish(); //권한을 부여하기 위해 setting으로 이동했지만 거기서도 권한을 부여하지 않고 종료하면 SetZone 액티비티 종료
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

    //위치정보의 정확도나 정보를 요청하는 간격을 설정하는 메소드
    private void checkLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(DEFAULT_LOCATION_REQUEST_PRIORITY);
        locationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_FAST_INTERVAL);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true);
        settingsClient.checkLocationSettings(builder.build()) //개발자가 설정한 기준대로 setting이 가능한지 check
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SetZoneActivity.this);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //위치 정보에 대한 권한이 부여되어 있지 않은 경우에 진입되는데
                            //권한이 부여 되어있지 않은경우 checkLocationSetting 메소드 자체에 진입하지 않으므로 별도의 예외처리 X
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                })
                .addOnFailureListener(SetZoneActivity.this, new OnFailureListener() {
                    //통상적으로 GPS기능이 꺼져있는 경우
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: //설정을 통해 해결이 가능한 경우 -> GPS 기능 활성화
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    //밑에 onActivityResult로 이동하여 GPS기능을 활성화 하도록 안내
                                    rae.startResolutionForResult(SetZoneActivity.this, GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.w(TAG, "unable to start resolution for result due to " + sie.getLocalizedMessage());
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: //설정을 통해 해결이 불가능한 경우 ex) GPS기능이 없는 디바이스
                                String errorMessage = "location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE) { //사용자가 확인을 눌러 GPS기능을 활성화 했으면 다시 checkLocationSetting 시작
            if (resultCode == RESULT_OK) {
                checkLocationSetting();
            } else { //사용자가 아니오를 눌러 GPS기능을 활성화 하지 않으면 SetZone 액티비티 종료
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
            fusedLocationProviderClient.removeLocationUpdates(locationCallback); //실시간이 아니라 한번만 실행하는 것

            Log.d("Fourth latlng", ":" + latitude + "," + longitude);
            //distance 계산을 위해 user와 inha location setting
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
            markerOptions.title("내 위치");
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