package com.deepanshu.whatsappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_activity extends AppCompatActivity {
    private String receiverUserId,sender_User_id,Current_state;
    private CircleImageView userProfileImage;
    private TextView userProfileName,userProfileStatus;
    private Button SendMessageRequesetButton, declineMsgRequestButton;
    private DatabaseReference userRef,ChatRequestRef,ContactsRef,NotificationRef ;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef= FirebaseDatabase.getInstance().getReference().child("Notifications");

        receiverUserId=getIntent().getExtras().get("visit_user_id").toString();//comming from toher activity
        sender_User_id=mAuth.getInstance().getUid();

       // Toast.makeText(this, "User Id:"+receiverUserId, Toast.LENGTH_SHORT).show();
        userProfileImage=(CircleImageView)findViewById(R.id.visit_profile_image);
        userProfileName=(TextView)findViewById(R.id.visit_user_name);
        userProfileStatus=(TextView)findViewById(R.id.visit_user_status);
        SendMessageRequesetButton=(Button)findViewById(R.id.send_messsage_request_button);
        declineMsgRequestButton =(Button)findViewById(R.id.decline_message_request_button);

        Current_state="new";
        retriveUserInformation();



    }

    private void retriveUserInformation() {
        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {
                   // Toast.makeText(Profile_activity.this, "profile image found", Toast.LENGTH_SHORT).show();
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    managechatRequest();

                }
                else{
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                }

            } @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void managechatRequest() {
        ChatRequestRef.child(sender_User_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receiverUserId)){
                    String request_type=dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        Current_state="request_sent";
                        SendMessageRequesetButton.setText("Cancel chat request");
                    }
                    else if(request_type.equals("received")){
                        Current_state="request_Received";
                        SendMessageRequesetButton.setText("Accept Chat Request");
                        declineMsgRequestButton.setVisibility(View.VISIBLE);
                        declineMsgRequestButton.setEnabled(true);
                        declineMsgRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Cancel_chat_request();

                            }
                        });
                    }

                }
                else{
                    ContactsRef.child(sender_User_id)
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.hasChild(receiverUserId)){
                                       Current_state="friends";
                                       SendMessageRequesetButton.setText("Remove this Contact");
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(!sender_User_id.equals(receiverUserId)){
            SendMessageRequesetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessageRequesetButton.setEnabled(false);
                    if(Current_state.equals("new")){
                        SendChatRequest();
                        
                    }
                    if(Current_state.equals("request_sent")){
                        Cancel_chat_request();
                    }
                    if(Current_state.equals("request_Received")){
                        AcceptChatRequest();
                    }if(Current_state.equals("friends")){
                        RemoveSpecificContact();
                    }
                }
            });

        }
        else{
            SendMessageRequesetButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {
        ContactsRef.child(sender_User_id).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactsRef.child(receiverUserId).child(sender_User_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendMessageRequesetButton.setEnabled(true);
                                                Current_state="new ";
                                                SendMessageRequesetButton.setText("Send Messages");
                                                declineMsgRequestButton.setVisibility(View.INVISIBLE);
                                                declineMsgRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void AcceptChatRequest() {
        ContactsRef.child(sender_User_id).child(receiverUserId)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        ContactsRef.child(receiverUserId).child(sender_User_id)
                                .child("Contacts").setValue("saved")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                        ChatRequestRef.child(sender_User_id).child(receiverUserId)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            ChatRequestRef.child(receiverUserId).child(sender_User_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            SendMessageRequesetButton.setEnabled(true);
                                                                            SendMessageRequesetButton.setVisibility(View.VISIBLE);
                                                                            Current_state="friends";
                                                                            SendMessageRequesetButton.setText("Remove this contact");
                                                                            declineMsgRequestButton.setVisibility(View.INVISIBLE);
                                                                            declineMsgRequestButton.setEnabled(false);

                                                                        }
                                                                    });
                                                    }
                                                }});

                                        }
                                    }});


                    }
                }});

    }

    private void Cancel_chat_request() {
        ChatRequestRef.child(sender_User_id).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ChatRequestRef.child(receiverUserId).child(sender_User_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendMessageRequesetButton.setEnabled(true);
                                                Current_state="new ";
                                                SendMessageRequesetButton.setText("Send Messages");
                                                declineMsgRequestButton.setVisibility(View.INVISIBLE);
                                                declineMsgRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void SendChatRequest() {
        ChatRequestRef.child(sender_User_id).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ChatRequestRef.child(receiverUserId).child(sender_User_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            HashMap<String ,String> chatnotificationMap =new HashMap<>();
                                            chatnotificationMap.put("from",sender_User_id);
                                            chatnotificationMap.put("type","request");
                                            NotificationRef.child(receiverUserId).push()
                                                    .setValue(chatnotificationMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        //every movile has its device/token id to send the notificaiton
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                SendMessageRequesetButton.setEnabled(true);
                                                                Current_state="request_sent";
                                                                SendMessageRequesetButton.setText("Cancel Chat Request");

                                                            }
                                                        }
                                                    });
                                           }
                                    });
                        }
                    }
                });
    }
}
