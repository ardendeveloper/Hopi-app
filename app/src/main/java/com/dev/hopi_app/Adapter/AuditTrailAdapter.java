package com.dev.hopi_app.Adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.hopi_app.Activity.ProfileActivity;
import com.dev.hopi_app.AuditTrail;
import com.dev.hopi_app.Firebase.FirebaseRecyclerAdapter;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.Query;

import java.util.ArrayList;


public class AuditTrailAdapter extends FirebaseRecyclerAdapter<AuditTrailAdapter.ViewHolder, AuditTrail> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardAction;
        TextView cardUser;
        TextView cardDate;


        public ViewHolder(View view) {
            super(view);
            cardAction = (TextView) view.findViewById(R.id.cardAction);
            cardUser = (TextView) view.findViewById(R.id.cardUser);
            cardDate = (TextView) view.findViewById(R.id.cardDate);
        }
    }

    public AuditTrailAdapter(Query query, Class<AuditTrail> itemClass, @Nullable ArrayList<AuditTrail> items,
                             @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    @Override public AuditTrailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_audit_trail, parent, false);

        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(AuditTrailAdapter.ViewHolder holder, int position) {
        final AuditTrail item = getItem(position);
        holder.cardAction.setText(item.getAction());
        holder.cardUser.setText("By: " +item.getUser());
        holder.cardDate.setText("Date: " +item.getTimestamp());

//        holder.cardName.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
//                intent.putExtra("USER_ID",item.getUserID());
//                v.getContext().startActivity(intent);
//
////                Toast.makeText(v.getContext(), item.getUserID(), Toast.LENGTH_SHORT).show();
//
//            }
//        });


    }

    @Override protected void itemAdded(AuditTrail item, String key, int position) {
        Log.d("UserAdapter", "Added a new item to the adapter.");
    }

    @Override protected void itemChanged(AuditTrail oldItem, AuditTrail newItem, String key, int position) {
        Log.d("UserAdapter", "Changed an item.");
    }

    @Override protected void itemRemoved(AuditTrail item, String key, int position) {
        Log.d("UserAdapter", "Removed an item from the adapter.");
    }

    @Override protected void itemMoved(AuditTrail item, String key, int oldPosition, int newPosition) {
        Log.d("UserAdapter", "Moved an item.");
    }

}