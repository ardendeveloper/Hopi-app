package com.dev.hopi_app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hopi_app.LoginActivity;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.dev.hopi_app.Adapter.UserAdapter;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    final String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvStudentNumber;
    private TextView tvCourse;
    private ImageView tvImage;
    Button btnSearch;
    Query mQuery;
    UserAdapter mMyAdapter;
    ArrayList<Users> mAdapterItems = null;
    ArrayList<String> mAdapterKeys;
    SharedPreferences sharedPref;
    Firebase myFirebaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_users);
        navigationView.setNavigationItemSelectedListener(this);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com/users");
        mQuery = new Firebase("https://hopiiapp.firebaseio.com/users");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_users);
        mMyAdapter = new UserAdapter(mQuery, Users.class, mAdapterItems, mAdapterKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mMyAdapter);

        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.txtSearch);
                String name = input.getText().toString();
                mQuery = new Firebase("https://hopiiapp.firebaseio.com/users");
                System.out.println(name);
                Query queryRef = mQuery.orderByChild("firstName").equalTo(name);
                queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println("Value: "+snapshot.getKey());
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            Users user = postSnapshot.getValue(Users.class);
                            System.out.println(user.getPushID());
                            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                            intent.putExtra("PUSH_ID",user.getPushID());
                            startActivity(intent);
                        }

                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_users);
                        mMyAdapter = new UserAdapter(mQuery, Users.class, mAdapterItems, mAdapterKeys);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        recyclerView.setAdapter(mMyAdapter);
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                        Toast.makeText(UsersActivity.this, "No Record Found! Please take not that first name is case sensitive.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Put Data on navigation Drawer Header
        View header = navigationView.getHeaderView(0);
        tvEmail = (TextView) header.findViewById(R.id.sideEmail);
        tvName = (TextView) header.findViewById(R.id.sideName);
        tvStudentNumber = (TextView) header.findViewById(R.id.sideStudentNumber);
        tvCourse = (TextView) header.findViewById(R.id.sideCourse);
        tvImage = (ImageView) header.findViewById(R.id.sideImage);

        tvEmail.setText(sharedPref.getString("email",""));
        tvName.setText(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
        tvStudentNumber.setText(sharedPref.getString("studentNumber",""));
        tvCourse.setText(sharedPref.getString("course","") + " - " + sharedPref.getString("year",""));

        if (sharedPref.getString("profileImage", "").equals("wew")) {
            tvImage.setImageResource(R.drawable.avatar);
        } else {
            tvImage.setImageBitmap(decodeBase64(sharedPref.getString("profileImage", "")));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_friend_request) {
            Intent intent = new Intent(this, FriendRequestActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_users) {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Firebase ref = new Firebase("https://hopiiapp.firebaseio.com/");
            ref.unauth();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Saving the list of items and keys of the items on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(SAVED_ADAPTER_ITEMS, Parcels.wrap(mMyAdapter.getItems()));
        outState.putStringArrayList(SAVED_ADAPTER_KEYS, mMyAdapter.getKeys());
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");
       // Toast.makeText(UsersActivity.this, "Destroy!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(){
        super.onStart();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("online");
        //Toast.makeText(UsersActivity.this, "Start!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");
//        Toast.makeText(UsersActivity.this, "Pause!!", Toast.LENGTH_SHORT).show();
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
