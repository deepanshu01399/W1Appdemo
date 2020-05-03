package com.deepanshu.whatsappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

public class Login_Activity extends AppCompatActivity {
    //private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private ProgressDialog loadingBar;
    private TextView NeedNewAccountLink,ForgetPasswordLink;
    private DatabaseReference userRef;
   // private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
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
        loadingBar=new ProgressDialog(this);

    }

}
