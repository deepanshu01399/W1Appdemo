package com.deepanshu.whatsappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton sendMsgButton;
    private EditText userMessageInput;
    private TextView userName, userLastSeen;

    private GroupMessagesAdapter groupMessagesAdapter;

    private ArrayList<GroupMessages> groupMsgList=new ArrayList<>();;
    private RecyclerView groupMsgRecyclerView;
    private TextView displayTextMessages;
    private FirebaseAuth mAuth;

    private String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;
    private DatabaseReference userRef,GroupNameRef,GroupmessagekeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mToolbar=(Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_layout, null);
        actionBar.setCustomView(actionBarView);

        currentGroupName=getIntent().getExtras().get("groupName").toString();

       // Toast.makeText(GroupChatActivity.this,"current grp name :"+currentGroupName,Toast.LENGTH_LONG).show();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);




        /*messageReceiverName = getIntent().getExtras().get("User_name").toString();
        userProfileImage = getIntent().getExtras().get("user_profile_image").toString();
*/

        InitializedFields();
        userName.setText(currentGroupName);

        String members = null;//see in future
        userLastSeen.setText("members");


        groupMsgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupMsgRecyclerView.setAdapter(groupMessagesAdapter);

        GetUserInfo();

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMsgToDatabase();
                userMessageInput.setText("");
                //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupMessages groupMessages=dataSnapshot.getValue(GroupMessages.class);
                groupMsgList.add(groupMessages);
                groupMessagesAdapter.notifyDataSetChanged();

                groupMsgRecyclerView.smoothScrollToPosition(groupMsgRecyclerView.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void GetUserInfo() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists());
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMsgToDatabase() {
        String message=userMessageInput.getText().toString();
        String messageKey=GroupNameRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(this,"please write the message",Toast.LENGTH_LONG).show();
        }
        else{
            Calendar calforDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd,yyyy");
            currentDate=currentDateFormat.format(calforDate.getTime());

            Calendar calforTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calforTime.getTime());

            HashMap<String ,Object> groupMessageKey =new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupmessagekeyRef=GroupNameRef.child(messageKey);
            HashMap<String,Object> messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            GroupmessagekeyRef.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(GroupChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(GroupChatActivity.this, "Message is not sent", Toast.LENGTH_SHORT).show();
                }

            });

        }

    }
    /*
    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessages=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();
            displayTextMessages.append(chatName+":\n"+chatMessages+"\n"+chatTime+" "+chatDate+"\n\n");
            //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }*/

    private void InitializedFields() {

        getSupportActionBar().setTitle(currentGroupName);
        sendMsgButton=(ImageButton)findViewById(R.id.send_msg_button);
        userMessageInput=(EditText)findViewById(R.id.input_group_msg);
        groupMessagesAdapter=new GroupMessagesAdapter(GroupChatActivity.this,groupMsgList);
        groupMsgRecyclerView=(RecyclerView)findViewById(R.id.group_messages_list_of_users);
        groupMsgRecyclerView.setAdapter(groupMessagesAdapter);
        userName = findViewById(R.id.userName);
        userLastSeen = findViewById(R.id.userlastseen);



    }

}
