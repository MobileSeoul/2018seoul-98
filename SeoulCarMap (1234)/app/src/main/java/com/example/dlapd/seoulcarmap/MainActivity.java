package com.example.dlapd.seoulcarmap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dlapd.seoulcarmap.chat.ChatFolderActivity;
import com.example.dlapd.seoulcarmap.chat.ChatToManager;
import com.example.dlapd.seoulcarmap.chat.MessageActivity;
import com.example.dlapd.seoulcarmap.model.ChatModel;
import com.example.dlapd.seoulcarmap.model.UserModel;
import com.example.dlapd.seoulcarmap.service.MyFirebaseInstanceIDService;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.skt.Tmap.TMapTapi;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private TextView nameTextView, emailTextView;
    private ImageView userImageview;
    private ImageButton qmark;
    private ImageButton closebtn;

    private FirebaseAuth auth;
    final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private GoogleSignInClient mGoogleSignInClient;
    String name;
    private DatabaseReference mDatabaseRef;
    private ArrayList<Upload> contacts1;
    private ArrayList<MapList> contacts2;
    private ArrayList<String> latitude,
            longitude,
            address,
            tel,
            parkingname,
            capacity,
            pay,
            weekdayBegin,
            weekdayEnd,
            weekendBegin,
            weekendEnd,
            holidayBegin,
            holidayEnd,
            saturdayPay,
            holidayPay,
            fulltimeMonth,
            rate,
            timeRate,
            addRate,
            addtimeRate,
            dayMaximum;

    private String TagName,
            TagAddress,
            TagCapacity,
            TagPay,
            TagWeekDayBegin,
            TagWeekDayEnd,
            TagWeekEndBegin,
            TagWeekEndEnd,
            TagHolidayBegin,
            TagHolidayEnd,
            TagSaturdayPay,
            TagHolidayPay,
            TagFulltimeMonth,
            TagRate,
            TagTimeRate,
            TagAddRate,
            TagAddtimeRate,
            TagDayMaximum,
            TagTel,
            TagImage,
            TagUId,
            TagLat,
            TagLng,
            TagCond;
    public static String TagToken;

    private GoogleMap map;
    private Marker markerAPI,
            markerPlace,
            markerMy;
    ;
    private ConcurrentHashMap<String, Marker> visibleMarkers = new ConcurrentHashMap<String, Marker>();
    private ConcurrentHashMap<String, Marker> visibleMarkersUpload = new ConcurrentHashMap<String, Marker>();

    LatLng seoulcityhall;

    private FusedLocationProviderClient mFusedLocationClient;
    public static int REQUEST_CODE_PERMISSIONS = 1000;
    public static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
//        myFirebaseInstanceIDService.onTokenRefresh();

        latitude = new ArrayList<String>();
        longitude = new ArrayList<String>();
        address = new ArrayList<String>();
        parkingname = new ArrayList<String>();
        tel = new ArrayList<String>();
        capacity = new ArrayList<String>();
        pay = new ArrayList<String>();
        weekdayBegin = new ArrayList<String>();
        weekdayEnd = new ArrayList<String>();
        weekendBegin = new ArrayList<String>();
        weekendEnd = new ArrayList<String>();
        holidayBegin = new ArrayList<String>();
        holidayEnd = new ArrayList<String>();
        saturdayPay = new ArrayList<String>();
        holidayPay = new ArrayList<String>();
        fulltimeMonth = new ArrayList<String>();
        rate = new ArrayList<String>();
        timeRate = new ArrayList<String>();
        addRate = new ArrayList<String>();
        addtimeRate = new ArrayList<String>();
        dayMaximum = new ArrayList<String>();

        contacts1 = new ArrayList<Upload>();
        contacts2 = new ArrayList<MapList>();

        //NaviDrawer 영역 시작
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_my_hand)));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //NaviDrawer 영역 끝

        //이부분에서 navigationView의 Header부분 불러옴
        View view = navigationView.getHeaderView(0);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        auth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //네비윗부분 셋팅 수정영역 시작
        nameTextView = (TextView) view.findViewById(R.id.header_name_Textview);
        emailTextView = (TextView) view.findViewById(R.id.header_email_Textview);

        nameTextView.setText(auth.getCurrentUser().getDisplayName());
        emailTextView.setText(auth.getCurrentUser().getEmail());
        //네비윗부분 셋팅 수정영역 끝


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.lab1_map);
        mapFragment.getMapAsync(this);


        // 지도 객체를 얻으려면 OnMapReadyCallBack 인터페이스를 구현한 클래스를 getMapAsync() 함수를 이용하여 등록한다. 이렇게 해놓으면 지도 객체를 사용할 수 있을떄
        // onMapReady() 함수가 자동으로 호출되면서 매개변수로 GoogleMap 객체가 전달된다.


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);

        ImageButton ivPlace = (ImageButton) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button);
        ivPlace.setImageResource(R.drawable.glass);

        etPlace.setHint("검색할 지역을 입력하세요         ");

//        etPlace.setTextColor(getResources().getColor(R.color.color_font));
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

        ImageButton qmark=(ImageButton)findViewById(R.id.qmark);

        qmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.description_dialog);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                //This makes the dialog take up the full width
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.show();

                ImageButton closebtn = dialog.findViewById(R.id.close_btn);
                closebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        //etPlace.setHintTextColor(getResources().getColor(R.color.colorPrimary)); 작동안함



        /*autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.clear();
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                MarkerOptions marker= new MarkerOptions();
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker3));
                marker.title(place.getName().toString());// Title 마커에 표시하고 싶은 타이틀을 문자열로 설정한다.
                marker.snippet(place.getAddress().toString());//Snippet 마커에 표시되는 타이틀 바로 밑에 추가될 텍스트를 문자열로 설정한다.
                marker.alpha(0.8f); // Alpha 마커아이콘의 투명도를 설정한다. 단 타이틀이나 텍스트는 적용되지 않는다.
                map.addMarker(marker.position(place.getLatLng()));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });*/

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
            AutocompleteFilter typeFilter = new AutocompleteFilter
                    .Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .setCountry("KR").build();
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

    // A place has been received; use requestCode to track the request.
    Circle circle;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (markerPlace != null) {
                    markerPlace.remove();
                }
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                markerPlace = markerMy = map.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.myplace)));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // 지도제어 중 가장 기본적인 제어인 지도의 중심 위치 이동을 위한 메소드

        map = googleMap; //googleMap 객체를 얻음.
        seoulcityhall = new LatLng(37.566643, 126.978279);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getXmlData();
//                // 아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addUploadItemsToMap(contacts1);
                        addItemsToMap(contacts2);
                    }
                });
            }
        }).start();
        boolean gpsEnable = false;
        LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnable = true;
        }
        try {
            int locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationMode != LOCATION_MODE_HIGH_ACCURACY || permissionCheck != PackageManager.PERMISSION_GRANTED) {
                final LatLng latLng = new LatLng(37.566643, 126.978279);// 지도내의 특정위치를 LatLng 객체로 표현. LatLng 객체에 위도, 경도를 매개변수로 줌.
                CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(18.0f).build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            } else {
                OnLastLocationButtonClicked();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        ImageButton btnMyLoca = (ImageButton) findViewById(R.id.btn_mylocation);
        btnMyLoca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (markerMy != null)
                    markerMy.remove();
                OnLastLocationButtonClicked();
            }
        });
        LatLngBounds korea = new LatLngBounds(new LatLng(33, 124), new LatLng(43, 132));
        Double maplat = map.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude;
        Double maplng = map.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude;

        if (maplat < 33 || maplat > 43 || maplng < 124 || maplng > 132)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulcityhall, 18));

        map.setMinZoomPreference(6.5f);
        map.setMaxZoomPreference(19f);
        map.setLatLngBoundsForCameraTarget(korea);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Upload upload = s.getValue(Upload.class);
                    if (upload.getLat() != null) {
                        contacts1.add(upload);
                        addUploadItemsToMap(contacts1);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*google map이벤트 처리 영역*/
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() { //사용자의 특정 위치클릭시 이벤트 주는 메소드
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() { // 지도의 특정 위치를 롱클릭시 이벤트 주는 메소드
            public void onMapLongClick(LatLng latLng) {

            }
        });
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() { //카메라 이동
            @Override
            public void onCameraMove() {

            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {// 지도 변경 이벤트가 끝난 순간 한번 호출되는 이벤트
            @Override
            public void onCameraIdle() {
                if (map.getCameraPosition().zoom < 13f) {
                    Toast.makeText(MainActivity.this, "지도를 일정 범위 이상 축소하시면 정보가 나타나지 않습니다", Toast.LENGTH_SHORT).show();
                }
                if (map.getCameraPosition().zoom < 13f) {
                    map.clear();
                } else {
                    addUploadItemsToMap(contacts1);
                    addItemsToMap(contacts2);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // 마커 클릭시 이벤트
            @Override
            public boolean onMarkerClick(final Marker marker) { //주차장 정보 다이얼로그
                if (marker.equals(markerPlace))
                    Log.e("place", "click");
                else if (marker.equals(markerMy))
                    Log.e("my", "click");
                else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));

                    //주차장 정보 다이얼로그
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.bottom_sheet);
                    dialog.show();

                    /*다이얼로그 인스턴스 선언*/
                    TextView dialogParkingName = (TextView) dialog.findViewById(R.id.dialog_ParkingName);
                    TextView dialogAddress = (TextView) dialog.findViewById(R.id.dialog_Address);
                    TextView dialogRate = (TextView) dialog.findViewById(R.id.dialog_Rate);
                    TextView dialogTime = (TextView) dialog.findViewById(R.id.dialog_Time);
                    ImageButton navi_btn = dialog.findViewById(R.id.bottom_sheet_navi_btn);
                    ImageButton call_btn = dialog.findViewById(R.id.bottom_sheet_call_btn);
                    ImageButton chat_btn = dialog.findViewById(R.id.bottom_sheet_chat_btn);

                    /*태그 문자열 추출*/
                    final int idx1 = marker.getTag().toString().indexOf("이름");
                    final int idx2 = marker.getTag().toString().indexOf("주소");
                    final int idx3 = marker.getTag().toString().indexOf("면수");
                    final int idx4 = marker.getTag().toString().indexOf("평일시작");
                    final int idx5 = marker.getTag().toString().indexOf("평일종료");
                    final int idx6 = marker.getTag().toString().indexOf("주말시작");
                    final int idx7 = marker.getTag().toString().indexOf("주말종료");
                    final int idx8 = marker.getTag().toString().indexOf("공휴일시작");
                    final int idx9 = marker.getTag().toString().indexOf("공휴일종료");
                    final int idx10 = marker.getTag().toString().indexOf("월정기권");
                    final int idx11 = marker.getTag().toString().indexOf("기본요금");
                    final int idx12 = marker.getTag().toString().indexOf("기본시간");
                    final int idx13 = marker.getTag().toString().indexOf("추가요금");
                    final int idx14 = marker.getTag().toString().indexOf("추가시간");
                    final int idx15 = marker.getTag().toString().indexOf("일최대");
                    final int idx16 = marker.getTag().toString().indexOf("번호");
                    final int idx17 = marker.getTag().toString().indexOf("사진");
                    final int idx18 = marker.getTag().toString().indexOf("UId");
                    final int idx19 = marker.getTag().toString().indexOf("위도");
                    final int idx20 = marker.getTag().toString().indexOf("경도");
                    final int idx21 = marker.getTag().toString().indexOf("토큰");
                    final int idx22 = marker.getTag().toString().indexOf("운영조건");

                    TagName = marker.getTag().toString().substring(idx1 + 2, idx2);
                    TagAddress = marker.getTag().toString().substring(idx2 + 2, idx3);
                    TagCapacity = marker.getTag().toString().substring(idx3 + 2, idx4);
                    TagWeekDayBegin = marker.getTag().toString().substring(idx4 + 4, idx5);
                    TagWeekDayEnd = marker.getTag().toString().substring(idx5 + 4, idx6);
                    TagWeekEndBegin = marker.getTag().toString().substring(idx6 + 4, idx7);
                    TagWeekEndEnd = marker.getTag().toString().substring(idx7 + 4, idx8);
                    TagHolidayBegin = marker.getTag().toString().substring(idx8 + 5, idx9);
                    TagHolidayEnd = marker.getTag().toString().substring(idx9 + 5, idx10);
                    TagFulltimeMonth = marker.getTag().toString().substring(idx10 + 4, idx11);
                    TagRate = marker.getTag().toString().substring(idx11 + 4, idx12);
                    TagTimeRate = marker.getTag().toString().substring(idx12 + 4, idx13);
                    TagAddRate = marker.getTag().toString().substring(idx13 + 4, idx14);
                    TagAddtimeRate = marker.getTag().toString().substring(idx14 + 4, idx15);
                    TagDayMaximum = marker.getTag().toString().substring(idx15 + 3, idx16);
                    TagTel = marker.getTag().toString().substring(idx16 + 2, idx17);
                    TagImage = marker.getTag().toString().substring(idx17 + 2, idx18);
                    TagUId = marker.getTag().toString().substring(idx18 + 3, idx19);
                    TagLat = marker.getTag().toString().substring(idx19 + 2, idx20);
                    TagLng = marker.getTag().toString().substring(idx20 + 2, idx21);
                    TagToken = marker.getTag().toString().substring(idx21 + 2, idx22);
                    TagCond = marker.getTag().toString().substring(idx22 + 4);


                    /*다이얼로그 정보 갱신*/
                    dialogParkingName.setText(TagName);
                    dialogAddress.setText(TagAddress);
                    if (!TagTimeRate.equals("") || !TagRate.equals(""))
                        dialogRate.setText(TagTimeRate + "분당 " + TagRate + "원");

                    if (!TagRate.equals("") && TagTimeRate.equals(""))
                        dialogRate.setText("시간당 " + TagRate + "원");

                    if (TagRate.equals("0") && TagTimeRate.equals("")|| TagRate.equals("무료"))
                        dialogRate.setText("무료");

                    if (!TagWeekDayBegin.equals("") || !TagWeekDayEnd.equals("")) {
                        dialogTime.setText(TagWeekDayBegin + TagWeekDayEnd);
                    }

                    navi_btn.setOnClickListener(new View.OnClickListener() { //내비 버튼 클릭 이벤트
                        @Override
                        public void onClick(View view) {
                            final String[] items = new String[]{" T map ", " 카카오 내비 "};
                            final Integer[] icons = new Integer[]{R.drawable.tmap, R.drawable.kakao_navi};
                            ListAdapter adapter = new ArrayAdapterWithIcon(MainActivity.this, items, icons);
                            new AlertDialog.Builder(MainActivity.this).setTitle("길안내를 시작할 앱을 선택해주세요")
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {

                                            switch (item) {
                                                case 0: //티맵
                                                    TMapTapi tmaptapi = new TMapTapi(MainActivity.this);
                                                    boolean isTmapApp = tmaptapi.isTmapApplicationInstalled();

                                                        tmaptapi.setSKTMapAuthentication("d163e2cf-8e08-4964-b54a-12dc3b616e63");
                                                        tmaptapi.invokeRoute(marker.getTag().toString().substring(idx1 + 2, idx2),
                                                                (float) marker.getPosition().longitude, (float) marker.getPosition().latitude);

                                                    break;

                                                case 1: //카카오내비
                                                    // Location.Builder를 사용하여 Location 객체를 만든다.
                                                    boolean isKakaoNavi = KakaoNaviService.isKakaoNaviInstalled(MainActivity.this);
                                                    if (isKakaoNavi) {
                                                        com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(marker.getTag().toString().substring(idx1 + 2, idx2),
                                                                marker.getPosition().longitude, marker.getPosition().latitude).build();
                                                        NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST)
                                                                .setRpOption(RpOption.SHORTEST).build();
                                                        KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                                                        KakaoNaviService.navigate(MainActivity.this, builder.build());
                                                    } else
                                                        Toast.makeText(MainActivity.this, "'카카오내비'가 설치되어 있지 않습니다", Toast.LENGTH_SHORT).show();
                                                    break;
                                            }
                                        }
                                    }).show();


                        }
                    });

                    call_btn.setOnClickListener(new View.OnClickListener() { //전화 버튼 이벤트
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + TagTel));
                            startActivity(intent);
                        }
                    });

                    chat_btn.setOnClickListener(new View.OnClickListener() { //채팅 버튼 이벤트
                        @Override
                        public void onClick(View view) {
                            if (TagUId.equals(myUid)) { //자기가 올린 게시물일 경우
                                Toast.makeText(getApplicationContext(), "자신의 게시물입니다.", Toast.LENGTH_SHORT).show();
                            } else if (TagUId.equals("")) {
                                Toast.makeText(MainActivity.this, "'공영주차장'은 채팅기능을 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intentchat = new Intent(view.getContext(), MessageActivity.class)
                                        .putExtra("destinationUid", TagUId)
                                        .putExtra("destinationToken", TagToken);
                                startActivity(intentchat);
                            }
                        }
                    });

                    LinearLayout bottomsheetDialog = dialog.findViewById(R.id.bottom_sheet_layout);
                    bottomsheetDialog.setOnClickListener(new View.OnClickListener() { //다이얼로그 레이아웃 클릭 이벤트
                        @Override
                        public void onClick(View view) {
                            if (!TagUId.equals("")) {
                                startActivity(new Intent(MainActivity.this, UploadParkingInfo.class)
                                        .putExtra("TagName", TagName)
                                        .putExtra("TagAddress", TagAddress)
                                        .putExtra("TagCapacity", TagCapacity)
                                        .putExtra("TagWeekDayBegin", TagWeekDayBegin)
                                        .putExtra("TagWeekDayEnd", TagWeekDayEnd)
                                        .putExtra("TagRate", TagRate)
                                        .putExtra("TagTimeRate", TagTimeRate)
                                        .putExtra("TagAddRate", TagAddRate)
                                        .putExtra("TagTel", TagTel)
                                        .putExtra("TagImage", TagImage)
                                        .putExtra("TagUId", TagUId)
                                        .putExtra("TagLat", TagLat)
                                        .putExtra("TagLng", TagLng)
                                        .putExtra("TagCond", TagCond));
                            } else {
                                startActivity(new Intent(MainActivity.this, ParkingInfo.class)
                                        .putExtra("TagName", TagName)
                                        .putExtra("TagAddress", TagAddress)
                                        .putExtra("TagCapacity", TagCapacity)
                                        .putExtra("TagWeekDayBegin", TagWeekDayBegin)
                                        .putExtra("TagWeekDayEnd", TagWeekDayEnd)
                                        .putExtra("TagWeekEndBegin", TagWeekEndBegin)
                                        .putExtra("TagWeekEndEnd", TagWeekEndEnd)
                                        .putExtra("TagHolidayBegin", TagHolidayBegin)
                                        .putExtra("TagHolidayEnd", TagHolidayEnd)
                                        .putExtra("TagSaturdayPay", TagSaturdayPay)
                                        .putExtra("TagHolidayPay", TagHolidayPay)
                                        .putExtra("TagFulltimeMonth", TagFulltimeMonth)
                                        .putExtra("TagRate", TagRate)
                                        .putExtra("TagTimeRate", TagTimeRate)
                                        .putExtra("TagAddRate", TagAddRate)
                                        .putExtra("TagAddtimeRate", TagAddtimeRate)
                                        .putExtra("TagDayMaximum", TagDayMaximum)
                                        .putExtra("TagTel", TagTel)
                                        .putExtra("TagImage", TagImage)
                                        .putExtra("TagUId", TagUId)
                                        .putExtra("TagLat", TagLat)
                                        .putExtra("TagLng", TagLng)
                                );
                            }
                        }
                    });
                }
                return true;

            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() { //마커의 정보창 클릭시 이벤트
            @Override
            public void onInfoWindowClick(Marker marker) {
                //..
            }
        });
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
            public void onSuccess(Location location) {// GPS 연동 성공
                try {
                    int locationMode = Settings.Secure.getInt(MainActivity.this.getContentResolver(), Settings.Secure.LOCATION_MODE);
                    if (locationMode != LOCATION_MODE_HIGH_ACCURACY) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Toast.makeText(MainActivity.this, "위치 인식 방식을 '높은 정확도'로 설정해주세요", Toast.LENGTH_LONG).show();
                    } else {
                        if (location != null) {
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            map.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
                            if (markerMy != null) markerMy.remove();
                            markerMy = map.addMarker(new MarkerOptions()
                                    .position(myLocation)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.myplace)));
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

    /*class MyReverseGeocodingThread extends Thread{
        String address;
        public MyReverseGeocodingThread(String address){
            this.address=address;
        }

        @Override
        public void run(){
            Geocoder geocoder = new Geocoder(MainActivity.this);
            try{
                List<Address> results = geocoder.getFromLocationName(address, 1);
                Address resultAddress = results.get(0);
                LatLng latLng = new LatLng(resultAddress.getLatitude(), resultAddress.getLongitude());

                Message msg = new Message();
                msg.what = 200;
                msg.obj = latLng;
                handler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class MyGeocodingThread extends Thread {

        LatLng latLng;

        public MyGeocodingThread(LatLng latLng){
            this.latLng = latLng;
        }

        @Override
        public void run(){
            Geocoder geocoder = new Geocoder(MainActivity.this);

            List<Address> addresses = null;
            String addressText = "";
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                Thread.sleep(500);

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    addressText = address.getAdminArea()+""+(address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : address.getLocality())+"";

                    String txt=address.getSubLocality();

                    if(txt != null)
                        addressText +=address.getThoroughfare() + "" + address.getSubThoroughfare();

                    Message msg = new Message();
                    msg.what = 100;
                    msg.obj = addressText;
                    handler.sendMessage(msg);
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 100: {
                    Toast toast = Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_LONG);
                    toast.show();
                    break;
                }
                case 200:{

                    MarkerOptions markerOption2 = new MarkerOptions();
                    markerOption2.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2));
                    markerOption2.position((LatLng)msg.obj);
                    markerOption2.title("서울시립미술관");
                    map.addMarker(markerOption2);
                }
            }
        }
    };*/


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Plus) {
            startActivity(new Intent(MainActivity.this, AddingActivity2.class));
        } else if (id == R.id.nav_UploadList) {
            startActivity(new Intent(MainActivity.this, ImagesActivity.class));
        } else if (id == R.id.nav_MyList) {
            startActivity(new Intent(MainActivity.this, ImagesActivity2.class));
        } else if (id == R.id.nav_ChatList) {
            startActivity(new Intent(MainActivity.this, ChatFolderActivity.class));
        }  else if (id == R.id.nav_admin_chat) {
            startActivity(new Intent(MainActivity.this, ChatToManager.class));
        } else if (id == R.id.nav_Setting) {
            startActivity(new Intent(MainActivity.this, ManageActivity.class));
        } else if (id == R.id.nav_logout) {


            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("로그아웃")
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
// Firebase sign out
                            auth.signOut();
                            //Facebook sign out
                            LoginManager.getInstance().logOut();
            /*Google sign out
            mGoogleSignInClient.signOut();*/

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "로그아웃",
                                    Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    // 아무일도 안 일어남
                }
            })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private long time = 0;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

    /*XML파싱*/
    public void getXmlData() {

        AssetManager am = getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open("PARKING_INFO.xml");
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser(); // XMLPullParser 사용

            for (int i = 0; i < 882; i++) {
                parser.setInput(new InputStreamReader(is, "UTF-8"));   // 파싱하기위해서 스트림을 열어야한다.
                int parserEvent = parser.getEventType();  // 파싱할 데이터의 타입을 알려준다.
                String tag;

                boolean LAT = false,
                        LNG = false,
                        ADDR = false,
                        TEL = false,
                        PARKING_NAME = false,
                        CAPACITY = false,
                        WEEKDAY_BEGIN_TIME = false,
                        WEEKDAY_END_TIME = false,
                        WEEKEND_BEGIN_TIME = false,
                        WEEKEND_END_TIME = false,
                        HOLIDAY_BEGIN_TIME = false,
                        HOLIDAY_END_TIME = false,
                        FULLTIME_MONTHLY = false,
                        RATES = false,
                        TIME_RATE = false,
                        ADD_RATES = false,
                        ADD_TIME_RATE = false,
                        DAY_MAXIMUM = false;

                while (parserEvent != XmlPullParser.END_DOCUMENT) { // xml 파일의 문서 끝인가?
                    switch (parserEvent) {

                        case XmlPullParser.TEXT:

                            if (LAT) {
                                String max = parser.getText();
                                latitude.add(max);
                            } else if (LNG) {
                                String max = parser.getText();
                                longitude.add(max);
                            } else if (ADDR) {
                                String max = parser.getText();
                                address.add(max);
                            } else if (TEL) {
                                String max = parser.getText();
                                tel.add(max);
                            } else if (PARKING_NAME) {
                                String max = parser.getText();
                                parkingname.add(max);
                            } else if (CAPACITY) {
                                String max = parser.getText();
                                capacity.add(max);
                            } else if (WEEKDAY_BEGIN_TIME) {
                                String max = parser.getText();
                                weekdayBegin.add(max);
                            } else if (WEEKDAY_END_TIME) {
                                String max = parser.getText();
                                weekdayEnd.add(max);
                            } else if (WEEKEND_BEGIN_TIME) {
                                String max = parser.getText();
                                weekendBegin.add(max);
                            } else if (WEEKEND_END_TIME) {
                                String max = parser.getText();
                                weekendEnd.add(max);
                            } else if (HOLIDAY_BEGIN_TIME) {
                                String max = parser.getText();
                                holidayBegin.add(max);
                            } else if (HOLIDAY_END_TIME) {
                                String max = parser.getText();
                                holidayEnd.add(max);
                            } else if (FULLTIME_MONTHLY) {
                                String max = parser.getText();
                                fulltimeMonth.add(max);
                            } else if (RATES) {
                                String max = parser.getText();
                                rate.add(max);
                            } else if (TIME_RATE) {
                                String max = parser.getText();
                                timeRate.add(max);
                            } else if (ADD_RATES) {
                                String max = parser.getText();
                                addRate.add(max);
                            } else if (ADD_TIME_RATE) {
                                String max = parser.getText();
                                addtimeRate.add(max);
                            } else if (DAY_MAXIMUM) {
                                String max = parser.getText();
                                dayMaximum.add(max);
                            }

                            break;

                        case XmlPullParser.END_TAG: // 나중에
                            tag = parser.getName();

                            if (tag.equals("LAT")) {
                                LAT = false;
                            } else if (tag.equals("LNG")) {
                                LNG = false;
                            } else if (tag.equals("ADDR")) {
                                ADDR = false;
                            } else if (tag.equals("PARKING_NAME")) {
                                PARKING_NAME = false;
                            } else if (tag.equals("TEL")) {
                                TEL = false;
                            } else if (tag.equals("CAPACITY")) {
                                CAPACITY = false;
                            } else if (tag.equals("WEEKDAY_BEGIN_TIME")) {
                                WEEKDAY_BEGIN_TIME = false;
                            } else if (tag.equals("WEEKDAY_END_TIME")) {
                                WEEKDAY_END_TIME = false;
                            } else if (tag.equals("WEEKEND_BEGIN_TIME")) {
                                WEEKEND_BEGIN_TIME = false;
                            } else if (tag.equals("WEEKEND_END_TIME")) {
                                WEEKEND_END_TIME = false;
                            } else if (tag.equals("HOLIDAY_BEGIN_TIME")) {
                                HOLIDAY_BEGIN_TIME = false;
                            } else if (tag.equals("HOLIDAY_END_TIME")) {
                                HOLIDAY_END_TIME = false;
                            }else if (tag.equals("FULLTIME_MONTHLY")) {
                                FULLTIME_MONTHLY = false;
                            } else if (tag.equals("RATES")) {
                                RATES = false;
                            } else if (tag.equals("TIME_RATE")) {
                                TIME_RATE = false;
                            } else if (tag.equals("ADD_RATES")) {
                                ADD_RATES = false;
                            } else if (tag.equals("ADD_TIME_RATE")) {
                                ADD_TIME_RATE = false;
                            } else if (tag.equals("DAY_MAXIMUM")) {
                                DAY_MAXIMUM = false;
                            }
                            break;

                        case XmlPullParser.START_TAG: // 먼저
                            tag = parser.getName();

                            if (tag.equals("LAT")) {
                                LAT = true;
                            } else if (tag.equals("LNG")) {
                                LNG = true;
                            } else if (tag.equals("ADDR")) {
                                ADDR = true;
                            } else if (tag.equals("PARKING_NAME")) {
                                PARKING_NAME = true;
                            } else if (tag.equals("TEL")) {
                                TEL = true;
                            } else if (tag.equals("CAPACITY")) {
                                CAPACITY = true;
                            } else if (tag.equals("WEEKDAY_BEGIN_TIME")) {
                                WEEKDAY_BEGIN_TIME = true;
                            } else if (tag.equals("WEEKDAY_END_TIME")) {
                                WEEKDAY_END_TIME = true;
                            } else if (tag.equals("WEEKEND_BEGIN_TIME")) {
                                WEEKEND_BEGIN_TIME = true;
                            } else if (tag.equals("WEEKEND_END_TIME")) {
                                WEEKEND_END_TIME = true;
                            } else if (tag.equals("HOLIDAY_BEGIN_TIME")) {
                                HOLIDAY_BEGIN_TIME = true;
                            } else if (tag.equals("HOLIDAY_END_TIME")) {
                                HOLIDAY_END_TIME = true;
                            } else if (tag.equals("FULLTIME_MONTHLY")) {
                                FULLTIME_MONTHLY = true;
                            } else if (tag.equals("RATES")) {
                                RATES = true;
                            } else if (tag.equals("TIME_RATE")) {
                                TIME_RATE = true;
                            } else if (tag.equals("ADD_RATES")) {
                                ADD_RATES = true;
                            } else if (tag.equals("ADD_TIME_RATE")) {
                                ADD_TIME_RATE = true;
                            } else if (tag.equals("DAY_MAXIMUM")) {
                                DAY_MAXIMUM = true;
                            }
                            break;
                    }
                    parserEvent = parser.next();
                }
            }
        } catch (Exception e) {
            Log.e("Error", "Error in network call", e);
        }

        for (int loop = 0; loop < latitude.size(); loop++) {
            contacts2.add(new MapList(latitude.get(loop),
                    longitude.get(loop),
                    address.get(loop),
                    parkingname.get(loop),
                    tel.get(loop),
                    capacity.get(loop),
                    weekdayBegin.get(loop),
                    weekdayEnd.get(loop),
                    weekendBegin.get(loop),
                    weekendEnd.get(loop),
                    holidayBegin.get(loop),
                    holidayEnd.get(loop),
                    fulltimeMonth.get(loop),
                    rate.get(loop),
                    timeRate.get(loop),
                    addRate.get(loop),
                    addtimeRate.get(loop),
                    dayMaximum.get(loop)
            ));
        }
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }


    private void addItemsToMap(List<MapList> items) {
        if (map != null) {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

            //Loop through all the items that are available to be placed on the map
            for (MapList item : items) {
//                Iterator<MapList> iter = items.iterator();
//                while(iter.hasNext()){
                //If the item is within the the bounds of the screen
                if (bounds.contains(new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude())))) {
                    //If the item isn't already being displayed
                    if (!visibleMarkers.containsKey(item.getParkingname())) {
                        //Add the Marker to the Map and keep track of it with the HashMap
                        //getMarkerForItem just returns a MarkerOptions object

                        View marker = ((LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_api, null);

                        markerAPI = map.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude())))
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MainActivity.this, marker))));
                        markerAPI.setTag("이름" + item.getParkingname() +
                                "주소" + item.getAddress() +
                                "면수" + item.getCapacity() +
                                "평일시작" + item.getwWeekDayBegin() +
                                "평일종료" + item.getWeekDayEnd() +
                                "주말시작" + item.getWeekEndBegin() +
                                "주말종료" + item.getWeekEndEnd() +
                                "공휴일시작" + item.getHolidayBegin() +
                                "공휴일종료" + item.getHolidayEnd() +
                                "월정기권" + item.getFulltimeMonth() +
                                "기본요금" + item.getRate() +
                                "기본시간" + item.getTimeRate() +
                                "추가요금" + item.getAddRate() +
                                "추가시간" + item.getAddtimeRate() +
                                "일최대" + item.getDayMaximum() +
                                "번호" + item.getTel() +
                                "사진" +
                                "UId" +
                                "위도" + item.getLatitude() +
                                "경도" + item.getLongitude() +
                                "토큰" +
                                "운영조건");
                        this.visibleMarkers.put(item.getParkingname(), markerAPI
                        );
                    }
                }

                //If the marker is off screen
                else {
                    //If the course was previously on screen
                    if (visibleMarkers.containsKey(item.getParkingname())) {
                        //1. Remove the Marker from the GoogleMap
                        visibleMarkers.get(item.getParkingname()).remove();

                        //2. Remove the reference to the Marker from the HashMap
                        visibleMarkers.remove(item.getParkingname());
                    }
                }
            }
        }
    }

    private void addUploadItemsToMap(List<Upload> items) {
        Upload upload = new Upload();
        if (map != null) {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

            //Loop through all the items that are available to be placed on the map
            for (Upload item : contacts1) {

                //If the item is within the the bounds of the screen
                if (bounds.contains(new LatLng((item.getLat()), (item.getLon())))) {
                    //If the item isn't already being displayed
                    if (!visibleMarkersUpload.containsKey(item.getName())) {
                        //Add the Marker to the Map and keep track of it with the HashMap
                        //getMarkerForItem just returns a MarkerOptions object

                        View marker2 = ((LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_layout, null);

                        TextView tvMarker = (TextView) marker2.findViewById(R.id.tv_marker);

                        tvMarker.setText(item.getprice());
                        if(item.getprice().equals("0"))tvMarker.setText("무료");
                        Marker markerUpload = map.addMarker(new MarkerOptions()
                                .position(new LatLng(item.getLat(), item.getLon()))
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MainActivity.this, marker2))));

                        markerUpload.setTag("이름" + item.getName() +
                                "주소" + item.getParkingAddress() +
                                "면수" + item.getParkingSize() +
                                "평일시작" + item.getParkingTimeStart() +
                                "평일종료" + item.getParkingTimeFinish() +
                                "주말시작" +
                                "주말종료" +
                                "공휴일시작" +
                                "공휴일종료" +
                                "월정기권" +
                                "기본요금" + item.getprice() +
                                "기본시간" +
                                "추가요금" +
                                "추가시간" +
                                "일최대" +
                                "번호" + item.getPhoneNumb() +
                                "사진" + item.getImageUrl() +
                                "UId" + item.getuserId() +
                                "위도" + item.getLat() +
                                "경도" + item.getLon() +
                                "토큰" + item.getUserToken() +
                                "운영조건" + item.getParkingCond());

                        visibleMarkersUpload.put(item.getName(), markerUpload
                        );
                    }
                }

                //If the marker is off screen
                else {
                    //If the course was previously on screen
                    if (visibleMarkersUpload.containsKey(item.getName())) {
                        //1. Remove the Marker from the GoogleMap
                        visibleMarkersUpload.get(item.getName()).remove();
                        //2. Remove the reference to the Marker from the HashMap
                        visibleMarkersUpload.remove(item.getName());
                    }
                }
            }
        }
    }

}
