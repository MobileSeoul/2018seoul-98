package com.example.dlapd.seoulcarmap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dlapd.seoulcarmap.model.ChatModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends AppCompatActivity {
    private List<Upload> mUploads3;
    private List<ChatModel> chatModels;

    private ImageButton Withdrawal;
    private ImageButton Privacytos;
    private ImageButton locationtos;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        setTitle("환경설정");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        auth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Privacytos =(ImageButton) findViewById(R.id.privacy);
        Privacytos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageActivity.this, PrivacyTOS.class);
                startActivity(intent);
            }
        });

        locationtos =(ImageButton) findViewById(R.id.location);
        locationtos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageActivity.this, LocationTOS.class);
                startActivity(intent);
            }
        });

        mUploads3 = new ArrayList<>();
        chatModels = new ArrayList<>();
        final String muserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Withdrawal = (ImageButton) findViewById(R.id.Withdrawalout);
        Withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ManageActivity.this)
                        .setTitle("회원탈퇴")
                        .setMessage("회원탈퇴를 하시겠습니까? " + "\n"+"탈퇴 시 기존의 모든 게시물은 삭제됩니다"+"\n"+"")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                                FirebaseDatabase.getInstance().getReference().child("uploads").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        mUploads3.clear();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            Upload upload = postSnapshot.getValue(Upload.class);
                                            if (upload.muserId.equals(muserId))
                                                FirebaseDatabase.getInstance().getReference().child("uploads").child(postSnapshot.getKey()).removeValue();
//                                                    upload.setKey(postSnapshot.getKey());
//                                                   mUploads3.add(upload);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                });
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        chatModels.clear();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            ChatModel chatModel = postSnapshot.getValue(ChatModel.class);
//                                            chatModels.add(chatModel);
                                            if (chatModel.users.containsKey(muserId))
                                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(postSnapshot.getKey()).removeValue();
//                                                    upload.setKey(postSnapshot.getKey());
//                                                   mUploads3.add(upload);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                });






                                // 이 버튼 클릭시 삭제 진행
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                // 아무일도 안 일어남
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



            }
        });



    }


    private void delete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            Toast.makeText(ManageActivity.this, "회원탈퇴", Toast.LENGTH_SHORT).show();
                            ActivityCompat.finishAffinity(ManageActivity.this);

//                        startActivity(new Intent(SuccessActivity.this,MainActivity.class));
//                        updateUI(null);
                        }
                    }
                });

        auth.signOut();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                        Toast.makeText(SuccessActivity.this, "회원탈퇴", Toast.LENGTH_SHORT).show();
//                       finish();
                        }else {
//                        Toast.makeText(SuccessActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
//                        return;
                        }
                    }
                });



    }
}