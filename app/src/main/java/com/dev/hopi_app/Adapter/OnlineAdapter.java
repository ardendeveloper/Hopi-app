package com.dev.hopi_app.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.hopi_app.Activity.ProfileActivity;
import com.dev.hopi_app.Firebase.FirebaseRecyclerAdapter;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.Query;

import java.util.ArrayList;


public class OnlineAdapter extends FirebaseRecyclerAdapter<OnlineAdapter.ViewHolder, Users> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardName;
        TextView cardEmail;
        TextView cardStudentNumber;
        TextView cardStatus;

        public ViewHolder(View view) {
            super(view);
            cardName = (TextView) view.findViewById(R.id.cardName);
            cardEmail = (TextView) view.findViewById(R.id.cardEmail);
            cardStudentNumber = (TextView) view.findViewById(R.id.cardStudentNumber);
            cardStatus = (TextView) view.findViewById(R.id.cardStatus);
        }
    }

    public OnlineAdapter(Query query, Class<Users> itemClass, @Nullable ArrayList<Users> items,
                         @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    @Override public OnlineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_users, parent, false);

        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(OnlineAdapter.ViewHolder holder, int position) {
        final Users item = getItem(position);
        holder.cardName.setText(item.getFirstName()+" "+item.getLastName());
        holder.cardEmail.setText(item.getEmail());
        holder.cardStudentNumber.setText(item.getStudentNumber());
        holder.cardStatus.setText(item.getStatus());
        holder.cardStatus.setTextColor(Color.GREEN);

        holder.cardName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("USER_ID",item.getUserID());
                v.getContext().startActivity(intent);

//                Toast.makeText(v.getContext(), item.getUserID(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override protected void itemAdded(Users item, String key, int position) {
        Log.d("UserAdapter", "Added a new item to the adapter.");
    }

    @Override protected void itemChanged(Users oldItem, Users newItem, String key, int position) {
        Log.d("UserAdapter", "Changed an item.");
    }

    @Override protected void itemRemoved(Users item, String key, int position) {
        Log.d("UserAdapter", "Removed an item from the adapter.");
    }

    @Override protected void itemMoved(Users item, String key, int oldPosition, int newPosition) {
        Log.d("UserAdapter", "Moved an item.");
    }

}