package com.deepanshu.whatsappdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.deepanshu.whatsappdemo.extraUtil.ColoredSnackbar;
import com.deepanshu.whatsappdemo.extraUtil.ConnectivityReceiver;
import com.deepanshu.whatsappdemo.interfaces.BaseInterface;
import com.deepanshu.whatsappdemo.model.FindFriends;
import com.deepanshu.whatsappdemo.extraUtil.Myapplication;
import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.extraUtil.SharedPreferencesFactory;
import com.deepanshu.whatsappdemo.adapter.TabsAccessorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.grpc.internal.SharedResourceHolder;
import pl.droidsonroids.gif.GifImageView;

import static com.deepanshu.whatsappdemo.activity.SettingActivity.CHK_STATUS;

public class MainActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, BaseInterface {
    public static final String ONLINE_STATUS = null;
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    String currentUserId;
    ProgressBar progressBar;
    private Button btnCheck;
    android.app.AlertDialog.Builder alertDialogBuilder;
    android.app.AlertDialog alertDialog;
    GifImageView NoInternetGif;
    LinearLayout linearLayout;
    SearchView searchView;
    MenuItem myActionMenuItem;
    SharedPreferencesFactory sharedPreferencesFactory = null;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MYCHAT");
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);
        myViewPager.setCurrentItem(1);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        //myTabLayout.getChildAt(1).getLayoutParams().width = 220;
        myTabLayout.getTabAt(1).getOrCreateBadge().setNumber(2);
        myTabLayout.getTabAt(1).getOrCreateBadge().setBadgeTextColor(Color.BLACK);
        myTabLayout.getTabAt(1).getOrCreateBadge().setBackgroundColor(Color.WHITE);
        //myTabLayout.getTabAt(0).setIcon(R.drawable.ic_camera_alt_black_24dp);


        NoInternetGif = findViewById(R.id.NoInternetGif);
        progressBar = findViewById(R.id.spin_kit);
        //sharedPreferencesFactory= SharedPreferencesFactory.getInstance(this);
        //SharedPreferences sharedPreferences=sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);
        //checkConnection();
        settheme();


    }

    private void settheme() {
        sharedPreferencesFactory = SharedPreferencesFactory.getInstance(MainActivity.this);
        prefs = sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);

        if (sharedPreferencesFactory.getPreferenceValue(CHK_STATUS) != null) {
            if (sharedPreferencesFactory.getPreferenceValue(CHK_STATUS).equalsIgnoreCase("TRUE")) {
                startnightmode();

            } else {
                stardaymode();
            }
        }
    }

    private void stardaymode() {
        //sharedPreferencesFactory.writePreferenceValue(CHK_STATUS, "FALSE");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    private void startnightmode() {
        //sharedPreferencesFactory.writePreferenceValue(CHK_STATUS, "TRUE");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

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
            //sharedPreferencesFactory.writePreferenceValue(ONLINE_STATUS,"online");
            Update_UserStatus("online");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            //sharedPreferencesFactory.writePreferenceValue(ONLINE_STATUS,"offline");
            //Update_UserStatus("offline");


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBar.setVisibility(View.GONE);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // sharedPreferencesFactory.writePreferenceValue(ONLINE_STATUS,"offline");
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
        myActionMenuItem = menu.findItem(R.id.action_search);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_search) {
            searchView = (SearchView) myActionMenuItem.getActionView();
            searchAction();
        }
        if (item.getItemId() == R.id.main_logout_options) {
            logoutDialog();
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

    private void logoutDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.custom_logout_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button btnCancel = promptView.findViewById(R.id.cancelBtn);
        Button btnlogout = promptView.findViewById(R.id.logout_button);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    //sharedPreferencesFactory.writePreferenceValue(ONLINE_STATUS,"offline");
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

    private void searchAction() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
    }


    private void RequestNewGroup() {
        final Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_group_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button turnOn = dialog.findViewById(R.id.btnCreate);
        Button deny = dialog.findViewById(R.id.btnCancel);
        turnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (ConnectivityUtils.isConnected(DeviceListActivity.this)) {
                dialog.dismiss();
                final EditText editText = dialog.findViewById(R.id.edtGrpName);
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

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        CheckConnectivity( isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        Myapplication.getInstance().setConnectivityListener(this);
    }

    // Showing the status in Snackbar
    @Override
    public void CheckConnectivity(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            NoInternetGif.setVisibility(View.GONE);
            myViewPager.setVisibility(View.VISIBLE);
            ColoredSnackbar.confirm(Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)).show();

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            ColoredSnackbar.alert(Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)).show();
            alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("Internet Alert")
                    .setMessage("Internet connection is lost ! please check connection..")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myViewPager.setVisibility(View.GONE);
                            NoInternetGif.setVisibility(View.VISIBLE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //checkConnection();
                            finish();
                        }

                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.green));
            buttonPositive.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            Button buttonNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.red));
            buttonNegative.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CheckConnectivity(isConnected);
    }

    @Override
    public void showdialog() {

    }

    @Override
    public void hideDialog() {

    }


}
