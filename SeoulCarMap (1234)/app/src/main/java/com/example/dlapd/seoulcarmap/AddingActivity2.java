package com.example.dlapd.seoulcarmap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;

public class AddingActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private double lat;
    private double lon;
    GoogleMap map2;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 200;
    private int REQUEST_CODE_PERMISSIONS = 2000;
    Marker marker;
    String address;
    TextView show_now;
    private FusedLocationProviderClient mFusedLocationClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.lab2_map);
        mapFragment.getMapAsync(AddingActivity2.this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);
        EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);
        ImageButton ivPlace = (ImageButton) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button);
        ivPlace.setImageResource(R.drawable.glass);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etPlace.setHint("추가할 주차장 위치를 검색         ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            etPlace.setTextColor(getColor(R.color.color_black));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            etPlace.setTextAppearance(R.style.customfontstyle);
        }
        TranslateAnimation ani = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        ani.setDuration(6000);
        ani.setRepeatMode(Animation.RESTART);
        ani.setRepeatCount(Animation.INFINITE);
        etPlace.startAnimation(ani);

        etPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view);
            }
        });

        ivPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view);
            }
        });


    }


    public void findPlace(View view) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).setCountry("KR").build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map2 = googleMap; //googleMap 객체를 얻음.
        show_now =(TextView)findViewById(R.id.show_now);


        try {
            int locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationMode != LOCATION_MODE_HIGH_ACCURACY || permissionCheck != PackageManager.PERMISSION_GRANTED) {
                final LatLng latLng = new LatLng(37.566643, 126.978279);// 지도내의 특정위치를 LatLng 객체로 표현. LatLng 객체에 위도, 경도를 매개변수로 줌.
                CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(18.0f).build();
                map2.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            } else {
                OnLastLocationButtonClicked();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        ImageButton btnMyLoca2 = (ImageButton) findViewById(R.id.btn_mylocation2);
        btnMyLoca2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marker!=null)
                    marker.remove();
                OnLastLocationButtonClicked();
            }
        });
        LatLngBounds korea = new LatLngBounds(new LatLng(33, 124), new LatLng(43, 132));
        map2.setMinZoomPreference(6.5f);
        map2.setMaxZoomPreference(19f);
        map2.setLatLngBoundsForCameraTarget(korea);
        map2.getUiSettings().setRotateGesturesEnabled(false);
        map2.getUiSettings().setMapToolbarEnabled(false);
        map2.getUiSettings().setZoomControlsEnabled(true);
        // 내 위치 찍기 관련 영역
        map2.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null)
                    marker.remove();
                marker = map2.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.plogo)));
                Geocoder geocoder = new Geocoder(AddingActivity2.this);
                // 현재위치 주소얻기
                try {
                    lat = latLng.latitude;
                    lon = latLng.longitude;
                    List<Address> listAddress = geocoder.getFromLocation(lat, lon, 1);
                    if (listAddress != null && listAddress.size() > 0) {
                        address = listAddress.get(0).getAddressLine(0);
                    } else address = "";
//

                } catch (IOException e) {
                    e.printStackTrace();
                    // 실패할경우 이걸로 대체.
                }

                show_now = (TextView) findViewById(R.id.show_now);
                show_now.setText(address);


            }

        });

        ImageButton imageButton = (ImageButton) findViewById(R.id.check_btn);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(show_now.getText().toString().equals("공유할 주차창 위치를 지도위에 표시하세요")||show_now.getText().toString().equals("")) {
                        Toast.makeText(AddingActivity2.this, "등록할 주차장의 위치를 지도상에 표시해주세요", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(AddingActivity2.this, AddingActivity.class);
                        intent.putExtra("address", address);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lon", lon);
                        startActivity(intent);
                        finish();
                    }

                }
            });
    }

    Marker marker2;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(this, data);

                map2.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),18f));

            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    /*현재 위치 받아오기*/
    public void OnLastLocationButtonClicked() { //현재 위치 버튼 클릭 이벤트 (권한 동의 확인)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    int locationMode = Settings.Secure.getInt(AddingActivity2.this.getContentResolver(), Settings.Secure.LOCATION_MODE);
                    if (locationMode != LOCATION_MODE_HIGH_ACCURACY) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Toast.makeText(AddingActivity2.this, "위치 인식 방식을 '높은 정확도'로 설정해주세요", Toast.LENGTH_LONG).show();
                    } else {
                        if (location != null) {
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            map2.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            map2.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
                        }
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, //권한 거부 시
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 없음", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

