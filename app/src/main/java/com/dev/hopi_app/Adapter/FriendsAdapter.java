package com.dev.hopi_app.Adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.hopi_app.Activity.ChatActivity;
import com.dev.hopi_app.Activity.ProfileActivity;
import com.dev.hopi_app.Firebase.FirebaseRecyclerAdapter;
import com.dev.hopi_app.Friends;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.Query;

import java.util.ArrayList;

public class FriendsAdapter extends FirebaseRecyclerAdapter<FriendsAdapter.ViewHolder, Friends> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardName;
        TextView cardEmail;
        TextView cardStudentNumber;
        ImageView imgChat;

        public ViewHolder(View view) {
            super(view);
            cardName = (TextView) view.findViewById(R.id.cardName);
            cardEmail = (TextView) view.findViewById(R.id.cardEmail);
            cardStudentNumber = (TextView) view.findViewById(R.id.cardStudentNumber);
            imgChat = (ImageView) view.findViewById(R.id.chat);
        }
    }

    public FriendsAdapter(Query query, Class<Friends> itemClass, @Nullable ArrayList<Friends> items,
                          @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    @Override public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_friends, parent, false);

        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        final Friends item = getItem(position);
        holder.cardName.setText(item.getFirstName()+" "+item.getLastName());
        holder.cardEmail.setText(item.getEmail());
        holder.cardStudentNumber.setText(item.getStudentNumber());

        holder.cardName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("PUSH_ID",item.getPushID());
                v.getContext().startActivity(intent);

//                Toast.makeText(v.getContext(), item.getUserID(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("room_id",item.getChatRoom());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override protected void itemAdded(Friends item, String key, int position) {
        Log.d("FriendsAdapter", "Added a new item to the adapter.");
    }

    @Override protected void itemChanged(Friends oldItem, Friends newItem, String key, int position) {
        Log.d("FriendsAdapter", "Changed an item.");
    }

    @Override protected void itemRemoved(Friends item, String key, int position) {
        Log.d("FriendsAdapter", "Removed an item from the adapter.");
    }

    @Override protected void itemMoved(Friends item, String key, int oldPosition, int newPosition) {
        Log.d("FriendsAdapter", "Moved an item.");
    }

}