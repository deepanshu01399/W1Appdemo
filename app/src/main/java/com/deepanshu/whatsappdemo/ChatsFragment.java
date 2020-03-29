package com.deepanshu.whatsappdemo;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {
private View privateChat_View;
private RecyclerView chatsList;
private DatabaseReference chatReference,UserRef;//to create query we need chatref;
private FirebaseAuth mAuth;
private String CurrentUserId;
public  ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        privateChat_View= inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserId=mAuth.getCurrentUser().getUid();
        chatsList=(RecyclerView)privateChat_View.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        chatReference= FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserId);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");

        return privateChat_View;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatReference, Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        return new ChatsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String userId = getRef(position).getKey();//get id from the db
                        final String[] profileImage = {"default_image"};

                        UserRef.child(userId).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    profileImage[0] = dataSnapshot.child("image").getValue().toString();
                                    final String profileName = dataSnapshot.child("name").getValue().toString();
                                    String profileStatus = dataSnapshot.child("status").getValue().toString();
                                    final String userState=dataSnapshot.child("userState").child("state").getValue().toString();
                                    final String userLastSeenDate=dataSnapshot.child("userState").child("date").getValue().toString();
                                    final String userLastSeenTime=dataSnapshot.child("userState").child("time").getValue().toString();

                                    holder.userName.setText(profileName);
                                    if(userState.equalsIgnoreCase("online")){
                                        holder.online_staus.setVisibility(View.VISIBLE);
                                        holder.UserStatus.setText(profileStatus);

                                    }
                                    else {
                                        holder.online_staus.setVisibility(View.INVISIBLE);
                                        holder.UserStatus.setText("Last seen:"+userLastSeenTime+"\n"+userLastSeenDate);

                                    }

                                   // holder.UserStatus.setText(profileStatus);
                                    Picasso.get().load(profileImage[0]).placeholder(R.drawable.profile_image).into(holder.user_profileImage);
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("Visit_user_id",userId);
                                            chatIntent.putExtra("User_name",profileName);
                                            chatIntent.putExtra("user_profile_image", profileImage[0]);
                                            chatIntent.putExtra("state",userState);
                                            chatIntent.putExtra("time",userLastSeenTime);
                                            chatIntent.putExtra("date",userLastSeenDate);
                                            startActivity(chatIntent);
                                        }
                                    });

                                } else {
                                    String profileName = dataSnapshot.child("name").getValue().toString();
                                    String profileStatus = dataSnapshot.child("status").getValue().toString();
                                    holder.userName.setText(profileName);
                                    holder.UserStatus.setText(profileStatus);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                };

        chatsList.setAdapter(adapter);
        adapter.startListening();
                };


    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        TextView userName,UserStatus;
        CircleImageView user_profileImage;
        ImageView online_staus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            UserStatus=itemView.findViewById(R.id.user_status);
            online_staus=itemView.findViewById(R.id.user_online_icon);
            user_profileImage=itemView.findViewById(R.id.user_profile_image);

        }
    }

}
