package com.example.kenne.box_a_lot.dialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kenne.box_a_lot.BlurDialogFragment.BlurDialogFragment;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.RegisterActivity;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
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
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CustomBlurDialogFragment extends BlurDialogFragment {

    private static final String TAG = "LoginActivity";
    private static final int DIALOG_REQUEST_CODE = 10;
    protected boolean exitOnBackPressed = false;
    protected RelativeLayout mainView;
    protected boolean authenticating = false;

    protected EditText usernameEditText;
    protected EditText passwordEditText;

    private DialogInterface.OnDismissListener onDismissListener;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();

        mSignInClient = GoogleSignIn.getClient(getActivity(), options);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                if(mAuth.getCurrentUser() == null)
                    loginFailed();
                dismiss();

            }
        };
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_login, container,
                false);

        initViews(v);

        return v;
    }

    protected void initViews(View v) {
        btnLogin = v.findViewById(R.id.login_Btn);
        btnReg = v.findViewById(R.id.login_register_Btn);
        btnFacebook = v.findViewById(R.id.login_facebook_Btn);
        btnGoogle = v.findViewById(R.id.login_google_Btn);
        usernameEditText = v.findViewById(R.id.login_Email_ET);
        passwordEditText = v.findViewById(R.id.login_Password_ET);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInEmail();
            }
        });
        btnReg .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.setFragment(this);
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
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
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
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());


                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("TAG", "signInWithEmail", task.getException());
                                //Toast.makeText(LoginActivity.this, "Authentication failed.",
                                //        Toast.LENGTH_SHORT).show();
                            } else {
                                loginSuccedded();
                            }
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            //Toast.makeText(LoginActivity.this, "Authentication failed.",
                             //       Toast.LENGTH_SHORT).show();
                        } else {
                            loginSuccedded();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            loginSuccedded();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    void loginSuccedded(){
        //((UpdateAble)getParentFragment()).onResumeFragment();
        dismiss();
    }

    public void loginFailed(){
        ((UiUpdateInterface)getActivity()).loginFailed();
        dismiss();
    }
}
