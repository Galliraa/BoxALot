/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kenne.box_a_lot.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by itzik on 6/8/2014.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final String MESSAGES_CHILD = "Users/";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth auth;

    protected EditText usernameEditText;
    protected EditText passwordEditText;

    private CallbackManager mCallbackManager;

    private String email;
    private String password;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mSignInClient;


    // This is a list of extras that are passed to the login view
    protected HashMap<String, Object> extras = new HashMap<>();

    /** Passed to the context in the intent extras, Indicates that the context was called after the user press the logout button,
     * That means the context wont try to authenticate in inResume. */

    // Firebase instance variables
    private FirebaseAuth mAuth;

    protected Button btnLogin, btnReg, btnGoogle, btnResetPassword;
    private LoginButton btnFacebook;
    protected ImageView appIconImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();

        mSignInClient = GoogleSignIn.getClient(this, options);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //setContentView(activityLayout());
        setContentView(R.layout.activity_login);


        initViews();

    }

    protected void initViews() {
        btnLogin = findViewById(R.id.login_Btn);
        btnReg = findViewById(R.id.login_register_Btn);
        btnFacebook = findViewById(R.id.login_facebook_Btn);
        btnGoogle = findViewById(R.id.login_google_Btn);
        usernameEditText = findViewById(R.id.login_Email_ET);
        passwordEditText = findViewById(R.id.login_Password_ET);

        btnLogin.setOnClickListener(this);
        btnReg .setOnClickListener(this);
        btnGoogle.setOnClickListener(this);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
                setResult(RESULT_CANCELED);
                //---close the activity---
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
                setResult(RESULT_CANCELED);
                //---close the activity---
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_google_Btn:
                signInGoogle();
                break;
            case R.id.login_Btn:
                signInEmail();
                break;
            case R.id.login_register_Btn:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void signInGoogle() {
        // Launches the sign in flow, the result is returned in onActivityResult
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void signInEmail() {
        // Retrieves user inputs
        email = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        // trims the input
        email = email.trim();
        password = password.trim();
        if(email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            // Launches the sign in flow, the result is returned in onActivityResult
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());


                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("TAG", "signInWithEmail", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                                //---close the activity---
                                finish();
                            } else {
                                Intent data = new Intent();
                                //String text = "Result to be returned...."
                                //---set the data to pass back---
                                //data.setData(Uri.parse(text));
                                setResult(RESULT_OK, data);
                                //---close the activity---
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        setResult(RESULT_CANCELED);
                        //---close the activity---
                        finish();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent data = new Intent();
                            //String text = "Result to be returned...."
                            //---set the data to pass back---
                            //data.setData(Uri.parse(text));
                            setResult(RESULT_OK, data);
                            //---close the activity---
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                User user = new User();
                                user.setName(task.getResult().getUser().getDisplayName());
                                user.setPhoneNumber(task.getResult().getUser().getPhoneNumber());
                                mFirebaseDatabaseReference.child(MESSAGES_CHILD +  "/" + auth.getCurrentUser().getUid()).setValue(user.getUserMap());
                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            setResult(RESULT_OK);
                            //---close the activity---
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_CANCELED);
                            //---close the activity---
                            finish();

                        }

                        // ...
                    }
                });
    }


}
