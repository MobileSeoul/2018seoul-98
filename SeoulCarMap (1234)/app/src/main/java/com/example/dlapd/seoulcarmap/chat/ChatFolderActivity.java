package com.example.dlapd.seoulcarmap.chat;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dlapd.seoulcarmap.ImagesActivity;
import com.example.dlapd.seoulcarmap.MainActivity;
import com.example.dlapd.seoulcarmap.R;
import com.example.dlapd.seoulcarmap.Upload;
import com.example.dlapd.seoulcarmap.chat.MessageActivity;
import com.example.dlapd.seoulcarmap.model.ChatModel;
import com.example.dlapd.seoulcarmap.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.example.dlapd.seoulcarmap.MainActivity.TagToken;
//import static com.example.dlapd.seoulcarmap.MainActivity.TagUId;

public class ChatFolderActivity extends AppCompatActivity {
    SwipeController swipeController = null;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseRef;
    private List<ChatModel> chatModels;
    private ValueEventListener mDBListener;


    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_folder);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.chatfolder_recyclerview);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatModels = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("chatrooms");   // db 삭제를 위해 firebase - chatroom 접근


        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    ChatModel chatModel = postSnapshot.getValue(ChatModel.class);
//                    chatModel.setKey(postSnapshot.getKey());
//                    chatModels.add(chatModel);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(final int position) {


                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        ChatModel selectedItem = chatModels.get(position);
                        final String selectedKey = selectedItem.getKey();
//                        String selectedKey = dataSnapshot.getKey();

                        final AlertDialog.Builder builder = new AlertDialog.Builder( ChatFolderActivity.this);
//                        builder.setTitle("알림");
                        builder.setMessage("채팅방에서 나가시겠습니까?"+"\n"+"나가기를 하면 대화내용이 모두 삭제되고"+"\n"+"채팅 목록에서도 삭제됩니다.");
                        builder.setCancelable(true);
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                mDatabaseRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ChatFolderActivity.this, "채팅목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(ChatFolderActivity.this, ChatFolderActivity.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();

                            }
                        });
                        builder.create().show();



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            };
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


    }



    class ChatRecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private String muserId;
        private String mParkingAddress;
        private ArrayList<String> destinationUsers = new ArrayList<>();



        public ChatRecyclerViewAdapter() {
            muserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+muserId).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for (DataSnapshot item :dataSnapshot.getChildren()){

                        ChatModel chatModel = item.getValue(ChatModel.class);
                        chatModel.setKey(item.getKey());
                        chatModels.add(chatModel);

                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);


            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {



            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;

            // 일일이 챗방에 있는 유저를 체크
            for(String user: chatModels.get(position).users.keySet()){
                if(!user.equals(muserId)){
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel userModel =  dataSnapshot.getValue(UserModel.class);
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(R.drawable.usericon2)
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);

                    customViewHolder.textView_title.setText("주차 문의");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //메시지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).messagge);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    MainActivity main = new MainActivity();
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",destinationUsers.get(position));
                    intent.putExtra("destinationToken", TagToken);

                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright,R.anim.toleft);
                        startActivity(intent,activityOptions.toBundle());
                    }





                }
            });


            //TimeStamp
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));



        }

        public void delete(int position) { // 삭제 메서드

            try {

                chatModels.remove(position);
//
                notifyItemRemoved(position);

            } catch(IndexOutOfBoundsException ex) {

                ex.printStackTrace();

            }

        }


        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;

            public CustomViewHolder(View view) {
                super(view);

                imageView = (ImageView) view.findViewById(R.id.chatitem_imageview);
                textView_title = (TextView)view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = (TextView)view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp = (TextView)view.findViewById(R.id.chatitem_textview_timestamp);
            }
        }





    }


}