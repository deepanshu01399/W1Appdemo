package com.deepanshu.whatsappdemo.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deepanshu.whatsappdemo.interfaces.APIservice;
import com.deepanshu.whatsappdemo.model.Client;
import com.deepanshu.whatsappdemo.model.Contacts;
import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.model.Notifi_Response;
import com.deepanshu.whatsappdemo.model.NotificationData;
import com.deepanshu.whatsappdemo.model.Sender;
import com.deepanshu.whatsappdemo.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Reqeust_fragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView myRecylerList;
    private DatabaseReference chatRequestRef, userRef, ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    GifImageView gifImageView;
    APIservice apIservice;

    TextView txt_No_requestMsg;

    public Reqeust_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        mAuth = FirebaseAuth.getInstance();
        apIservice = Client.getClient("https://fcm.googleapis.com/").create(APIservice.class);
        currentUserId = mAuth.getCurrentUser().getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestFragmentView = inflater.inflate(R.layout.reqeust_fragment, container, false);
        myRecylerList = (RecyclerView) requestFragmentView.findViewById(R.id.chat_request_list);
        gifImageView = (GifImageView) requestFragmentView.findViewById(R.id.gifareoplane);
        txt_No_requestMsg = (TextView) requestFragmentView.findViewById(R.id.no_requestMsg);
        myRecylerList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserId), Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, RequestviewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestviewHolder holder, int position, @NonNull Contacts model) {
                // Toast.makeText(getContext(), "adapter", Toast.LENGTH_SHORT).show();

                holder.itemView.findViewById(R.id.user_profile_name).setVisibility(View.INVISIBLE);
                holder.itemView.findViewById(R.id.user_status).setVisibility(View.INVISIBLE);
                holder.itemView.findViewById(R.id.user_profile_image).setVisibility(View.INVISIBLE);
                holder.itemView.findViewById(R.id.user_online_icon).setVisibility(View.INVISIBLE);
                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.INVISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);


                final String list_user_id = getRef(position).getKey();
                final DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Toast.makeText(getContext(), "datasnapshot exixts", Toast.LENGTH_SHORT).show();
                            String type = dataSnapshot.getValue().toString();
                            // Toast.makeText(getContext(), "type: " + type, Toast.LENGTH_SHORT).show();
                            if (type.equals("received")) {
                                holder.itemView.findViewById(R.id.user_profile_name).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.user_status).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.user_profile_image).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.user_online_icon).setVisibility(View.INVISIBLE);
                                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                                //Toast.makeText(getContext(), "received", Toast.LENGTH_SHORT).show();
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Toast.makeText(getContext(), "DCreceived", Toast.LENGTH_SHORT).show();

                                        if (dataSnapshot.hasChild("image")) {

                                            final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                            final String user_status = dataSnapshot.child("status").getValue().toString();
                                            final String requestProfileImage = dataSnapshot.child("image").toString();

                                            holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ContactsRef.child(currentUserId).child(list_user_id).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                ContactsRef.child(list_user_id).child(currentUserId).child("Contact")
                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            chatRequestRef.child(currentUserId).child(list_user_id)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });


                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }

                                                    });

                                                }
                                            });
                                            holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //CancelSendRequest();

                                                    /*
                                                    ContactsRef.child(currentUserId).child(list_user_id).child("Contact")
                                                            .setValue("Saved")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                ContactsRef.child(list_user_id).child(currentUserId).child("Contact")
                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){*/
                                                    chatRequestRef.child(currentUserId).child(list_user_id)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), "Contact deleted ", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });


                                                }
                                            });

                                                                /*});

                                                            }
                                                        }

                                                    });
                                                }
                                            });*/

                                            holder.userName.setText(requestUserName);
                                            //  Toast.makeText(getContext(), "userName" + requestUserName + "\n request_status :" + user_status, Toast.LENGTH_SHORT).show();
                                            holder.userProfileStatus.setText(user_status);
                                            Picasso.get().load(requestProfileImage).placeholder(R.drawable.profile_image).into(holder.profileImage);


                                        } else {
                                            //Toast.makeText(getContext(), "received DCnot image", Toast.LENGTH_SHORT).show();

                                            final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                            final String cancel = dataSnapshot.child("status").getValue().toString();

                                            holder.userName.setText(requestUserName);
                                            holder.userProfileStatus.setText(cancel);

                                        }
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            //in future dekha jayega "sent request ko bhi show karna

                            else if (type.equals("sent")) {//for sent request
                                holder.itemView.findViewById(R.id.user_profile_name).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.user_status).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.user_profile_image).setVisibility(View.VISIBLE);
                                //holder.itemView.findViewById(R.id.user_online_icon).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                                String msg = "Want's to Connect you";

                                //holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                            final String userStatus = dataSnapshot.child("status").getValue().toString();
                                            final String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                            holder.userName.setText(requestUserName);
                                            holder.userProfileStatus.setText(userStatus);
                                            holder.AcceptButton.setBackgroundColor(Color.parseColor("#ffff4444"));
                                            holder.AcceptButton.setText("  Cancel sent request  ");
                                            holder.AcceptButton.setTextColor(Color.parseColor("#FFFFFFFF"));
                                            sendNotification(list_user_id, requestProfileImage, msg);
                                            Picasso.get().load(requestProfileImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                            holder.AcceptButton.setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    //CancelSendRequest();
                                                    chatRequestRef.child(currentUserId).child(list_user_id)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), "Contact deleted ", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }


                                            });


                                        } else {
                                            final String requestUserName = dataSnapshot.child("name").toString();
                                            final String userStatus = dataSnapshot.child("status").toString();

                                            holder.userName.setText(requestUserName);
                                            holder.userProfileStatus.setText(userStatus);
                                            holder.AcceptButton.setBackgroundColor(Color.parseColor("#ffff4444"));
                                            holder.AcceptButton.setText("  Cancel sent request  ");
                                            holder.AcceptButton.setTextColor(Color.parseColor("#FFFFFFFF"));

                                            holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //CancelSendRequest();
                                                    chatRequestRef.child(currentUserId).child(list_user_id)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), "Contact deleted ", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }


                                            });


                                        }
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }


                        }
                        /*else{//todo tried to used for "having no friend request pending
                            myRecylerList.setVisibility(View.GONE);
                            gifImageView.setVisibility(View.VISIBLE);
                            txt_No_requestMsg.setVisibility(View.VISIBLE);

                        }*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
            }


       /*
            private void CancelSendRequest() {
                chatRequestRef.child(currentUserId).child(list_user_id)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    chatRequestRef.child(list_user_id).child(currentUserId)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Contact deleted ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }

            */

            @NonNull
            @Override
            public RequestviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                return new RequestviewHolder(view);
            }

        };
        myRecylerList.setAdapter(adapter);
        adapter.startListening();

    }

    private void sendNotification(String list_user_id, String requestProfileImage, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(list_user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    //NotificationData data = new NotificationData(msg, messageSenderId, "notification", "sended", R.mipmap.ic_launcher);
                    NotificationData data = new NotificationData("this is message", "deepanshu", "notification", "sended", R.mipmap.ic_launcher);
                    Sender sender = new Sender(token.getToken(), data);
                    apIservice.notificaiton_response(sender).enqueue(new Callback<Notifi_Response>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(Call<Notifi_Response> call, Response<Notifi_Response> response) {
                            if (response.code() == 200) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Request sent Successfull ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Request Not sent Successfull", Toast.LENGTH_SHORT).show();
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


    public static class RequestviewHolder extends RecyclerView.ViewHolder {
        TextView userName, userProfileStatus;
        CircleImageView profileImage;
        ImageView online_icon;
        Button AcceptButton, CancelButton;

        public RequestviewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userProfileStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.user_profile_image);
            online_icon = itemView.findViewById(R.id.user_online_icon);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);


        }
    }


}