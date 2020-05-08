package com.deepanshu.whatsappdemo.activity;

import androidx.annotation.NonNull;
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

import com.deepanshu.whatsappdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText userEmail,UserPassword;
    private TextView AlreadyHaveAnAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference Rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mAuth=FirebaseAuth.getInstance();
        Rootref= FirebaseDatabase.getInstance().getReference();
        intializedFiled();
        AlreadyHaveAnAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserTOLoginActivity();
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {
        String email=userEmail.getText().toString();
        String password=UserPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter email ",Toast.LENGTH_LONG).show();;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter password ",Toast.LENGTH_LONG).show();;
        }
        else{
            loadingBar.setTitle("creating new Acccount");
            loadingBar.setMessage("please wait,while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //SendUserTOLoginActivity();
                                String currentUserId=mAuth.getCurrentUser().getUid();
                                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                    Rootref.child(currentUserId).child("device_Token")
                                            .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sendtoMainActivity();
                                            Toast.makeText(RegisterActivity.this,"Account created successful",Toast.LENGTH_LONG).show();
                                            loadingBar.dismiss();

                                        }
                                    });

                                }
                                
                            else{
                                String msg=task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Something went wrong :"+msg,Toast.LENGTH_LONG);
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendtoMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
       mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void SendUserTOLoginActivity() {
        Intent loginIntent=new Intent(RegisterActivity.this,Login_Activity.class);
        startActivity(loginIntent);
        finish();

    }

    private void intializedFiled() {
        createAccountButton=(Button)findViewById(R.id.create_account);
        userEmail=(EditText)findViewById(R.id.signUp_email);
        UserPassword=(EditText)findViewById(R.id.signUp_password);
        AlreadyHaveAnAccountLink=(TextView)findViewById(R.id.Already_have_account);
        loadingBar=new ProgressDialog(this);

    }


}
