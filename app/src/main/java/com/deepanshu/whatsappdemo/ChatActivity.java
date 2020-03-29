package com.deepanshu.whatsappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String MessageReceiverId, messageReceiverName, messageSenderId;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private String userProfileImage,userOnlineState;
    private ImageButton SendMessageBotton,google_mic,attachFile;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private final List<Messages> messagesList=new ArrayList<>();
    private  MessagesAdapter messagesAdapter;
    private RecyclerView userMessageList;
    private String userLastseenDate,userLastSeenTime;
    private final int ReqCode=100,requestcode2=438;
    private String Cheker="",myUri=" ";
    private StorageTask uploadTask;
    private Uri fileUri;
    private StorageTask storageTask;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_layout, null);
        actionBar.setCustomView(actionBarView);

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
                Messages messages=dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
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
                final CharSequence options[]=new CharSequence[]{"Images","PDF Files","Ms Word Files"};
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            Cheker="image";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");//this will send the user to the galaery and user selec t the picute
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }
                        if (which == 1) {
                           Cheker="pdf";
                        }
                        if (which == 2) {
                            Cheker="docs";
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
        userOnlineState=getIntent().getExtras().get("state").toString();
        if(userOnlineState.equalsIgnoreCase("online")){
            userLastSeen.setText("online");
        }
        else {
            userLastseenDate = getIntent().getExtras().get("date").toString();
            userLastSeenTime = getIntent().getExtras().get("time").toString();
            userLastSeen.setText("last seen at " + userLastSeenTime + " " + userLastseenDate);
        }


    }

    private void SendMessage() {
        String messageText = MessageInputText.getText().toString();
        MessageInputText.setText("");
        userOnlineState=getIntent().getExtras().get("state").toString();

        if(userOnlineState.equalsIgnoreCase("online")){
            userLastSeen.setText("online");
        }
        else {
            userLastseenDate = getIntent().getExtras().get("date").toString();
            userLastSeenTime = getIntent().getExtras().get("time").toString();
            userLastSeen.setText("last seen at " + userLastSeenTime + " " + userLastseenDate);
        }

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "First write your message", Toast.LENGTH_SHORT).show();
        }

        else {


            final String messageSenderRef = "Messages/" + messageSenderId + "/" + MessageReceiverId;
            final String messageReceiverRef = "Messages/" + MessageReceiverId + "/" + messageSenderId;
            DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                    .child(MessageReceiverId).push();
            final String messagePushId = userMessageKeyRef.getKey();///to create pushid

                Map messageImageBody = new HashMap();
                messageImageBody.put("message",messageText );
                messageImageBody.put("type", "text");
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
                            Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        } else
                            Toast.makeText(ChatActivity.this, "Message is not sent", Toast.LENGTH_SHORT).show();
                    }
                });

            }


        }




    private void googleSpeakToTextTask() {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Need to speak");
        try{
            startActivityForResult(intent,ReqCode);

        }catch (ActivityNotFoundException a){
            Toast.makeText(ChatActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ReqCode:
                if(resultCode==RESULT_OK && null!=data){
                    //Toast.makeText(this, "activity result ok", Toast.LENGTH_SHORT).show();

                    ArrayList result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    MessageInputText.setText(result.get(0)+"");
                }
                break;
            case requestcode2:
                if(resultCode==RESULT_OK && null!=data && data.getData()!=null){
                    loadingBar.setTitle("Sending File");
                    loadingBar.setMessage("please wait,We are sending your file");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    fileUri=data.getData();//we store the file which is seldct and stor in fileurie
                    if(!Cheker.equalsIgnoreCase("image")){

                    }


                    else if(Cheker.equalsIgnoreCase("image")){
                        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");//makeing other node at the firevase

                        final String messageSenderRef = "Messages/" + messageSenderId + "/" + MessageReceiverId;
                        final String messageReceiverRef = "Messages/" + MessageReceiverId + "/" + messageSenderId;
                        DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                                .child(MessageReceiverId).push();
                        final String messagePushId = userMessageKeyRef.getKey();///to create pushid
                        final StorageReference filePath=storageReference.child(messagePushId+"."+"jpg");
                        uploadTask=filePath.putFile(fileUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw  task.getException();
                                }
                                return filePath.getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Uri downloadUri=task.getResult();
                                    myUri=downloadUri.toString();

                                    Map messageImageBody = new HashMap();
                                    messageImageBody.put("message", myUri);
                                    messageImageBody.put("name", fileUri.getLastPathSegment());
                                    messageImageBody.put("type", Cheker);
                                    messageImageBody.put("from",messageSenderId);
                                    messageImageBody.put("to",MessageReceiverId);
                                    messageImageBody.put("messageId",messagePushId);
                                    messageImageBody.put("time",userLastSeenTime);
                                    messageImageBody.put("date",userLastseenDate);
                                    Map messageBodyDetails=new HashMap();
                                    messageBodyDetails.put(messageSenderRef+"/"+messagePushId,messageImageBody);
                                    messageBodyDetails.put(messageReceiverRef+"/"+messagePushId,messageImageBody);

                                    rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            //if any eerrror occurs
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else
                                                Toast.makeText(ChatActivity.this, "Message is not sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });

                    }


                    else{
                        Toast.makeText(this, "Nothing selected ,Error..!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }


                }


        }

    }



    private void initialisation() {
        loadingBar=new ProgressDialog(this);
        userName = findViewById(R.id.userName);
        userLastSeen = findViewById(R.id.userlastseen);
        userImage = findViewById(R.id.custom_profile_image);
        MessageInputText = findViewById(R.id.input_message);
        attachFile = (ImageButton)findViewById(R.id.send_pic);
        google_mic = (ImageButton)findViewById(R.id.google_mike);
        SendMessageBotton = (ImageButton)findViewById(R.id.send_message_btn);
        messagesAdapter=new MessagesAdapter(messagesList);
        userMessageList=(RecyclerView)findViewById(R.id.private_messages_list_of_users);
//current date and time  screen shot is present
    }



}
