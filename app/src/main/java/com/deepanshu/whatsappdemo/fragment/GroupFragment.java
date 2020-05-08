package com.deepanshu.whatsappdemo.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.deepanshu.whatsappdemo.activity.GroupChatActivity;
import com.deepanshu.whatsappdemo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String > list_of_group=new ArrayList<>();
    private DatabaseReference GroupRef;



    public GroupFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView= inflater.inflate(R.layout.group_fragment, container, false);
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");


        InitializationField();
        ReteiveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String CurrentGroupName=parent.getItemAtPosition(position).toString();
            Intent groupChatIntent=new Intent(getContext(), GroupChatActivity.class);
            groupChatIntent.putExtra("groupName",CurrentGroupName);
            //groupChatIntent.putExtra("totalMembers",list_of_group.size());

                startActivity(groupChatIntent);


            }
        });
        return groupFragmentView;

    }


    private void InitializationField() {
        listView=(ListView)groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,list_of_group);
         listView.setAdapter(arrayAdapter);
    }
    private void ReteiveAndDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_group.clear();
                list_of_group.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
