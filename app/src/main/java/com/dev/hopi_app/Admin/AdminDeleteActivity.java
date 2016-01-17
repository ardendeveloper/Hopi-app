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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hopi_app.Activity.ChatActivity;
import com.dev.hopi_app.Activity.ProfileActivity;
import com.dev.hopi_app.Activity.UsersActivity;
import com.dev.hopi_app.Adapter.AdminUserDeleteAdapter;
import com.dev.hopi_app.Adapter.UserAdapter;
import com.dev.hopi_app.Admin.AdminSignupActivity;
import com.dev.hopi_app.Admin.AdminUsersActivity;
import com.dev.hopi_app.LoginActivity;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminDeleteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    final String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    final String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";

    private TextView tvEmail;
    private TextView tvName;
    private TextView tvStudentNumber;

    Query mQuery;
    AdminUserDeleteAdapter mMyAdapter;
    ArrayList<Users> mAdapterItems = null;
    ArrayList<String> mAdapterKeys;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_users);
        navigationView.setNavigationItemSelectedListener(this);

        Firebase.setAndroidContext(this);
        mQuery = new Firebase("https://hopiiapp.firebaseio.com/users");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_users);
        mMyAdapter = new AdminUserDeleteAdapter(mQuery, Users.class, mAdapterItems, mAdapterKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mMyAdapter);

        final SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        //Put Data on navigation Drawer Header
        View header = navigationView.getHeaderView(0);
        tvEmail = (TextView) header.findViewById(R.id.sideEmail);
        tvName = (TextView) header.findViewById(R.id.sideName);
        tvStudentNumber = (TextView) header.findViewById(R.id.sideStudentNumber);

        tvEmail.setText(sharedPref.getString("email",""));
        tvName.setText(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
        tvStudentNumber.setText(sharedPref.getString("studentNumber",""));

        Intent text = getIntent();
        final Bundle b = text.getExtras();
        if(b!=null) {
            final String pushID =(String) b.get("PUSH_ID");
            Firebase deleteRef = new Firebase("https://hopiiapp.firebaseio.com/users/"+pushID);
            deleteRef.removeValue();

            String timeStamp = new SimpleDateFormat("MMM dd yyyy - h.mm a").format(new Date());
            Firebase auditRef = new Firebase("https://hopiiapp.firebaseio.com/audit-trail");
            Firebase tempRef = auditRef.push();
            tempRef.child("action").setValue("Deleted: User with push ID "+pushID+" has been removed.");
            tempRef.child("user").setValue(sharedPref.getString("firstName","")+" "+sharedPref.getString("lastName",""));
            tempRef.child("timestamp").setValue(timeStamp);

            Toast.makeText(AdminDeleteActivity.this, "User has been successfully deleted.", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
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

    // Saving the list of items and keys of the items on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(SAVED_ADAPTER_ITEMS, Parcels.wrap(mMyAdapter.getItems()));
        outState.putStringArrayList(SAVED_ADAPTER_KEYS, mMyAdapter.getKeys());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyAdapter.destroy();
    }

}
