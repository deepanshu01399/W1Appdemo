package com.deepanshu.whatsappdemo.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.activity.Profile_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecylcerList;
    private DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friends_activity);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        initialisation();
    }

    private void initialisation() {
        FindFriendsRecylcerList=(RecyclerView)findViewById(R.id.find_friend_recylerView);
        FindFriendsRecylcerList.setLayoutManager(new LinearLayoutManager(this));
        mToolbar=(Toolbar)findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //firebase recylere opiton will ve create
        FirebaseRecyclerOptions<Contacts> contactsFirebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UserRef,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(contactsFirebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.UserStatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id=getRef(position).getKey();
                                Intent profileIntent=new Intent(FindFriends.this, Profile_activity.class);
                                profileIntent.putExtra("visit_user_id",visit_user_id);
                                startActivity(profileIntent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
                        return new FindFriendViewHolder(view);
                    }
                };
        FindFriendsRecylcerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
        TextView userName,UserStatus;
        CircleImageView profileImage;
        ImageView online_staus;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            UserStatus=itemView.findViewById(R.id.user_status);
            online_staus=itemView.findViewById(R.id.user_online_icon);
            profileImage=itemView.findViewById(R.id.user_profile_image);

        }
    }



}