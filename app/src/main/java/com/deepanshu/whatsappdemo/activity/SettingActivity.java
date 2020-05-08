
package com.deepanshu.whatsappdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.extraUtil.SharedPreferencesFactory;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Button UpdateAccountSettings;
    private EditText userName,userstatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final int GalleryPick=1;
    private StorageReference UserProfileImageRef;
    ProgressDialog loadingBar;
    private Toolbar settingToolBar;
    private String Cheker="",myUri=" ";
    private  final int requestcodeforDp=43;
    private StorageTask uploadTask;
    private Switch switchbtn;
    SharedPreferencesFactory sharedPreferencesFactory;
    public static final String CHK_STATUS ="STATUS" ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef =FirebaseStorage.getInstance().getReference().child("Profile Images");
        sharedPreferencesFactory= SharedPreferencesFactory.getInstance(this);
        SharedPreferences sharedPreferences=sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);
        switchbtn=findViewById(R.id.switchTransaction);
        switchbtn.setOnCheckedChangeListener(this);
        setswitchstaus();
        Initialization();
        RetriveUserInfo();


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult (Intent.createChooser(
                        galleryIntent,
                        "Select Image from here...")
                         ,GalleryPick);
*/
                Cheker="image";
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//this will send the user to the galaery and user selec t the picute
                startActivityForResult(intent.createChooser(intent,"Select Image"),requestcodeforDp);


            }
        });
    }

    private void setswitchstaus() {
            if (sharedPreferencesFactory.getPreferenceValue(CHK_STATUS) != null && sharedPreferencesFactory.getPreferenceValue(CHK_STATUS).equalsIgnoreCase("TRUE")) {
                switchbtn.setChecked(true);
            } else
                switchbtn.setChecked(false);

    }

    private void RetriveUserInfo() {
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&&
                        (dataSnapshot.hasChild("image")))){
                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImgage=dataSnapshot.child("image").getValue().toString();
                    userName.setText(retrieveUserName);
                    userstatus.setText(retrieveStatus);
                    Picasso.get().load(retrieveProfileImgage).into(userProfileImage);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                    userName.setText(retrieveUserName);
                    userstatus.setText(retrieveStatus);

                }
                else{
                    Toast.makeText(SettingActivity.this,"please set & update your profile info",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setUserStatus = userstatus.getText().toString();
        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "please write your User name", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(setUserStatus)) {
            Toast.makeText(this, "please write your Status", Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            RootRef.child("Users").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile Update successfully", Toast.LENGTH_LONG).show();

                            } else {
                                String msg = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error" + msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        }
    }


    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(SettingActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void Initialization() {
        UpdateAccountSettings=(Button)findViewById(R.id.update_setting_button);
        userName=(EditText)findViewById(R.id.set_user_name);
        userstatus=(EditText)findViewById(R.id.set_Profile_status);
        userProfileImage=(CircleImageView)findViewById(R.id.profile_image);
        loadingBar=new ProgressDialog(this);
        settingToolBar=(Toolbar)findViewById(R.id.setting_toolbar);
        setSupportActionBar(settingToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
        settingToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton();
            }
        });

    }


    private void backButton() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==requestcodeforDp && resultCode==RESULT_OK){
            Uri ImageUri=data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }



         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK) {
          CropImage.ActivityResult result = CropImage.getActivityResult(data);
        //if the result code is OK

            loadingBar.setTitle("Set Profile Image");
            loadingBar.setMessage("please wait,your profile is uploading");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Uri resultUri = result.getUri();
            //Uri resultUri = data.getData();

            final StorageReference filepath = UserProfileImageRef.child(currentUserId+"."+"jpg");
            uploadTask=filepath.putFile(resultUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return filepath.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        myUri=downloadUri.toString();
                        Toast.makeText(SettingActivity.this, "my Uri"+myUri +"...........................", Toast.LENGTH_SHORT).show();


                        RootRef.child("Users").child(currentUserId).child("image").setValue(myUri)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingActivity.this, "Image save in the database,Successfully", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }else{
                                            String errorMsg=task.getException().toString();
                                            Toast.makeText(SettingActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                    }
                }
            });

            /*    filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata()!=null) {
                            final String  downLoadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            Toast.makeText(SettingActivity.this, "444"+downLoadUri, Toast.LENGTH_SHORT).show();
                        */

            /*filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingActivity.this, "Profile image loaded successfully", Toast.LENGTH_LONG).show();
                            final String  downLoadUri = task.getResult().getStorage().getDownloadUrl().toString();
                            Toast.makeText(SettingActivity.this, "444"+downLoadUri, Toast.LENGTH_SHORT).show();
                            RootRef.child("Users").child(currentUserId).child("image").setValue(downLoadUri)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingActivity.this, "Image save in the database,Successfully", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }else{
                                            String errorMsg=task.getException().toString();
                                            Toast.makeText(SettingActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                        }
                    }

                });*/

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            //Exception error = resultC.getError();
            Toast.makeText(SettingActivity.this," Something went wrong !..Error ",Toast.LENGTH_LONG).show();
            loadingBar.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked==true){
            //Toast.makeText(this, "checked", Toast.LENGTH_SHORT).show();
            startnightmode();


        }else {
            //Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show();
            stardaymode();
           }
    }

    private void stardaymode() {
        sharedPreferencesFactory.writePreferenceValue(CHK_STATUS, "FALSE");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
        //finish();

    }

    private void startnightmode() {
        sharedPreferencesFactory.writePreferenceValue(CHK_STATUS, "TRUE");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
        //finish();

    }
}