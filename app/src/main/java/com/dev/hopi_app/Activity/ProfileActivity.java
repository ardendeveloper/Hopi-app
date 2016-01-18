package com.dev.hopi_app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.hopi_app.Friends;
import com.dev.hopi_app.LoginActivity;
import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Firebase myFirebaseRef;
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvStudentNumber;
    private ImageView mImg;
    private TextView tvCourse;
    private ImageView tvImage;
    SharedPreferences sharedPref;
    FloatingActionButton fab;
    String ActiveProfileName;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        final TextView cName = (TextView) findViewById(R.id.content_name);
        final TextView cEmail = (TextView) findViewById(R.id.content_email);
        final TextView cStudentNumber = (TextView) findViewById(R.id.content_studentNumber);
        mImg = (ImageView) findViewById(R.id.profile_image);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com/users");

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
        if (b != null) {
            final String userId = (String) b.get("USER_ID");
            final Firebase ref = new Firebase("https://hopiiapp.firebaseio.com/users");
            Query queryRef = ref.orderByChild("userID").equalTo(userId);
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        final Users user = postSnapshot.getValue(Users.class);
                        setTitle(user.getFirstName() + " " + user.getLastName());

                        cEmail.setText(user.getEmail());
                        cName.setText(user.getFirstName() + " " + user.getLastName());
                        cStudentNumber.setText(user.getStudentNumber());

                        ActiveProfileName = user.getFirstName() + " " + user.getLastName();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

            //change icon if already friends
            isAlreadyFriends(userId);
            requestPending(userId);

            //add friend
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    final Firebase postRef = myFirebaseRef.child("friends/"+sharedPref.getString("userID",""));
//                    Query queryRef = ref.orderByChild("userID").equalTo(userId);
//                    queryRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot snapshot) {
//                                System.out.println("There are " + snapshot.getChildrenCount() + " User/s");
//                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                    final Users user = postSnapshot.getValue(Users.class);
//                                    System.out.println(user.getStudentNumber() + " - " + user.getEmail() + " - " + user.getPassword() + " - " + user.getFirstName() + " - " + user.getLastName());
//                                    Users newUser = new Users(user.getEmail(),user.getPassword(),user.getFirstName(),user.getLastName(),user.getStudentNumber(),user.getUserID(),null);
//                                    postRef.push().setValue(newUser);
//                                    Toast.makeText(ProfileActivity.this, "You are now friends with "+user.getFirstName(), Toast.LENGTH_SHORT).show();
//                                }
//
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//                            System.out.println("The read failed: " + firebaseError.getMessage());
//                        }
//                    });
                    final Firebase postRef = myFirebaseRef.child("friend-requests/" + userId + "/" + sharedPref.getString("userID", ""));

                    Friends newUser = new Friends(
                            sharedPref.getString("email", ""),
                            sharedPref.getString("password", ""),
                            sharedPref.getString("firstName", ""),
                            sharedPref.getString("lastName", ""),
                            sharedPref.getString("studentNumber", ""),
                            sharedPref.getString("userID", ""),
                            sharedPref.getString("pushID", ""),
                            null);
                    postRef.setValue(newUser);

                    String timeStamp = new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date());
                    Firebase auditRef = new Firebase("https://hopiiapp.firebaseio.com/audit-trail");
                    Firebase tempRef = auditRef.push();
                    tempRef.child("action").setValue("New Friend Request: \n From: " + sharedPref.getString("firstName", "") + " " + sharedPref.getString("lastName", "") + "\n To: " + ActiveProfileName);
                    tempRef.child("user").setValue(sharedPref.getString("firstName", "") + " " + sharedPref.getString("lastName", ""));
                    tempRef.child("timestamp").setValue(timeStamp);

                    Toast.makeText(ProfileActivity.this, "Friend request has been sent!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            setTitle(sharedPref.getString("firstName", "") + " " + sharedPref.getString("lastName", ""));
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.edit));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditInfoActivity.class);
                    startActivity(intent);
                }
            });

            cEmail.setText(sharedPref.getString("email", ""));
            cName.setText(sharedPref.getString("firstName", "") + " " + sharedPref.getString("lastName", ""));
            cStudentNumber.setText(sharedPref.getString("studentNumber", ""));

        }

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
    @Override
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

    public void isAlreadyFriends(String userId) {
        final Firebase postRef = myFirebaseRef.child("friends/" + sharedPref.getString("userID", ""));
        Query queryRef = postRef.orderByChild("userID").equalTo(userId);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.check));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ProfileActivity.this, "You are already friends!", Toast.LENGTH_SHORT).show();
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

    public void requestPending(String userId) {
        final Firebase postRef = myFirebaseRef.child("friend-requests/" + sharedPref.getString("userID", "") + '/' + userId);
        Query queryRef = postRef.orderByChild("userID").equalTo(userId);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    fab.setImageDrawable(getResources().getDrawable(R.drawable.check));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ProfileActivity.this, "Friend request is still pending.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID", ""));
        tempRef.child("status").setValue("offline");
//        Toast.makeText(ProfileActivity.this, "Destroy!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID", ""));
        tempRef.child("status").setValue("online");
//        Toast.makeText(ProfileActivity.this, "Start!!", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onPause() {
        super.onPause();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID", ""));
        tempRef.child("status").setValue("offline");
//        Toast.makeText(ProfileActivity.this, "Pause!!", Toast.LENGTH_SHORT).show();
    }

    private static final int SELECT_PICTURE = 0;

    public void pickPhoto(View view) {
        //TODO: launch the photo picker
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bmp;
        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                Bitmap Bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                bmp.recycle();
                byte[] byteArray = bYtE.toByteArray();
                String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

                Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID", ""));
                tempRef.child("profileImage").setValue(imageFile);
                editor.putString("profileImage", imageFile);
                editor.apply();
                mImg.setImageBitmap(Bit);
            } catch (FileNotFoundException ex) {
            }

//            myFirebaseRef.child(sharedPref.getString("pushID", "")+"/profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    snapshot.getValue();
//                    mImg.setImageBitmap(decodeBase64(snapshot.getValue().toString()));
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//                }
//            });


        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
