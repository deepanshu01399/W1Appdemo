package com.deepanshu.whatsappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.base.MoreObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    String currentUserId;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WECHAT");
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        progressBar = findViewById(R.id.spin_kit);



    }

    @Override
    protected void onStart() {
        super.onStart();
//when our app start check login or not
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // currentUserId=mAuth.getCurrentUser().getUid();//to get the current user id
        Rootref = FirebaseDatabase.getInstance().getReference();

        if (currentUser == null) {
            progressBar.setVisibility(View.VISIBLE);
            sendUserTOLoginActivity();
        } else {
            verifUserExistence();//verify usrname exixts
            Update_UserStatus("online");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Update_UserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBar.setVisibility(View.GONE);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Update_UserStatus("offline");
        }
    }

    private void verifUserExistence() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = mAuth.getCurrentUser().getUid();
        Rootref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("name").exists()) {//check for user name;
                    progressBar.setVisibility(View.GONE);
                    //  Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                } else {
                    sendUserTOSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserTOLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, Login_Activity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_options) {
                   /* dialog = new Dialog(ConsumerVerificationActivity.this);
        dialog.setContentView(R.layout.custom_bluethooth_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button btnCancel = dialog.findViewById(R.id.btnDeny);
        Button btnTurnOn=dialog.findViewById(R.id.btnTurnOn);
        btnTurnOn.setVisibility(View.GONE);
        RelativeLayout relativeLayout=dialog.findViewById(R.id.linearbtn);
        ((RelativeLayout) relativeLayout).setGravity(Gravity.CENTER);
        TextView txtMsg = dialog.findViewById(R.id.txtbluethooth_Msg);
        txtMsg.setText("User Pending Action");
        //txtMsg.setText(transactionSummaryResponse.getMessage().getErrorMessage());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
       */
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.custom_bluethooth_dialog, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Button btnCancel = promptView.findViewById(R.id.cancelBtn);
            Button btnlogout=promptView.findViewById(R.id.logout_button);
            btnlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        Update_UserStatus("offline");
                    }
                    mAuth.signOut();
                    sendUserTOLoginActivity();
                    finish();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setView(promptView);
            alertDialog.show();

             }

        if (item.getItemId() == R.id.main_createGroup) {
            RequestNewGroup();

        }
        if (item.getItemId() == R.id.main_settings_options) {
            sendUserTOSettingActivity();
        }
        if (item.getItemId() == R.id.main_find_Friends_option) {
            sendUserToFindFriendActivity();

        }
        return true;
    }


    private void RequestNewGroup() {
        final Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.create_group_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button turnOn = dialog.findViewById(R.id.btnCreate);
        Button deny = dialog.findViewById(R.id.btnCancel);
        turnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (ConnectivityUtils.isConnected(DeviceListActivity.this)) {
                    dialog.dismiss();
                final EditText editText= dialog.findViewById(R.id.edtGrpName);
                String groupName = editText.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    ColoredSnackbar.alert(Snackbar.make(findViewById(android.R.id.content), "please write Group Name", Snackbar.LENGTH_LONG)).show();
                } else {
                    CreateNewGroup(groupName);
                }

            } //else {
                    //dialog.dismiss();
                    //ColoredSnackbar.alert(Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)).show();
                //}}
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Cancel
                dialog.dismiss();
            }
        });
        dialog.show();
       /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Avengers");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    ColoredSnackbar.alert(Snackbar.make(findViewById(android.R.id.content), "please write Group Name", Snackbar.LENGTH_LONG)).show();
                } else {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
*/
    }

    private void CreateNewGroup(final String groupName) {
        Rootref.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           // Toast.makeText(MainActivity.this, groupName + " group is Created successfully", Toast.LENGTH_LONG).show();
                            ColoredSnackbar.info(Snackbar.make(findViewById(android.R.id.content), "Group is Created Successfully", Snackbar.LENGTH_LONG)).show();
                        }
                    }
                });
    }

    private void sendUserTOSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
        //settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        //finish();
    }

    private void sendUserToFindFriendActivity() {
        Intent findFriendIntent = new Intent(MainActivity.this, FindFriends.class);
        startActivity(findFriendIntent);
    }

    private void Update_UserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserId = mAuth.getCurrentUser().getUid();
        //now we make another node userstate and put value form the hashmap
        Rootref.child("Users").child(currentUserId).child("userState").updateChildren(onlineStateMap);


    }


}
