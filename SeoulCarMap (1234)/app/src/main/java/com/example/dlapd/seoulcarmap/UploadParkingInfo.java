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
import android.view.Window;
import android.view.WindowManager;
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

import uk.co.senab.photoview.PhotoViewAttacher;

public class UploadParkingInfo extends AppCompatActivity {
    private PhotoViewAttacher photoViewAttacher;
    private ImageView imageView;

    private TextView tvName,
            tvAddress,
            tvTel,
            tvRate,
            tvTime,
            tvCapacity,
            tvCond;

    private ImageButton BtnNavi,
            BtnChat;

    private String name,
            address,
            tel,
            rate,
            timeStart,
            timeEnd,
            image,
            uId,
            lat,
            lng,
            capacity,
            cond,
            token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_parking_info);
        imageView = (ImageView) findViewById(R.id.info_upload_image);
        tvName = (TextView) findViewById(R.id.info_upload_name);
        tvAddress = (TextView) findViewById(R.id.info_upload_address);
        tvTel = (TextView) findViewById(R.id.info_upload_tel);
        tvRate = (TextView) findViewById(R.id.info_upload_rate);
        tvTime = (TextView) findViewById(R.id.info_upload_time);
        tvCapacity = (TextView) findViewById(R.id.info_upload_capacity);
        tvCond = (TextView) findViewById(R.id.info_upload_cond);
        BtnNavi = (ImageButton) findViewById(R.id.info_upload_navi);
        BtnChat = (ImageButton) findViewById(R.id.info_upload_chat);

        Intent intent = getIntent();
        name = intent.getStringExtra("TagName");
        address = intent.getStringExtra("TagAddress");
        tel = intent.getStringExtra("TagTel");
        rate = intent.getStringExtra("TagRate");
        timeStart = intent.getStringExtra("TagWeekDayBegin");
        timeEnd = intent.getStringExtra("TagWeekDayEnd");
        image = intent.getStringExtra("TagImage");
        uId = intent.getStringExtra("TagUId");
        lat = intent.getStringExtra("TagLat");
        lng = intent.getStringExtra("TagLng");
        capacity = intent.getStringExtra("TagCapacity");
        cond = intent.getStringExtra("TagCond");
        token = intent.getStringExtra("TagToken");


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

        if (!rate.equals(""))
            tvRate.setText(rate + "원");

        if (rate.equals("0"))
            tvRate.setText("무료");

        if (!timeStart.equals("") && !timeEnd.equals(""))
            tvTime.setText(timeStart + timeEnd);

        if (!image.equals("")) {
            Picasso.with(UploadParkingInfo.this).load(image).into(imageView);
        }

        if (!capacity.equals(""))
            tvCapacity.setText(capacity);

        tvCond.setText(cond);

        BtnNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{" T map ", " 카카오 내비 "};
                final Integer[] icons = new Integer[]{R.drawable.tmap, R.drawable.kakao_navi};
                ListAdapter adapter = new ArrayAdapterWithIcon(UploadParkingInfo.this, items, icons);
                new AlertDialog.Builder(UploadParkingInfo.this).setTitle("길안내를 시작할 앱을 선택해주세요")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                switch (item) {
                                    case 0:
                                        TMapTapi tmaptapi = new TMapTapi(UploadParkingInfo.this);
                                        boolean isTmapApp = tmaptapi.isTmapApplicationInstalled();
//                                        if (isTmapApp) {
                                            tmaptapi.setSKTMapAuthentication("d163e2cf-8e08-4964-b54a-12dc3b616e63");
                                            tmaptapi.invokeRoute(name, Float.valueOf(lng), Float.valueOf(lat));
//                                        } else
//                                            Toast.makeText(UploadParkingInfo.this, "'Tmap'을 설치해주세요.", Toast.LENGTH_SHORT).show();
                                        break;

                                    case 1:
                                        // Location.Builder를 사용하여 Location 객체를 만든다.
                                        boolean isKakaoNavi = KakaoNaviService.isKakaoNaviInstalled(UploadParkingInfo.this);
                                        if (isKakaoNavi) {
                                            com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(name, Double.valueOf(lng), Double.valueOf(lat)).build();
                                            NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST)
                                                    .setRpOption(RpOption.SHORTEST).build();
                                            KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                                            KakaoNaviService.navigate(UploadParkingInfo.this, builder.build());
                                        } else
                                            Toast.makeText(UploadParkingInfo.this, "'카카오네비'를 설치해주세요", Toast.LENGTH_SHORT).show();
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
                            .putExtra("destinationToken", token);
                    startActivity(intent);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(UploadParkingInfo.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.upload_parking_image);


                ImageView upimage = dialog.findViewById(R.id.upimage);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                //This makes the dialog take up the full width
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(lp);
                Picasso.with(UploadParkingInfo.this).load(image).into(upimage);
                photoViewAttacher = new PhotoViewAttacher(upimage);
                dialog.show();

            }
        });
    }
}
