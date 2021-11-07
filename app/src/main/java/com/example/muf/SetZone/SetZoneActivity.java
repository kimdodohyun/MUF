package com.example.muf.SetZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.muf.AddPostActivity;
import com.example.muf.Home_frag;
import com.example.muf.R;
import com.example.muf.homeActivity;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class SetZoneActivity extends AppCompatActivity {
    private static final String TAG = SetZoneActivity.class.getSimpleName();
    private static final int GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE = 101;

    public static final int DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY; //정확도는 HIGH_ACRRURACY 보다 낮지만 전력이 저략됨
    public static final long DEFAULT_LOCATION_REQUEST_INTERVAL = 20000L;
    public static final long DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 10000L; //위치 정보를 불러오는 간격을 10~20초로 설정한 것

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private double latitude = 0, longitude = 0;
    private double inhalat = 37.450013, inhalng = 126.653577;
    private int flag = -1; //0 : No zone, 1 : Set zone


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_zone);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();
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
            longitude = locationResult.getLastLocation().getLongitude();
            latitude = locationResult.getLastLocation().getLatitude();
            fusedLocationProviderClient.removeLocationUpdates(locationCallback); //실시간이 아니라 한번만 실행하는 것

            Location user_location = new Location("user");
            user_location.setLatitude(latitude);
            user_location.setLongitude(longitude);

            Location inha_location = new Location("inha");
            inha_location.setLatitude(inhalat);
            inha_location.setLongitude(inhalng);

            double distance = user_location.distanceTo(inha_location);
            if(distance > 0 && distance < 500) flag = 1; //Set zone;
            else flag = 0;
            flag = 1;
            Log.d("SetZoneActivity : ", "flagvalue = " + flag + " kimgijeong");
            //엑티비티간 화면 전환
            Intent intent = new Intent();
            intent.putExtra("flag", flag);
            setResult(RESULT_OK,intent);
            finish();

        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.i(TAG, "onLocationAvailability - " + locationAvailability);
        }
    };
}