package com.deepanshu.whatsappdemo.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepanshu.whatsappdemo.model.Messages;
import com.deepanshu.whatsappdemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private List<Messages> userPersonalMsgList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    IPersonalchat iPersonalchat;

    public MessagesAdapter(List<Messages> userPersonalMsgList,IPersonalchat iPersonalchat) {
        this.userPersonalMsgList = userPersonalMsgList;
        this.iPersonalchat=iPersonalchat;

    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_msg_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages personalMsg = userPersonalMsgList.get(position);
        String fromUserId = personalMsg.getFrom();
        String fromMessageType = personalMsg.getType();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverProfileImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverProfileImage).placeholder(R.drawable.profile_image).into(holder.recieverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.receiverMessageText.setVisibility(View.INVISIBLE);
        holder.recieverProfileImage.setVisibility(View.INVISIBLE);
        holder.senderMessageText.setVisibility(View.INVISIBLE);
        holder.receiverImageLayout.setVisibility(View.GONE);
        holder.senderimageLayout.setVisibility(View.GONE);



        if (fromUserId.equals(messageSenderId)) {

            if (fromMessageType.equalsIgnoreCase("text")) {
                holder.senderMsgImage.setMaxHeight(0);
                // holder.senderMsgImage.setVisibility(View.INVISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.reciverMsgLayout.setVisibility(View.GONE);
                holder.sender_msg_layout.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sendermsglayout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(personalMsg.getMessage());
                holder.sender_time_text.setText(personalMsg.getTime());

            } else {
                holder.senderimageLayout.setVisibility(View.VISIBLE);
                Picasso.get().load(personalMsg.getMessage()).into(holder.senderMsgImage);
            }
        }

        else {
            holder.recieverProfileImage.setVisibility(View.VISIBLE);
            holder.sender_msg_layout.setVisibility(View.GONE);
            if (fromMessageType.equalsIgnoreCase("text")) {
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.reciverMsgLayout.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.recievermsglayout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(personalMsg.getMessage());
                holder.receiver_msg_time.setText(personalMsg.getTime());
            } else if (fromMessageType.equalsIgnoreCase("image")) {
                holder.receiverImageLayout.setVisibility(View.VISIBLE);
                Picasso.get().load(personalMsg.getMessage()).into(holder.receicerMsgImage);

            }
        }

    }


    @Override
    public int getItemCount() {
        return userPersonalMsgList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText,receiver_msg_time,sender_time_text;
        public CircleImageView recieverProfileImage;
        public ImageView senderMsgImage, receicerMsgImage;
        private  LinearLayout senderimageLayout,receiverImageLayout,reciverMsgLayout,sender_msg_layout;

        public MessageViewHolder(@NonNull View itemView) {

            super(itemView);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_msg_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_msg_text);
            recieverProfileImage = (CircleImageView) itemView.findViewById(R.id.msg_profile_image);
            senderMsgImage = (ImageView) itemView.findViewById(R.id.sender_msg_image);
            receicerMsgImage = (ImageView) itemView.findViewById(R.id.receiver_msg_Image);
            senderimageLayout=(LinearLayout)itemView.findViewById(R.id.senderImageLayout);
            receiverImageLayout=itemView.findViewById(R.id.receicreImagelayout);
            reciverMsgLayout=itemView.findViewById(R.id.reciverLayout);
            receiver_msg_time=itemView.findViewById(R.id.receiver_msg_time);
            sender_time_text=itemView.findViewById(R.id.sender_time_text);
            sender_msg_layout=itemView.findViewById(R.id.sender_msg_layout);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    iPersonalchat.longPressOnpersonalChat(userPersonalMsgList.get(getAdapterPosition()));
                    return true;
                }
            });


        }
    }

    public interface  IPersonalchat{
         void longPressOnpersonalChat(Messages messages);
    }

}
