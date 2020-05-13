package com.deepanshu.whatsappdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.deepanshu.whatsappdemo.interfaces.APIservice;
import com.deepanshu.whatsappdemo.model.Client;
import com.deepanshu.whatsappdemo.model.Messages;
import com.deepanshu.whatsappdemo.adapter.MessagesAdapter;
import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.extraUtil.SharedPreferencesFactory;
import com.deepanshu.whatsappdemo.model.Notifi_Response;
import com.deepanshu.whatsappdemo.model.NotificationData;
import com.deepanshu.whatsappdemo.model.Sender;
import com.deepanshu.whatsappdemo.model.Token;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.protobuf.Api;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.deepanshu.whatsappdemo.activity.MainActivity.ONLINE_STATUS;

public class Personal_ChatActivity extends AppCompatActivity implements View.OnClickListener, MessagesAdapter.IPersonalchat {
    private String MessageReceiverId, messageReceiverName, messageSenderId;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private String userProfileImage, userOnlineState;
    private ImageButton SendMessageBotton, google_mic, attachFile;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, notifyrootref;
    private final List<Messages> personalMsgList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private RecyclerView userMessageList;
    private String userLastseenDate, userLastSeenTime;
    private final int ReqCode = 100, requestcode2 = 438;
    private String Cheker = "", myUri = " ";
    private StorageTask uploadTask;
    private Uri fileUri;
    private StorageTask storageTask;
    private ProgressDialog loadingBar;
    private ImageButton backImage;
    SharedPreferencesFactory sharedPreferencesFactory = null;
    String saveCurrentTime, saveCurrentDate;
    FirebaseMessagingService firebaseMessagingService;
    APIservice apIservice;
    Boolean notify = false;
    String messagePushId;
    Map messageBodyDetails;
    Map messageImageBody;
    String messageSenderRef;
    String messageReceiverRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_msg_activity);

        chatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        apIservice = Client.getClient("https://fcm.googleapis.com/").create(APIservice.class);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_layout, null);
        actionBar.setCustomView(actionBarView);
        backImage = actionBarView.findViewById(R.id.imgBack);
        backImage.setOnClickListener(this);

        messageReceiverName = getIntent().getExtras().get("User_name").toString();
        MessageReceiverId = getIntent().getExtras().get("Visit_user_id").toString();
        userProfileImage = getIntent().getExtras().get("user_profile_image").toString();

        initialisation();

        Picasso.get().load(userProfileImage).placeholder(R.drawable.profile_image).into(userImage);
        userName.setText(messageReceiverName);

        userMessageList.setLayoutManager(new LinearLayoutManager(this));
        userMessageList.setAdapter(messagesAdapter);

        SendMessageBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        google_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleSpeakToTextTask();

            }

        });

        rootRef.child("Messages").child(messageSenderId).child(MessageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages personalMsg = dataSnapshot.getValue(Messages.class);
                personalMsgList.add(personalMsg);
                messagesAdapter.notifyDataSetChanged();
                //for scrolling upto the last
                userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
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

        attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence options[] = new CharSequence[]{"Images", "PDF Files", "Ms Word Files"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_ChatActivity.this);
                builder.setTitle("Select File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            Cheker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");//this will send the user to the galaery and user selec t the picute
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                        }
                        if (which == 1) {
                            Cheker = "pdf";
                        }
                        if (which == 2) {
                            Cheker = "docs";
                        }
                    }
                });
                builder.show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        userOnlineState = getIntent().getExtras().get("state").toString();
        sharedPreferencesFactory = SharedPreferencesFactory.getInstance(this);
        SharedPreferences prefs = sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);
        sharedPreferencesFactory.writePreferenceValue(ONLINE_STATUS, userOnlineState);
        String online_status = sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS);
        Log.d("online_status", online_status);
        if (sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS) != null) {
            if (sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS).equalsIgnoreCase("online")) {
                userLastSeen.setText(sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS));
            } else {
                userLastseenDate = getIntent().getExtras().get("date").toString();
                userLastSeenTime = getIntent().getExtras().get("time").toString();
                userLastSeen.setText("last seen at " + userLastSeenTime + " " + userLastseenDate);
                //userLastSeen.setText(sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS));
            }
        }
    }


    private void SendMessage() {
        String messageText = MessageInputText.getText().toString();
        MessageInputText.setText("");
        userOnlineState = getIntent().getExtras().get("state").toString();
        sharedPreferencesFactory.writePreferenceValue(ONLINE_STATUS, userOnlineState);
        sharedPreferencesFactory = SharedPreferencesFactory.getInstance(this);
        SharedPreferences prefs = sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);
        String online_status = sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS);
        Log.d("online_statuse", online_status);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        if (sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS) != null) {
            userLastseenDate = saveCurrentDate;
            userLastSeenTime = saveCurrentTime;

            if (sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS).equalsIgnoreCase("online")) {
                // if(userOnlineState.equalsIgnoreCase("online"))
                //userLastSeen.setText("online");
                userLastSeen.setText(sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS));
            } else {
                userLastSeen.setText("last seen at " + userLastSeenTime + " " + userLastseenDate);
                //userLastSeen.setText(sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS));
            }
        }
        //todo start messaging sending and receiver
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "First write your message", Toast.LENGTH_SHORT).show();
        } else {
            notify = true;
            messageSenderRef = "Messages/" + messageSenderId + "/" + MessageReceiverId;
            messageReceiverRef = "Messages/" + MessageReceiverId + "/" + messageSenderId;
            DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                    .child(MessageReceiverId).push();
            messagePushId = userMessageKeyRef.getKey();///to create pushid

            messageImageBody = new HashMap();
            messageImageBody.put("message", messageText);
            messageImageBody.put("type", "text");
            messageImageBody.put("from", messageSenderId);
            messageImageBody.put("to", MessageReceiverId);
            messageImageBody.put("messageId", messagePushId);
            messageImageBody.put("time", userLastSeenTime);
            messageImageBody.put("date", userLastseenDate);
            messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageImageBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageImageBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    //if no  error occurs
                    if (task.isSuccessful()) {
                        // sendNotification(MessageReceiverId, (String) messageImageBody.get("from"),"this is my message");

                        Toast.makeText(Personal_ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        notify = true;

                    } else
                        Toast.makeText(Personal_ChatActivity.this, "Message is not sent", Toast.LENGTH_SHORT).show();
                }
            });
            final String msg = messageText;
            //rootRef.child("Messages").child(messageSenderId).child(MessageReceiverId).addChildEventListener(new ChildEventListener() {
            //rootRef = FirebaseDatabase.getInstance().getReference();
            if (!sharedPreferencesFactory.getPreferenceValue(ONLINE_STATUS).equalsIgnoreCase("online")) {
                //todo send notification only when user was offline
                notifyrootref = FirebaseDatabase.getInstance().getReference("Users").child(messageSenderId);
                notifyrootref.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Messages personalMsg = dataSnapshot.getValue(Messages.class);
                        if (notify) {
                            sendNotification(MessageReceiverId, personalMsg.getFrom(), msg);
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    private void sendNotification(String messageReceiverId, String name, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(messageReceiverId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    //NotificationData data = new NotificationData(msg, messageSenderId, "notification", "sended", R.mipmap.ic_launcher);
                    NotificationData data = new NotificationData("this is message", "deepanshu", "notification", "sended", R.mipmap.ic_launcher);
                    Sender sender = new Sender(token.getToken(), data);
                    apIservice.notificaiton_response(sender).enqueue(new Callback<Notifi_Response>() {
                        @Override
                        public void onResponse(Call<Notifi_Response> call, Response<Notifi_Response> response) {
                            if (response.code() == 200) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Successful sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Not send notification", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<Notifi_Response> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void googleSpeakToTextTask() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, ReqCode);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(Personal_ChatActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ReqCode:
                if (resultCode == RESULT_OK && null != data) {
                    //Toast.makeText(this, "activity result ok", Toast.LENGTH_SHORT).show();

                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    MessageInputText.setText(result.get(0) + "");
                }
                break;
            case requestcode2:
                if (resultCode == RESULT_OK && null != data && data.getData() != null) {
                    loadingBar.setTitle("Sending File");
                    loadingBar.setMessage("please wait,We are sending your file");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    fileUri = data.getData();//we store the file which is seldct and stor in fileurie
                    if (!Cheker.equalsIgnoreCase("image")) {

                    } else if (Cheker.equalsIgnoreCase("image")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");//makeing other node at the firevase

                        final String messageSenderRef = "Messages/" + messageSenderId + "/" + MessageReceiverId;
                        final String messageReceiverRef = "Messages/" + MessageReceiverId + "/" + messageSenderId;
                        DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                                .child(MessageReceiverId).push();
                        final String messagePushId = userMessageKeyRef.getKey();///to create pushid
                        final StorageReference filePath = storageReference.child(messagePushId + "." + "jpg");
                        uploadTask = filePath.putFile(fileUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filePath.getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    myUri = downloadUri.toString();

                                    Map messageImageBody = new HashMap();
                                    messageImageBody.put("message", myUri);
                                    messageImageBody.put("name", fileUri.getLastPathSegment());
                                    messageImageBody.put("type", Cheker);
                                    messageImageBody.put("from", messageSenderId);
                                    messageImageBody.put("to", MessageReceiverId);
                                    messageImageBody.put("messageId", messagePushId);
                                    messageImageBody.put("time", userLastSeenTime);
                                    messageImageBody.put("date", userLastseenDate);
                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageImageBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageImageBody);

                                    rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            //if any eerrror occurs
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Personal_ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else
                                                Toast.makeText(Personal_ChatActivity.this, "Message is not sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });

                    } else {
                        Toast.makeText(this, "Nothing selected ,Error..!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }


                }


        }

    }


    private void initialisation() {
        loadingBar = new ProgressDialog(this);
        userName = findViewById(R.id.userName);
        userLastSeen = findViewById(R.id.userlastseen);
        userImage = findViewById(R.id.custom_profile_image);
        MessageInputText = findViewById(R.id.input_message);
        attachFile = (ImageButton) findViewById(R.id.send_pic);
        google_mic = (ImageButton) findViewById(R.id.google_mike);
        SendMessageBotton = (ImageButton) findViewById(R.id.send_message_btn);
        messagesAdapter = new MessagesAdapter(personalMsgList, this);
        userMessageList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        //current date and time  screen shot is present
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
        }

    }

    @Override
    public void longPressOnMsgFromSender(Messages messages) {
        String text = messages.getMessage();
        Toast.makeText(this, "message: " + text, Toast.LENGTH_SHORT).show();
        final CharSequence options[] = new CharSequence[]{"Delete for All", "Delete from me", "Forword", "Quote"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Personal_ChatActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Cheker = "Delete for All";
                    delteMsgfromBothSide();
                    // Intent intent = new Intent();
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    //intent.setType("image/*");//this will send the user to the galaery and user selec t the picute
                    //startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                }
                if (which == 1) {
                    Cheker = "Delete from me";
                    delteMsgfromMe(messageSenderId, MessageReceiverId);
                }
                if (which == 2) {
                    Cheker = "forword";
                    Toast.makeText(Personal_ChatActivity.this, "Msg: " + Cheker, Toast.LENGTH_SHORT).show();

                }
                if (which == 3) {
                    Cheker = "quote";
                    Toast.makeText(Personal_ChatActivity.this, "Msg: " + Cheker, Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.show();
    }

    @Override
    public void lognPressOnMsgFromReceiver(Messages messages) {
        String text = messages.getMessage();
        Toast.makeText(this, "message: " + text, Toast.LENGTH_SHORT).show();
        final CharSequence options[] = new CharSequence[]{ "Forword", "Quote"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Personal_ChatActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*if (which == 0) {
                    Cheker = "Delete for me";
                    Toast.makeText(firebaseMessagingService, "This feature will be comming soon..", Toast.LENGTH_SHORT).show();
                    //delteMsgfromMe(MessageReceiverId, messageSenderId);
                }*/
                if (which == 0) {
                    Cheker = "forword";
                    Toast.makeText(Personal_ChatActivity.this, "Msg: " + Cheker, Toast.LENGTH_SHORT).show();

                }
                if (which == 2) {
                    Cheker = "quote";
                    Toast.makeText(Personal_ChatActivity.this, "Msg: " + Cheker, Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.show();
    }

    private void delteMsgfromMe(String from, String to) {
        rootRef.child("Messages").child(from).child(to).child(messagePushId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            messageBodyDetails.remove(messageReceiverRef + "/" + messagePushId, messageImageBody);
                            messagesAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "msg deleted  ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void delteMsgfromBothSide() {
        rootRef.child("Messages").child(messageSenderId).child(MessageReceiverId).child(messagePushId).
                removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            messageBodyDetails.remove(messageSenderRef + "/" + messagePushId, messageImageBody);
                            messagesAdapter.notifyDataSetChanged();
                            rootRef.child("Messages").child(MessageReceiverId).child(messageSenderId).child(messagePushId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                messageBodyDetails.remove(messageReceiverRef + "/" + messagePushId, messageImageBody);
                                                messagesAdapter.notifyDataSetChanged();
                                                Toast.makeText(getApplicationContext(), "msg deleted ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
