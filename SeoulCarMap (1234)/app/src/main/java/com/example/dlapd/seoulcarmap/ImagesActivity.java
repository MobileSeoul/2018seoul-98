package com.example.dlapd.seoulcarmap;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private FirebaseAuth auth;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    private List<Upload> mUploads;

    private Toolbar toolbar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);


        toolbar1 = (Toolbar) findViewById(R.id.toolbar_draw);


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(ImagesActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private ActionBar getSupportActionBar(Toolbar toolbar1) {
        return getDelegate().getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);


        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                Toast.makeText(ImagesActivity.this, "검색하기", Toast.LENGTH_LONG).show();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Toast.makeText(ImagesActivity.this, "검색닫기", Toast.LENGTH_LONG).show();

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
        final List<Upload> newList = new ArrayList<>();

        for (Upload name : mUploads) {
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
        mAdapter.updateList(newList, userInput);
        mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Upload selectedItem =newList.get(position);

                Intent intent = new Intent(ImagesActivity.this, UploadParkingInfo.class);
                intent.putExtra("TagName", selectedItem.getName())
                        .putExtra("TagAddress", selectedItem.getParkingAddress())
                        .putExtra("TagCapacity", selectedItem.getParkingSize())
                        .putExtra("TagWeekDayBegin", selectedItem.getParkingTimeStart())
                        .putExtra("TagWeekDayEnd", selectedItem.getParkingTimeFinish())
                        .putExtra("TagRate", selectedItem.getprice())
                        .putExtra("TagTel", selectedItem.getPhoneNumb())
                        .putExtra("TagImage", selectedItem.getImageUrl())
                        .putExtra("TagUId", selectedItem.getuserId())
                        .putExtra("TagLat", selectedItem.getLat().toString())
                        .putExtra("TagLng", selectedItem.getLon().toString())
                        .putExtra("TagToken", selectedItem.getUserToken())
                        .putExtra("TagCond", selectedItem.getParkingCond());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                auth = FirebaseAuth.getInstance();

                Upload selectedItem = newList.get(position);
                final String selectedKey = selectedItem.getKey();

                if (auth.getCurrentUser().getUid().equals(selectedItem.getuserId())) {

                    StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());

                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseRef.child(selectedKey).removeValue();
                            Toast.makeText(ImagesActivity.this, "삭제 완료", Toast.LENGTH_LONG).show();

                            ActivityCompat.finishAffinity(ImagesActivity.this);
                            Intent intent=new Intent(ImagesActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(ImagesActivity.this, "타인의 게시물 삭제 불가", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }


    @Override
    public void onItemClick(int position) {
        Upload selectedItem = mUploads.get(position);
        Intent intent = new Intent(ImagesActivity.this, UploadParkingInfo.class);
        intent.putExtra("TagName", selectedItem.getName())
                .putExtra("TagAddress", selectedItem.getParkingAddress())
                .putExtra("TagCapacity", selectedItem.getParkingSize())
                .putExtra("TagWeekDayBegin", selectedItem.getParkingTimeStart())
                .putExtra("TagWeekDayEnd", selectedItem.getParkingTimeFinish())
                .putExtra("TagFulltimeMonth", "")
                .putExtra("TagRate", selectedItem.getprice())
                .putExtra("TagTel", selectedItem.getPhoneNumb())
                .putExtra("TagImage", selectedItem.getImageUrl())
                .putExtra("TagUId", selectedItem.getuserId())
                .putExtra("TagLat", selectedItem.getLat().toString())
                .putExtra("TagLng", selectedItem.getLon().toString())
                .putExtra("TagToken", selectedItem.getUserToken())
                .putExtra("TagCond", selectedItem.getParkingCond());
        startActivity(intent);
    }



    @Override
    public void onDeleteClick(int position) {
        auth = FirebaseAuth.getInstance();

        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        if (auth.getCurrentUser().getUid().equals(selectedItem.getuserId())) {

            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());

            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDatabaseRef.child(selectedKey).removeValue();
                    Toast.makeText(ImagesActivity.this, "삭제 완료", Toast.LENGTH_LONG).show();

                    ActivityCompat.finishAffinity(ImagesActivity.this);
                    Intent intent=new Intent(ImagesActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(ImagesActivity.this, "타인의 게시물 삭제 불가", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }


}