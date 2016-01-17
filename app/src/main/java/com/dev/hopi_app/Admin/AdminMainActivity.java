package com.dev.hopi_app.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hopi_app.Activity.ChatActivity;
import com.dev.hopi_app.Activity.ProfileActivity;
import com.dev.hopi_app.LoginActivity;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class AdminMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Firebase myFirebaseRef;
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvStudentNumber;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        final TextView cAll = (TextView) findViewById(R.id.counterAll);
        final TextView cOnline = (TextView) findViewById(R.id.counterOnline);
        final TextView cOffline = (TextView) findViewById(R.id.counterOffline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.admin_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Put Data on navigation Drawer Header
        View header = navigationView.getHeaderView(0);
        tvEmail = (TextView) header.findViewById(R.id.sideEmail);
        tvName = (TextView) header.findViewById(R.id.sideName);
        tvStudentNumber = (TextView) header.findViewById(R.id.sideStudentNumber);

        Intent intent = getIntent();
        String name = intent.getStringExtra(LoginActivity.extra_name);
        String email = intent.getStringExtra(LoginActivity.extra_email);
        String studentNumber = intent.getStringExtra(LoginActivity.extra_studentnumber);

        tvEmail.setText(email);
        tvName.setText(name);
        tvStudentNumber.setText(studentNumber);

        Firebase allUsers = myFirebaseRef.child("users");
        allUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cAll.setText(""+ snapshot.getChildrenCount());
                Toast.makeText(AdminMainActivity.this, "Total number of users has been updated!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Firebase allOnline = myFirebaseRef.child("online");
        allOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cOnline.setText(""+ snapshot.getChildrenCount());
                Toast.makeText(AdminMainActivity.this, "Number of online users has been updated!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Firebase allOffline = myFirebaseRef.child("offline");
        allOffline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cOffline.setText(""+ snapshot.getChildrenCount());
                Toast.makeText(AdminMainActivity.this, "Number of offline users has been updated!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users_view) {
            Intent intent = new Intent(this, AdminUsersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_users_add) {
            Intent intent = new Intent(this, AdminSignupActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_users_delete) {
            Intent intent = new Intent(this, AdminDeleteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_chatroom) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_audit_trail) {
            Intent intent = new Intent(this, AdminAuditTrailActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, AdminProfileActivity.class);
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

    public void goToAllUsers(View v){
        Intent intent = new Intent(getBaseContext(),AdminUsersActivity.class);
        startActivity(intent);
        Toast.makeText(AdminMainActivity.this, "All Users", Toast.LENGTH_SHORT).show();
    }

    public void goToOnlineUsers(View v){
        Intent intent = new Intent(getBaseContext(),AdminOnlineActivity.class);
        startActivity(intent);
        Toast.makeText(AdminMainActivity.this, "Online Users", Toast.LENGTH_SHORT).show();
    }

    public void goToOfflineUsers(View v){
        Intent intent = new Intent(getBaseContext(),AdminOfflineActivity.class);
        startActivity(intent);
        Toast.makeText(AdminMainActivity.this, "Offline Users", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");

        Firebase ref = new Firebase("https://hopiiapp.firebaseio.com/");
        Firebase offlineRef = ref.child("offline/"+sharedPref.getString("pushID",""));
        Users offlineUser = new Users(
                sharedPref.getString("email",""),
                sharedPref.getString("password",""),
                sharedPref.getString("firstName",""),
                sharedPref.getString("lastName",""),
                sharedPref.getString("studentNumber",""),
                sharedPref.getString("userID",""),
                sharedPref.getString("pushID",""),
                "Offline");
        offlineRef.setValue(offlineUser);

        Firebase onlineRef = ref.child("online/"+sharedPref.getString("pushID",""));
        onlineRef.removeValue();

//        Toast.makeText(MainActivity.this, "Destroy!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(){
        super.onStart();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("online");

        Firebase ref = new Firebase("https://hopiiapp.firebaseio.com/");
        Firebase onlineRef = ref.child("online/"+sharedPref.getString("pushID",""));
        Users onlineUser = new Users(
                sharedPref.getString("email",""),
                sharedPref.getString("password",""),
                sharedPref.getString("firstName",""),
                sharedPref.getString("lastName",""),
                sharedPref.getString("studentNumber",""),
                sharedPref.getString("userID",""),
                sharedPref.getString("pushID",""),
                "Online");
        onlineRef.setValue(onlineUser);

        Firebase offlineRef = ref.child("offline/"+sharedPref.getString("pushID",""));
        offlineRef.removeValue();
//        Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
    }
}
