package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.chatapp.Adapters.AddParticipantAdapter;
import com.example.chatapp.Model.Groups;
import com.example.chatapp.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupParticipantAddActivity extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView recyclerView;
    AddParticipantAdapter adapter;
    String group_id;
    FirebaseAuth auth;
    String my_group_designation;
    List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant_add);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Add Participants");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        group_id=getIntent().getStringExtra("group_id");

        auth=FirebaseAuth.getInstance();


        recyclerView=findViewById(R.id.add_participants_recyclerview);


        
        
        loadGroupInfo();
        getAllUsers();






    }

    private void getAllUsers() {
        usersList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new AddParticipantAdapter(usersList,this,group_id,my_group_designation);

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

           usersList.clear();
           for (DataSnapshot snapshot:dataSnapshot.getChildren()){

               Users users=snapshot.getValue(Users.class);
               if (!auth.getCurrentUser().getUid().equals(users)){

                   usersList.add(users);

               }

           }

           recyclerView.setAdapter(adapter);
           adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGroupInfo() {

        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.orderByChild("groupID").equalTo(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Groups groups=snapshot.getValue(Groups.class);
                    final String groupTitle=groups.getGroup_name();
                    actionBar.setTitle("Add Participants");

                    reference.child(group_id).child("GroupMembers").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){

                                my_group_designation=""+dataSnapshot.child("designation").getValue();
                                actionBar.setTitle(groupTitle+"("+my_group_designation+")");


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

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
