package com.example.chatapp.HomeFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapters.FriendsAdapter;
import com.example.chatapp.Adapters.UsersAdapter;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Friends;
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


public class FriendFragment extends Fragment {

    RecyclerView recyclerView;
    FriendsAdapter adapter;
    List<Friends> friendsList;
    DatabaseReference reference;
    FirebaseAuth auth;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frined, container, false);

        reference=FirebaseDatabase.getInstance().getReference().child("Friends");
        auth=FirebaseAuth.getInstance();

        recyclerView=view.findViewById(R.id.friends_recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        friendsList=new ArrayList<>();
        adapter=new FriendsAdapter(friendsList,getContext());
        recyclerView.setAdapter(adapter);
        getFriends();




        return view;
    }

    private void getFriends() {

        reference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Friends friends=snapshot.getValue(Friends.class);

                    friendsList.add(friends);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
