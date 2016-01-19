package com.dev.hopi_app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hopi_app.Chat;
import com.dev.hopi_app.Firebase.FirebaseRecyclerAdapter1;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Firebase.FirebaseRecyclerAdapter;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    public static String TAG = "FirebaseUI.chat";
    Firebase mRef;
    Query mChatRef;
    Button mSendButton;
    EditText mMessageEdit;
    SharedPreferences sharedPref;
    String roomID;
    Firebase myFirebaseRef;

     RecyclerView mMessages;
     FirebaseRecyclerAdapter1<Chat, ChatHolder> mRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_recycler);

        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com/users");
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageEdit = (EditText) findViewById(R.id.messageEdit);
        Firebase.setAndroidContext(this);

        Intent text = getIntent();
        final Bundle friend = text.getExtras();
        if(friend != null){
            roomID =(String) friend.get("room_id");
            Toast.makeText(ChatActivity.this, roomID, Toast.LENGTH_SHORT).show();
            mRef = new Firebase("https://hopiiapp.firebaseio.com/messages/"+roomID);
            mChatRef = mRef.limitToLast(50);
        } else {
            mRef = new Firebase("https://hopiiapp.firebaseio.com/chat-room/");
            mChatRef = mRef.limitToLast(50);
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat(sharedPref.getString("firstName",""), sharedPref.getString("pushID",""), mMessageEdit.getText().toString());
                mRef.push().setValue(chat, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.e(TAG, firebaseError.toString());
                        }
                    }
                });

                String timeStamp = new SimpleDateFormat("MMM dd yyyy - h.mm a").format(new Date());
                Firebase auditRef = new Firebase("https://hopiiapp.firebaseio.com/audit-trail");
                Firebase tempRef = auditRef.push();
                tempRef.child("action").setValue("New Message: "+ mMessageEdit.getText().toString() +" has been sent");
                tempRef.child("user").setValue(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
                tempRef.child("timestamp").setValue(timeStamp);

                mMessageEdit.setText("");


            }
        });

        mMessages = (RecyclerView) findViewById(R.id.messagesList);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(false);

        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(manager);

        mRecycleViewAdapter = new FirebaseRecyclerAdapter1<Chat, ChatHolder>(Chat.class, R.layout.message, ChatHolder.class, mChatRef) {
            @Override
            public void populateViewHolder(ChatHolder chatView, Chat chat, int position) {
                chatView.setName(chat.getName());
                chatView.setText(chat.getText());

                if (sharedPref.getString("pushID","").equals(chat.getPushID())) {
                    chatView.setIsSender(true);
                } else {
                    chatView.setIsSender(false);
                }
            }
        };

        mMessages.setAdapter(mRecycleViewAdapter);
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setIsSender(Boolean isSender) {
            FrameLayout left_arrow = (FrameLayout) mView.findViewById(R.id.left_arrow);
            FrameLayout right_arrow = (FrameLayout) mView.findViewById(R.id.right_arrow);
            RelativeLayout messageContainer = (RelativeLayout) mView.findViewById(R.id.message_container);
            LinearLayout message = (LinearLayout) mView.findViewById(R.id.message);

            if (isSender) {
                left_arrow.setVisibility(View.GONE);
                right_arrow.setVisibility(View.VISIBLE);
                messageContainer.setGravity(Gravity.RIGHT);
            } else {
                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.GONE);
                messageContainer.setGravity(Gravity.LEFT);
            }
        }

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.name_text);
            field.setText(name);
        }

        public void setText(String text) {
            TextView field = (TextView) mView.findViewById(R.id.message_text);
            field.setText(text);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");
//        Toast.makeText(ChatActivity.this, "Destroy!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(){
        super.onStart();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("online");
//        Toast.makeText(ChatActivity.this, "Start!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");
//        Toast.makeText(ChatActivity.this, "Pause!!", Toast.LENGTH_SHORT).show();
    }

}
