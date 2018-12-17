package com.example.dlapd.seoulcarmap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dlapd.seoulcarmap.chat.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.skt.Tmap.TMapTapi;
import com.squareup.picasso.Picasso;

import static com.example.dlapd.seoulcarmap.MainActivity.TagToken;

public class ParkingInfo extends AppCompatActivity {
    private ImageView imageView;

    private TextView tvName,
            tvAddress,
            tvTel,
            tvCapacity,
            tvRate,
            tvAddRate,
            tvWeekDay,
            tvWeekEnd,
            tvHoliday,
            tvDayMaximum,
            tvFulltimeMonth;

    private ImageButton BtnNavi,
            BtnChat;

    private String name,
            address,
            tel,
            capacity,
            rate,
            timeRate,
            addRate,
            addTimeRate,
            weekDayBegin,
            weekDayEnd,
            weekEndBegin,
            weekEndEnd,
            holidayBegin,
            holidayEnd,
            fulltiomMonth,
            dayMaximum,
            image,
            uId,
            lat,
            lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_info);
        imageView = (ImageView) findViewById(R.id.imageView2);
        tvName = (TextView) findViewById(R.id.tv_Name);
        tvAddress = (TextView) findViewById(R.id.tv_Address);
        tvTel = (TextView) findViewById(R.id.tv_Tel);
        tvCapacity = (TextView) findViewById(R.id.tv_Capacity);
        tvRate = (TextView) findViewById(R.id.tv_Rate);
        tvAddRate = (TextView) findViewById(R.id.tv_AddRate);
        tvWeekDay = (TextView) findViewById(R.id.tv_WeekDay);
        tvWeekEnd = (TextView) findViewById(R.id.tv_WeekEnd);
        tvHoliday = (TextView) findViewById(R.id.tv_Holiday);
        tvDayMaximum = (TextView) findViewById(R.id.tv_DayMaximum);
        tvFulltimeMonth = (TextView) findViewById(R.id.tv_FulltimeMonth);
        BtnNavi = (ImageButton) findViewById(R.id.info_btn_navi);
        BtnChat = (ImageButton) findViewById(R.id.info_btn_chat);

        Intent intent = getIntent();
        name = intent.getStringExtra("TagName");
        address = intent.getStringExtra("TagAddress");
        tel = intent.getStringExtra("TagTel");
        capacity = intent.getStringExtra("TagCapacity");
        rate = intent.getStringExtra("TagRate");
        timeRate = intent.getStringExtra("TagTimeRate");
        addRate = intent.getStringExtra("TagAddRate");
        addTimeRate = intent.getStringExtra("TagAddtimeRate");
        weekDayBegin = intent.getStringExtra("TagWeekDayBegin");
        weekDayEnd = intent.getStringExtra("TagWeekDayEnd");
        weekEndBegin = intent.getStringExtra("TagWeekEndBegin");
        weekEndEnd = intent.getStringExtra("TagWeekEndEnd");
        holidayBegin = intent.getStringExtra("TagHolidayBegin");
        holidayEnd = intent.getStringExtra("TagHolidayEnd");
        fulltiomMonth = intent.getStringExtra("TagFulltimeMonth");
        dayMaximum = intent.getStringExtra("TagDayMaximum");
        image = intent.getStringExtra("TagImage");
        uId = intent.getStringExtra("TagUId");
        lat = intent.getStringExtra("TagLat");
        lng = intent.getStringExtra("TagLng");


        if (!name.equals("")) tvName.setText(name);

        if (!address.equals("")) tvAddress.setText(address);

        if (!tel.equals("")) tvTel.setText(tel);
        tvTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                startActivity(intent);
            }
        });
        if (!capacity.equals("")) tvCapacity.setText(capacity);
        if (!rate.equals("") && !timeRate.equals(""))
            tvRate.setText(timeRate + "분당 " + rate + "원");

        if (rate.equals("0") && timeRate.equals(""))
            tvRate.setText("무료");

        if (!addRate.equals("") && !addTimeRate.equals(""))
            tvAddRate.setText(addTimeRate + "분당 " + addRate + "원");

        if (!dayMaximum.equals("") || !dayMaximum.equals("0"))
            tvDayMaximum.setText(dayMaximum + "원");

        if (!fulltiomMonth.equals("")) tvFulltimeMonth.setText(fulltiomMonth + "원");


        if (!weekDayBegin.equals("") && !weekDayEnd.equals(""))
            tvWeekDay.setText(weekDayBegin + weekDayEnd);


        if (!weekEndBegin.equals("") && !weekEndEnd.equals(""))
            tvWeekEnd.setText(weekEndBegin + weekEndEnd);


        if (!holidayBegin.equals("") && !holidayEnd.equals(""))
            tvHoliday.setText(holidayBegin + holidayEnd);

        if (!image.equals("")) {
            Picasso.with(ParkingInfo.this).load(image).into(imageView);
        }

        BtnNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{" T map ", " 카카오 내비 "};
                final Integer[] icons = new Integer[]{R.drawable.tmap, R.drawable.kakao_navi};
                ListAdapter adapter = new ArrayAdapterWithIcon(ParkingInfo.this, items, icons);
                new AlertDialog.Builder(ParkingInfo.this).setTitle("길안내를 시작할 앱을 선택해주세요")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                switch (item) {
                                    case 0:
                                        TMapTapi tmaptapi = new TMapTapi(ParkingInfo.this);
                                        boolean isTmapApp = tmaptapi.isTmapApplicationInstalled();

                                            tmaptapi.setSKTMapAuthentication("d163e2cf-8e08-4964-b54a-12dc3b616e63");
                                            tmaptapi.invokeRoute(name, Float.valueOf(lng), Float.valueOf(lat));
                                        break;

                                    case 1:
                                        // Location.Builder를 사용하여 Location 객체를 만든다.
                                        boolean isKakaoNavi = KakaoNaviService.isKakaoNaviInstalled(ParkingInfo.this);
                                        if (isKakaoNavi) {
                                            com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(name, Double.valueOf(lng), Double.valueOf(lat)).build();
                                            NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST)
                                                    .setRpOption(RpOption.SHORTEST).build();
                                            KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                                            KakaoNaviService.navigate(ParkingInfo.this, builder.build());
                                        } else
                                            Toast.makeText(ParkingInfo.this, "'카카오내비'가 설치되어 있지 않습니다", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }).show();


            }
        });

        BtnChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (uId.equals(myUid)) { // ???
                    Toast.makeText(getApplicationContext(), "자신의 게시물입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class)
                            .putExtra("destinationUid", uId)
                            .putExtra("destinationToken", TagToken);
                    startActivity(intent);
                }
            }
        });
    }
}
