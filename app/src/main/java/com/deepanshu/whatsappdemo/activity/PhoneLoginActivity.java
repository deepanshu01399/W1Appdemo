package com.deepanshu.whatsappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.databaseHelper.CommanDataHolder;
import com.deepanshu.whatsappdemo.databaseHelper.DbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.CommonDataSource;

import static com.deepanshu.whatsappdemo.databaseHelper.DButil.getSelectedSpinnerItemObj;
import static com.deepanshu.whatsappdemo.databaseHelper.DButil.getSpinnerList;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.COLUMN_KEY;
import static com.deepanshu.whatsappdemo.databaseHelper.DbTables.SALUTATION;

public class PhoneLoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener {
    private Button SendVerificationCodeButton,VerifyButton;
    private EditText InputPhoneNumber,InputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Spinner spinphoneType;
    private TextView textPhoneType;
    private List<CommanDataHolder> commanDataHolderList = new ArrayList<>();
    private CommanDataHolder commanDataHolder,commanDataHolder1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_login_activity);
        mAuth=FirebaseAuth.getInstance();
        commanDataHolder= new CommanDataHolder();
        commanDataHolder.setKey("male");
        commanDataHolder.setValue("Male");
        commanDataHolderList.add(commanDataHolder);
        commanDataHolder1= new CommanDataHolder();
        commanDataHolder1.setKey("female");
        commanDataHolder1.setValue("Female");
        commanDataHolderList.add(commanDataHolder1);

        DbHelper.insertAll(getApplicationContext(), SALUTATION, commanDataHolderList);


        Initialization();
        setOnClickListner();
        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommanDataHolder moduelName = getSelectedSpinnerItem(SALUTATION, "male");
                Toast.makeText(PhoneLoginActivity.this, "gender: + "+moduelName.getValue(), Toast.LENGTH_SHORT).show();

                String phoneNumber=InputPhoneNumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this,"phone number is requried",Toast.LENGTH_LONG).show();
                }
                else{
                    loadingBar.setTitle("phone verification");
                    loadingBar.setMessage("Please wait we are creating your account");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,// Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }

            }
        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                String verificationcode=InputVerificationCode.getText().toString();
                if(TextUtils.isEmpty(verificationcode)){
                    Toast.makeText(PhoneLoginActivity.this,"please write verification code ",Toast.LENGTH_LONG).show();
                }
                else{
                    loadingBar.setTitle("phone verification");
                    loadingBar.setMessage("Please wait, while we are verifing verification code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });

        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this,"Invalid Phone Number,Please enter correct phone number..! ",Toast.LENGTH_LONG).show();
                SendVerificationCodeButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.INVISIBLE);
                InputVerificationCode.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("code send", "onCodeSent:" + verificationId);
                loadingBar.dismiss();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this,"Code has been sent please check and fill into this",Toast.LENGTH_LONG).show();
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);

            }
        };
    }

    private void setOnClickListner() {
        textPhoneType.setOnTouchListener(this);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInWithCredential", "signInWithCredential:success");
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this,"Congratulation ,you're logged in successfully..",Toast.LENGTH_LONG).show();
                            sendUserTOMainActivity();
                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w("signInWithCredential", "signInWithCredential:failure", task.getException());
                            //if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            String message=task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this,"Error : "+message,Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

    private void sendUserTOMainActivity() {
        Intent mainIntent=new Intent(PhoneLoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }


    private void Initialization() {
        SendVerificationCodeButton=(Button)findViewById(R.id.send_verification_code_button);
        VerifyButton=(Button)findViewById(R.id.verify_button);
        InputPhoneNumber=(EditText)findViewById(R.id.phone_number_input);
        InputVerificationCode=(EditText)findViewById(R.id.verification_code_input);
        loadingBar=new ProgressDialog(this);
        textPhoneType = findViewById(R.id.textPhoneType);
        spinphoneType = findViewById(R.id.spinPhoneType);

        spinphoneType.setOnItemSelectedListener(this);
        //setupSpinner();


    }

    private void setupSpinner() {
        List<CommanDataHolder> phoneTypeList = getSpinnerDataList(SALUTATION);
        //setSpinner(spinphoneType,textPhoneType,phoneTypeList);
    }

    private void setSpinner(Spinner spinphoneType, TextView textPhoneType, List<CommanDataHolder> phoneTypeList) {
        //spinphoneType.
    }

    public CommanDataHolder getSelectedSpinnerItem(String TABLE_NAME, String key) {
        Cursor cursor = DbHelper.fetchRow(getApplicationContext(), TABLE_NAME, null, COLUMN_KEY + "=?",
        new String[]{key}, null);
        CommanDataHolder itemObj = getSelectedSpinnerItemObj(cursor);
        return itemObj;
    }

    public List<CommanDataHolder> getSpinnerDataList(String TABLE_NAME) {
        Cursor cursor = DbHelper.fetchRow(getApplicationContext(), TABLE_NAME, null, null, null, null);
        List<CommanDataHolder> list = getSpinnerList(cursor);
        return list;
    }

    @Override
    public void onClick(View v) {
           }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textPhoneType.setText(((CommanDataHolder)spinphoneType.getSelectedItem()).getValue());
        textPhoneType.setTag(((CommanDataHolder) spinphoneType.getSelectedItem()).getKey());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.textPhoneType:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //showDatePickerDialog(mEdtDOB);
                        break;
                    case MotionEvent.ACTION_UP:
                        spinphoneType.performClick();
                        break;
                }
                break;
        }

        return false;
    }
}
