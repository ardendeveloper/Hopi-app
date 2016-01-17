package com.dev.hopi_app.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hopi_app.Activity.ChatActivity;
import com.dev.hopi_app.Activity.EditInfoActivity;
import com.dev.hopi_app.Activity.FriendRequestActivity;
import com.dev.hopi_app.Activity.FriendsActivity;
import com.dev.hopi_app.Activity.MapsActivity;
import com.dev.hopi_app.Activity.UsersActivity;
import com.dev.hopi_app.Friends;
import com.dev.hopi_app.LoginActivity;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class AdminProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Firebase myFirebaseRef;
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvStudentNumber;
    SharedPreferences sharedPref;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        final TextView cName = (TextView) findViewById(R.id.content_name);
        final TextView cEmail = (TextView) findViewById(R.id.content_email);
        final TextView cStudentNumber = (TextView) findViewById(R.id.content_studentNumber);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.item_account);
        navigationView.setNavigationItemSelectedListener(this);

        Intent text = getIntent();
        final Bundle b = text.getExtras();
        if(b!=null) {
            final String userId =(String) b.get("USER_ID");
            final Firebase ref = new Firebase("https://hopiiapp.firebaseio.com/users");
            Query queryRef = ref.orderByChild("userID").equalTo(userId);
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println("There are " + snapshot.getChildrenCount() + " User/s");
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        final Users user = postSnapshot.getValue(Users.class);
                        System.out.println(user.getStudentNumber() + " - " + user.getEmail() + " - " + user.getPassword() + " - " + user.getFirstName() + " - " + user.getLastName());
                        setTitle(user.getFirstName()+" "+user.getLastName());

                        cEmail.setText(user.getEmail());
                        cName.setText(user.getFirstName()+" "+user.getLastName());
                        cStudentNumber.setText(user.getStudentNumber());

                        fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.edit));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(),AdminEditInfoActivity.class);
                                intent.putExtra("USER_ID",user.getUserID());
                                startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });



        } else {
            setTitle(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
//            fab.setImageDrawable(getResources().getDrawable(R.drawable.edit));
//            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit));
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.edit));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),EditInfoActivity.class);
                    startActivity(intent);
                }
            });

            cEmail.setText(sharedPref.getString("email",""));
            cName.setText(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
            cStudentNumber.setText(sharedPref.getString("studentNumber",""));

        }


        //Put Data on navigation Drawer Header
        View header = navigationView.getHeaderView(0);
        tvEmail = (TextView) header.findViewById(R.id.sideEmail);
        tvName = (TextView) header.findViewById(R.id.sideName);
        tvStudentNumber = (TextView) header.findViewById(R.id.sideStudentNumber);

        tvEmail.setText(sharedPref.getString("email",""));
        tvName.setText(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
        tvStudentNumber.setText(sharedPref.getString("studentNumber",""));
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
    
    public void isAlreadyFriends(String userId){
        final Firebase postRef = myFirebaseRef.child("friends/"+sharedPref.getString("userID",""));
        Query queryRef = postRef.orderByChild("userID").equalTo(userId);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.check));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(AdminProfileActivity.this, "You are already friends!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void requestPending(String userId){
        final Firebase postRef = myFirebaseRef.child("friend-requests/"+sharedPref.getString("userID","")+'/'+userId);
        Query queryRef = postRef.orderByChild("userID").equalTo(userId);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
//                    fab.setImageDrawable(getResources().getDrawable(R.drawable.check));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(AdminProfileActivity.this, "Friend request is still pending.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }


}
