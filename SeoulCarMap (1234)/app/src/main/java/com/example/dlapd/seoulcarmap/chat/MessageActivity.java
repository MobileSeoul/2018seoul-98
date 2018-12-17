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

public class MessageActivity extends AppCompatActivity {
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA-B7JbRY:APA91bHyy6TyWrJBOmLmduwtRbxvEE6-2My4AhUwlhypJRGoe2jpOsu-GQK9JJqR26dfAySzxCyWQIe8Lw_APv4xEqZavXreIalSw8w0h7jcvpjRF8wgQMq_XfEG83Q0dHXwQcrNqcg7P59AIj5M5KTq_Yb-i42kIw";
    // Firebase - Realtime Database

    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    // Firebase - Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

//    private String fcmToken;

    private String destinationUid;
    private ImageButton imageButton;
    private EditText editText;

    private String muserId;
    private String chatRoomUid;

    private String userToken;
    private String destinationToken;

    private RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        muserId = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 아이디

        userToken = FirebaseInstanceId.getInstance().getToken();
        destinationToken = getIntent().getStringExtra("destinationToken");

        imageButton = (ImageButton) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);



        recyclerView = (RecyclerView)findViewById(R.id.messageActivity_reclclerview);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ChatModel chatModel = new ChatModel();
                chatModel.users.put(muserId, true);
                chatModel.users.put(destinationUid, true);
                chatModel.users.put(userToken,true);
                chatModel.users.put(destinationToken,true);




                if (chatRoomUid == null){
                    imageButton.setEnabled(false);
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
                            sendFcm(); // 상대방에게 푸시알림 보내기
                        }
                    });
                }

            }
        });
        checkChatRoom();


    }
    void sendFcm(){
        Gson gson = new Gson();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationToken;
        notificationModel.notification.title = "메시지가 도착하였습니다.";
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = "메시지가 도착하였습니다.";
        notificationModel.data.text = editText.getText().toString();


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=" + SERVER_KEY)
                .url(FCM_MESSAGE_URL)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }


//
//    // 채팅 fcm
//    private void sendPostToFCM(final ChatModel chatModel, final String message) {
//
//        mFirebaseDatabase.getReference("chatrooms")
//                .child(chatRoomUid).child("users")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            final ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
//
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            // FMC 메시지 생성 start
//                                            JSONObject root = new JSONObject();
//                                            JSONObject notification = new JSONObject();
//                                            notification.put("body", message);
//                                            notification.put("title", getString(R.string.app_name));
//                                            root.put("notification", notification);
//                                            root.put("to", destinationToken);
//                                            // FMC 메시지 생성 end
//
//                                            URL Url = new URL(FCM_MESSAGE_URL);
//                                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
//                                            conn.setRequestMethod("POST");
//                                            conn.setDoOutput(true);
//                                            conn.setDoInput(true);
//                                            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
//                                            conn.setRequestProperty("Accept", "application/json");
//                                            conn.setRequestProperty("Content-type", "application/json");
//                                            OutputStream os = conn.getOutputStream();
//                                            os.write(root.toString().getBytes("utf-8"));
//                                            os.flush();
//                                            conn.getResponseCode();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }).start();
//                            }
//
//
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
//    // 채팅 fcm


    void  checkChatRoom(){

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+muserId).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel  chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid)){ //firebase - chatroom 안에 있는 users꾸러미
                        chatRoomUid = item.getKey(); //chatroom 의 uid
                        imageButton.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);

            //ViewHolder - view를 재사용할때 쓰는 class
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);


            //내가 쓰는 말풍선과 상대방이 쓰는 말풍선 나누기 (좌,우)
            //내가보낸 메시지
            if(comments.get(position).muserId.equals(muserId)){
                messageViewHolder.textView_message.setText(comments.get(position).messagge);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.bubble2);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
//                setReadCounter(position, messageViewHolder.textView_readCounter_left);
                //상대방이 보낸 메시지
            }else {
                //채팅객체 이미지 설정
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.bubble1);
                messageViewHolder.textView_message.setText(comments.get(position).messagge);
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
//                setReadCounter(position, messageViewHolder.textView_readCounter_right);

            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);


        }


        @Override
        public int getItemCount() {
            return comments.size(); // 카운터를 넘겨줘야 정확하게 몇번 돌아가는지 알 수 있다.
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
//            public TextView textview_name;
//            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;


            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
//                textview_name = (TextView)view.findViewById(R.id.messageItem_textview_name);
//                imageView_profile = (ImageView)view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView)view.findViewById(R.id.messageItem_textview_timestamp);

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