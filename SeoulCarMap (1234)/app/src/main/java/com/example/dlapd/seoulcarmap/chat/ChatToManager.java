package com.example.dlapd.seoulcarmap.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dlapd.seoulcarmap.R;
import com.example.dlapd.seoulcarmap.model.ChatModel;
import com.example.dlapd.seoulcarmap.model.NotificationModel;
import com.example.dlapd.seoulcarmap.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatToManager extends AppCompatActivity {

    // Firebase - Realtime Database

    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    // Firebase - Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

//    private String fcmToken;

    private String destinationUid;
    private ImageButton button;
    private EditText editText;

    private String muserId;
    private String chatRoomUid;

    private String userToken;

    private RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy.MM.dd HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_to_manager);
        muserId = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        destinationUid = "ZTUBxrOyvfMzAetNfXmTdA83mOy2"; // 채팅을 당하는 아이디 (관리자)

        userToken = FirebaseInstanceId.getInstance().getToken();

        button = (ImageButton) findViewById(R.id.chatToManager_button);
        editText = (EditText) findViewById(R.id.chatToManager_editText);



        recyclerView = (RecyclerView)findViewById(R.id.chatToManager_reclclerview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ChatModel chatModel = new ChatModel();
                chatModel.users.put(muserId, true);
                chatModel.users.put(destinationUid, true);
                chatModel.users.put(userToken,true);





                if (chatRoomUid == null){
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();

                        }
                    });
                }else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.muserId = muserId;
                    comment.messagge = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    comment.fcmToken = FirebaseInstanceId.getInstance().getToken();
//                    tokenInfo.uid = muserId;
//                    tokenInfo.destinationUid = destinationUid;
//                    tokenInfo.userToken = userToken;
//                    tokenInfo.destinationToken = destinationToken;


                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editText.setText(""); // 전송버튼 누를시 EDITTEXT 초기화

                        }
                    });
                }

            }
        });
        checkChatRoom();


    }



    void  checkChatRoom(){

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+muserId).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel  chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid)){ //firebase - chatroom 안에 있는 users꾸러미
                        chatRoomUid = item.getKey(); //chatroom 의 uid
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatToManager.this));
                        recyclerView.setAdapter(new ChatToManager.RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        UserModel userModel;
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        void getMessageList(){

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();

                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    //메세지가 갱신
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size() - 1);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.managerchat_item_message,parent,false);

            //ViewHolder - view를 재사용할때 쓰는 class
            return new ChatToManager.RecyclerViewAdapter.MMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatToManager.RecyclerViewAdapter.MMessageViewHolder messageViewHolder2 = ((ChatToManager.RecyclerViewAdapter.MMessageViewHolder)holder);


            //내가 쓰는 말풍선과 상대방이 쓰는 말풍선 나누기 (좌,우)
            //내가보낸 메시지
            if(comments.get(position).muserId.equals(muserId)){
                messageViewHolder2.textView_message2.setText(comments.get(position).messagge);
                messageViewHolder2.textView_message2.setBackgroundResource(R.drawable.bubble2);
                messageViewHolder2.linearLayout_destination2.setVisibility(View.INVISIBLE);
                messageViewHolder2.textView_message2.setTextSize(20);
                messageViewHolder2.linearLayout_main2.setGravity(Gravity.RIGHT);
//                setReadCounter(position, messageViewHolder.textView_readCounter_left);
                //상대방이 보낸 메시지
            }else {
                //채팅객체 이미지 설정
//                Glide.with(holder.itemView.getContext())
//                        .load(R.drawable.account)
//                        .apply(new RequestOptions().circleCrop())
//                        .into(messageViewHolder2.imageView_profile2);
//                messageViewHolder2.textview_name2.setText("사용자");
                messageViewHolder2.linearLayout_destination2.setVisibility(View.VISIBLE);
                messageViewHolder2.textView_message2.setBackgroundResource(R.drawable.bubble1);
                messageViewHolder2.textView_message2.setText(comments.get(position).messagge);
                messageViewHolder2.textView_message2.setTextSize(20);
                messageViewHolder2.linearLayout_main2.setGravity(Gravity.LEFT);
//                setReadCounter(position, messageViewHolder.textView_readCounter_right);

            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date2 = new Date(unixTime);
            simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat2.format(date2);
            messageViewHolder2.textView_timestamp2.setText(time);


        }


        @Override
        public int getItemCount() {
            return comments.size(); // 카운터를 넘겨줘야 정확하게 몇번 돌아가는지 알 수 있다.
        }

        private class MMessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message2;
//            public TextView textview_name2;
//            public ImageView imageView_profile2;
            public LinearLayout linearLayout_destination2;
            public LinearLayout linearLayout_main2;
            public TextView textView_timestamp2;


            public MMessageViewHolder(View view) {
                super(view);
                textView_message2 = (TextView) view.findViewById(R.id.messageItem_textView_message2);
//                textview_name2 = (TextView)view.findViewById(R.id.messageItem_textview_name2);
//                imageView_profile2 = (ImageView)view.findViewById(R.id.messageItem_imageview_profile2);
                linearLayout_destination2 = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_destination2);
                linearLayout_main2 = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main2);
                textView_timestamp2 = (TextView)view.findViewById(R.id.messageItem_textview_timestamp2);

            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);
    }
}