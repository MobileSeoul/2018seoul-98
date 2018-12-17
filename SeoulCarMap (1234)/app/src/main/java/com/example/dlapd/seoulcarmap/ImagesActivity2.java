package com.example.dlapd.seoulcarmap;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dlapd.seoulcarmap.model.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity2 extends AppCompatActivity implements ImageAdapter2.OnItemClickListener, SearchView.OnQueryTextListener {

    private FirebaseAuth auth;
    private RecyclerView mRecyclerView;
    private ImageAdapter2 mAdapter2;
    private String muserId;
    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    private List<Upload> mUploads2;

    private Toolbar toolbar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images2);


        toolbar1 = (Toolbar) findViewById(R.id.toolbar_draw2);

        muserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = findViewById(R.id.recycler_view2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle2);

        mUploads2 = new ArrayList<>();

        mAdapter2 = new ImageAdapter2(ImagesActivity2.this, mUploads2);

        mRecyclerView.setAdapter(mAdapter2);

        mAdapter2.setOnItemClickListener(ImagesActivity2.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        final String muserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDBListener = mDatabaseRef.orderByChild(muserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads2.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());

                    if (upload.muserId.equals(muserId))
                        mUploads2.add(upload);
                }

                mAdapter2.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mDBListener = mDatabaseRef.orderByChild(muserId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                mUploads2.clear();
//
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Upload upload = postSnapshot.getValue(Upload.class);
//                    upload.setKey(postSnapshot.getKey());
//                    mUploads2.add(upload);
//                }
//
//                mAdapter2.notifyDataSetChanged();
//
//                mProgressCircle.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(ImagesActivity2.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                mProgressCircle.setVisibility(View.INVISIBLE);
//            }
//        });
    }

    private ActionBar getSupportActionBar(Toolbar toolbar1) {
        return getDelegate().getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu2, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search2);
        SearchView searchView2 = (SearchView) menuItem.getActionView();
        searchView2.setOnQueryTextListener(this);


        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                Toast.makeText(ImagesActivity2.this, "검색하기", Toast.LENGTH_LONG).show();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Toast.makeText(ImagesActivity2.this, "검색닫기", Toast.LENGTH_LONG).show();

                return true;
            }
        };
        return true;


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        List<Upload> newList = new ArrayList<>();

        for (Upload name : mUploads2) {
            if (name.getName().contains(userInput)
                    || name.getParkingAddress().contains(userInput)
                    || name.getParkingTimeStart().contains(userInput)
                    || name.getParkingTimeFinish().contains(userInput)
                    || name.getName().replaceAll(" ", "").contains(userInput)
                    || name.getParkingAddress().replaceAll(" ", "").contains(userInput)
                    || name.getParkingTimeStart().replaceAll(" ", "").contains(userInput)
                    || name.getParkingTimeFinish().replaceAll(" ", "").contains(userInput)) {
                newList.add(name);
            }
        }
        final SpannableStringBuilder sp = new SpannableStringBuilder(userInput);
        sp.setSpan(new ForegroundColorSpan(Color.RED), 0, userInput.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAdapter2.updateList(newList, userInput);
        return true;
    }


    @Override
    public void onItemClick(int position) {
        Upload selectedItem = mUploads2.get(position);
//        Intent intent = new Intent(ImagesActivity2.this, UploadParkingInfo.class);
//        intent.putExtra("TagName", selectedItem.getName())
//        .putExtra("TagAddress", selectedItem.getParkingAddress())
//        .putExtra("TagCapacity", selectedItem.getParkingSize())
//        .putExtra("TagWeekDayBegin", selectedItem.getParkingTimeStart())
//        .putExtra("TagWeekDayEnd", selectedItem.getParkingTimeFinish())
//        .putExtra("TagFulltimeMonth", "")
//        .putExtra("TagRate", selectedItem.getprice())
//        .putExtra("TagTel", selectedItem.getPhoneNumb())
//        .putExtra("TagImage", selectedItem.getImageUrl())
//        .putExtra("TagUId", selectedItem.getuserId())
//        .putExtra("TagLat", selectedItem.getLat().toString())
//        .putExtra("TagLng", selectedItem.getLon().toString())
//        .putExtra("TagToken", selectedItem.getUserToken());
//        startActivity(intent);
    }



    @Override
    public void onDeleteClick(int position) {
        auth = FirebaseAuth.getInstance();

        Upload selectedItem = mUploads2.get(position);
        final String selectedKey = selectedItem.getKey();

        if (auth.getCurrentUser().getUid().equals(selectedItem.getuserId())) {

            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());

            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDatabaseRef.child(selectedKey).removeValue();
                    Toast.makeText(ImagesActivity2.this, "삭제 완료", Toast.LENGTH_SHORT).show();

                    ActivityCompat.finishAffinity(ImagesActivity2.this);
                    Intent intent=new Intent(ImagesActivity2.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(ImagesActivity2.this, "타인의 게시물 삭제 불가", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }


}