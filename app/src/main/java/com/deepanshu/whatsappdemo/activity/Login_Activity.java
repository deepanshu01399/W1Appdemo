package com.deepanshu.whatsappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deepanshu.whatsappdemo.extraUtil.ColoredSnackbar;
import com.deepanshu.whatsappdemo.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login_Activity extends AppCompatActivity {
    //private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private Button LoginButton,PhoneLoginButton,See_login_details;

    private EditText UserEmail,UserPassword;
    private ProgressDialog loadingBar;
    private TextView NeedNewAccountLink,ForgetPasswordLink;
    private DatabaseReference userRef;
    com.facebook.login.widget.LoginButton loginButton;
    CallbackManager callbackManager;

    // private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        //currentUser=mAuth.getCurrentUser();
        initializedField();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowusertoLogin();
            }
        });
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();

            }
        });
        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUsertoPhoneLoginActivity();
            }
        });

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //loginResult.getAccessToken();
                //loginResult.getRecentlyDeniedPermissions()
                //loginResult.getRecentlyGrantedPermissions()
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                Log.d("API123", loggedIn + " ??");
                sendUserToMainActivity();


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException error) {

            }


        });
        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;

        if (!loggedOut) {
            //Picasso.with(this).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
            if(Profile.getCurrentProfile()!=null) {
                String profilenam = Profile.getCurrentProfile().getFirstName();

                Log.d("TAG", "Username is: " + Profile.getCurrentProfile().getName());
            }
            //Using Graph API
            getUserProfile(AccessToken.getCurrentAccessToken());
        }
        /*See_login_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.getCurrentAccessToken()!=null)
                    getUserProfile(AccessToken.getCurrentAccessToken());

            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            //String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            Toast.makeText(Login_Activity.this, "Name: "+first_name + last_name, Toast.LENGTH_LONG).show();
                            //txtUsername.setText("First Name: " + first_name + "\nLast Name: " + last_name);
                            //txtEmail.setText(email);
                            //Picasso.with(MainActivity.this).load(image_url).into(imageView);
                            //RequestOptions requestOptions = new RequestOptions();
                            //requestOptions.dontAnimate();
                            //Glide.with(Login_Activity.this).load(image_url).into(circleImageView);
                            //sendUserToMainActivity();
                            //Intent mainIntent=new Intent(Login_Activity.this,MainActivity.class);
                            //mainIntent.putExtra("first_name",first_name);
                            //mainIntent.putExtra("last_name",last_name);
                            //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //startActivity(mainIntent);
                            //finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }




    private void AllowusertoLogin() {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter email ",Toast.LENGTH_LONG).show();;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter password ",Toast.LENGTH_LONG).show();;
        }
        else{
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("please wait,we are comferming your's details..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String currentUserId=mAuth.getCurrentUser().getUid();
                        String deviceToken= FirebaseInstanceId.getInstance().getToken();
                        userRef.child(currentUserId).child("device_Token")
                                .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendUserToMainActivity();
                                ColoredSnackbar.info(Snackbar.make(findViewById(android.R.id.content), "Login Successfull", Snackbar.LENGTH_LONG)).show();
                               // Toast.makeText(Login_Activity.this, "login successfull", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                            }
                        });

                    } else{
                        String message=task.getException().toString();
                        ColoredSnackbar.alert(Snackbar.make(findViewById(android.R.id.content), "Error : "+message, Snackbar.LENGTH_LONG)).show();
//                        Toast.makeText(Login_Activity.this,"Error"+message,Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();

                    }
                }


            });
        }
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent=new Intent(Login_Activity.this,RegisterActivity.class);
        startActivity(registerIntent);
        finish();

    }

    /*@Override
    protected void onStart() {//when our app start check login or not
        super.onStart();
        if(currentUser !=null){
            sendUserToMainActivity();
        }
    }*/



    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(Login_Activity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
    private void SendUsertoPhoneLoginActivity() {
        Intent phoneLoginIntent=new Intent(Login_Activity.this,PhoneLoginActivity.class);
        //phoneLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(phoneLoginIntent);
        //finish();

    }


    private void initializedField() {
        LoginButton=(Button)findViewById(R.id.login_button);
        PhoneLoginButton=(Button)findViewById(R.id.Phone_login_button);
        UserEmail=(EditText)findViewById(R.id.login_email);
        UserPassword=(EditText)findViewById(R.id.login_password);
        NeedNewAccountLink=(TextView)findViewById(R.id.NeedTOcreateAccount);
        ForgetPasswordLink=(TextView)findViewById(R.id.forgetPassword);
        loginButton=findViewById(R.id.fb_login_button);
       // See_login_details=(Button)findViewById(R.id.See_login_details);
        loadingBar=new ProgressDialog(this);

    }

}
