package com.example.chatapp.HomeFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapters.UsersAdapter;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FindFriendFragment extends Fragment {



    RecyclerView recyclerView;
    UsersAdapter adapter;
    List<Users> usersList;
    DatabaseReference reference;
    FirebaseAuth auth;
    String myID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_friend, container, false);



        auth = FirebaseAuth.getInstance();
        myID=auth.getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.user_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersList = new ArrayList<>();
        adapter=new UsersAdapter(usersList,getActivity());
        recyclerView.setAdapter(adapter);


        getUsers();


        return view;
    }

    private void getUsers() {

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users users=snapshot.getValue(Users.class);
                    if (!users.getId().equals(Common.CurrentUser)) {
                        usersList.add(users);

                    }

                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
