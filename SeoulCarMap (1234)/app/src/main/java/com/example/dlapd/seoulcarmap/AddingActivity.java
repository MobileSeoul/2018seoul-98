package com.example.dlapd.seoulcarmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.Manifest;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

import static android.text.TextUtils.isEmpty;
import static java.security.AccessController.getContext;


public class AddingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    static int PReqCode = 1;
    static int REQESTCODE = 1;


    String[] items = { "00시",
            "01시",
            "02시",
            "03시",
            "04시",
            "05시",
            "06시",
            "07시",
            "08시",
            "09시",
            "10시",
            "11시",
            "12시",
            "13시",
            "14시",
            "15시",
            "16시",
            "17시",
            "18시",
            "19시",
            "20시",
            "21시",
            "22시",
            "23시",
            "24시" };

    /*private Button mButtonChooseImage;
    private Button mButtonUpload;
    private Button mMaps;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;*/

    private ImageButton uploadBtn;
    private EditText parkingName;
    private EditText phoneNumber;
    private TextView addressText;
    private EditText parkingSize;
    private EditText parkingCond;
    private  Spinner opTimeStart;
    private Spinner opTimeFinish;
//    private TextView opTimeStart;

    //    private TextView opTimeFinish;
    private EditText price;
    private String userId;

    private String fcmToken;

    private ImageView picImageView;



    double lat_value;
    double lon_value;

    private Uri mImageUri;

    private FirebaseAuth auth;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addinglayout);


        uploadBtn = findViewById(R.id.uploadbtn);
        parkingName = findViewById(R.id.parkingname);
        phoneNumber = findViewById(R.id.phonenumber);
        addressText = findViewById(R.id.addresstext);
        parkingSize = findViewById(R.id.parkingsize);
        parkingCond = findViewById(R.id.parkingcond);
        opTimeStart = findViewById(R.id.optimeStart);
        opTimeFinish = findViewById(R.id.optimeFinish);
        picImageView = findViewById(R.id.picimageview);
        price = findViewById(R.id.price);





        Intent intent=getIntent();
        String address=intent.getStringExtra("address");
        lat_value = intent.getDoubleExtra("lat",0);
        lon_value = intent.getDoubleExtra("lon",0);

        addressText.setText(address);


        /*mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mMaps = findViewById(R.id.button_map);*/

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        fcmToken = FirebaseInstanceId.getInstance().getToken();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");





        picImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=22){
                    checkAndRequestForPermission();
                }
                else{
                    openGallery();
                }


//                openFileChooser();
            }
        });
        Spinner optimeStart =(Spinner) findViewById(R.id.optimeStart) ;
        // 어댑터 객체 생성
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 어댑터 설정
        optimeStart.setAdapter(adapter);
        optimeStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 아이템이 선택되었을 때 호출됨
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//                textView.setText(items[position]);
            }

            // 아무것도 선택되지 않았을 때 호출됨
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                textView.setText("");
            }
        });
        Spinner optimeFinish =(Spinner) findViewById(R.id.optimeFinish) ;
        // 어댑터 객체 생성
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 어댑터 설정
        optimeFinish.setAdapter(adapter2);
        optimeFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 아이템이 선택되었을 때 호출됨
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//                textView.setText(items[position]);
            }

            // 아무것도 선택되지 않았을 때 호출됨
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                textView.setText("");
            }
        });



        uploadBtn.setOnClickListener(new View.OnClickListener() {

            /* String name = parkingName.getText().toString();
             String phone = phoneNumber.getText();*/
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddingActivity.this, "업로드중...", Toast.LENGTH_SHORT).show();

                } else {
                    uploadFile();


                }
//                    if (mUploadTask != null && mUploadTask.isInProgress()){
//                        startActivity(new Intent(AddingActivity.this, MainActivity.class));
//                        finish();
//                    }

            }
        });

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image!

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQESTCODE);

    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AddingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(AddingActivity.this, "갤러리권한을 승인해주세요", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AddingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);


            }

        }
        else openGallery();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(picImageView);
        }
    }








    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if (!isEmpty(parkingName.getText().toString()) && !isEmpty(phoneNumber.getText().toString()) && !isEmpty(price.getText().toString())) {
            if (mImageUri != null) {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            /*Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);*/
                                ActivityCompat.finishAffinity(AddingActivity.this);
                                startActivity(new Intent(AddingActivity.this, MainActivity.class));
                                finish();
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(AddingActivity.this, "업로드 완료", Toast.LENGTH_LONG).show();

                                Upload upload = new Upload(
                                        fcmToken,
                                        parkingName.getText().toString().trim(),
                                        phoneNumber.getText().toString().trim(),
                                        addressText.getText().toString().trim(),
                                        lat_value,
                                        lon_value,
                                        parkingSize.getText().toString().trim(),
                                        parkingCond.getText().toString().trim(),
                                        opTimeStart.getSelectedItem().toString().trim().substring(0,2)+":00~ ",
                                        opTimeFinish.getSelectedItem().toString().trim().substring(0,2)+":00",
                                        price.getText().toString().trim(),
                                        userId,
                                        taskSnapshot.getDownloadUrl().toString());



                            /*Upload upload = new Upload();
                            upload.setName(parkingName.getText().toString());
                            upload.setPhoneNUmb(phoneNumber.getText().toString());
                            upload.setImageUrl(taskSnapshot.getDownloadUrl().toString());*/
                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(uploadId).setValue(upload);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            /*double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);*/
                            }
                        });
            }else {Toast.makeText(AddingActivity.this, "최소 한장 이상의 사진 필요", Toast.LENGTH_LONG).show();}
        }else {Toast.makeText(AddingActivity.this, "주차장 기본정보와 사진은 필수 입력 항목입니다", Toast.LENGTH_LONG).show();}
    }

}

