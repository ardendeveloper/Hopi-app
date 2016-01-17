package com.dev.hopi_app.Admin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.hopi_app.R;
import com.dev.hopi_app.Users;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class AdminSignupActivity extends AppCompatActivity {
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFirstNameView;
    private EditText mLasttNameView;
    private EditText mStudentNumberView;
    private View mProgressView;
    private View mLoginFormView;
    private Firebase myFirebaseRef;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_signup);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLasttNameView = (EditText) findViewById(R.id.lastName);
        mStudentNumberView = (EditText) findViewById(R.id.studentNumber);

        Button mSignUpButton = (Button) findViewById(R.id.btnSignUp);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);

        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final EditText lastName = (EditText) findViewById(R.id.lastName);
        final EditText studentNumber = (EditText) findViewById(R.id.studentNumber);

        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com/users");

        // UPDATING DRAFT
//        Query queryRef = myFirebaseRef.orderByChild("firstName").equalTo("hdhdjdjdn");
//        queryRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                System.out.println("There are " + snapshot.getChildrenCount() + " User/s");
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    Users user = postSnapshot.getValue(Users.class);
//                    System.out.println(user.getStudentNumber() + " - " + user.getEmail() + " - " + user.getPassword() + " - " + user.getFirstName() + " - " + user.getLastName());
//                    email.setText(user.getEmail());
//                    password.setText(user.getPassword());
//                    firstName.setText(user.getFirstName());
//                    lastName.setText(user.getLastName());
//                    studentNumber.setText((user.getStudentNumber()));
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });

    }


    /**
     * Callback received when a permissions request has been completed.
     */


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView.setError(null);
        mLasttNameView.setError(null);
        mStudentNumberView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLasttNameView.getText().toString();
        String studentNumber = mStudentNumberView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;

        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //check first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        } else if (!isAlpha(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        //check last name
        if (TextUtils.isEmpty(lastName)) {
            mLasttNameView.setError(getString(R.string.error_field_required));
            focusView = mLasttNameView;
            cancel = true;
        } else if (!isAlpha(lastName)) {
            mLasttNameView.setError(getString(R.string.error_invalid_name));
            focusView = mLasttNameView;
            cancel = true;
        }

        //check student number
        if (TextUtils.isEmpty(studentNumber)) {
            mStudentNumberView.setError(getString(R.string.error_field_required));
            focusView = mStudentNumberView;
            cancel = true;
        } else if (!isNumber(studentNumber)) {
            mStudentNumberView.setError(getString(R.string.error_invalid_snumber));
            focusView = mStudentNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, firstName, lastName, studentNumber);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isAlpha(String name) {
        return name.matches("^[ A-z]+$");
    }

    private boolean isNumber(String number) {
        return number.matches("\\d{2}-\\d{6}");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstName;
        private final String mLastName;
        private final String mStudentNumber;
        private String postId;

        UserLoginTask(String email, String password, String firstName, String lastName, String studentNumber) {
            mEmail = email;
            mPassword = password;
            mFirstName = firstName;
            mLastName = lastName;
            mStudentNumber = studentNumber;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // CREATE USER
            myFirebaseRef.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    Firebase newPostRef = myFirebaseRef.push();
                    Users newUser = new Users(mEmail,mPassword,mFirstName,mLastName,mStudentNumber,result.get("uid").toString(),newPostRef.getKey(),"offline");
                    newPostRef.setValue(newUser);

                    String timeStamp = new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date());
                    Firebase auditRef = new Firebase("https://hopiiapp.firebaseio.com/audit-trail");
                    Firebase tempRef = auditRef.push();
                    tempRef.child("action").setValue("New user: "+mFirstName+" "+mLastName+" has been registered.");
                    tempRef.child("user").setValue(sharedPref.getString("firstName","") +" "+sharedPref.getString("lastName",""));
                    tempRef.child("timestamp").setValue(timeStamp);
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    // there was an error
                }
            });

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            //signUp Successful
            if (success) {
                Toast.makeText(AdminSignupActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

