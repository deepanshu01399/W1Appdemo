package com.deepanshu.whatsappdemo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationAction;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ContectFragment extends Fragment {
    private View ContactsView;
    private RecyclerView myContactsList;
    private DatabaseReference contactsRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ContectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactsView= inflater.inflate(R.layout.fragment_contect, container, false);

        myContactsList=(RecyclerView)ContactsView.findViewById(R.id.Contact_recylerlist);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(ContectFragment.this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //myContactsList.setLayoutManager(layoutManager);

        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        return ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new  FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                String userId=getRef(position).getKey();
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            String profileImage=dataSnapshot.child("image").getValue().toString();
                            String profileName=dataSnapshot.child("name").getValue().toString();
                            String profileStatus=dataSnapshot.child("status").getValue().toString();
                            holder.userName.setText(profileName);
                            holder.UserStatus.setText(profileStatus);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.user_profileImage);

                        }
                        else {
                            String profileName=dataSnapshot.child("name").getValue().toString();
                            String profileStatus=dataSnapshot.child("status").getValue().toString();
                            holder.userName.setText(profileName);
                            holder.UserStatus.setText(profileStatus);

                        }

                    }  @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
                return new ContactsViewHolder(view);
            }
        };
        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }
     public static class ContactsViewHolder extends RecyclerView.ViewHolder{

             TextView userName,UserStatus;
             CircleImageView user_profileImage;
             ImageView online_staus;

        public ContactsViewHolder(@NonNull View itemView) {
                 super(itemView);
                 userName=itemView.findViewById(R.id.user_profile_name);
                 UserStatus=itemView.findViewById(R.id.user_status);
                 online_staus=itemView.findViewById(R.id.user_online_icon);
                 user_profileImage=itemView.findViewById(R.id.user_profile_image);

             }
         }
     }

