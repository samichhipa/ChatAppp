package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.AddParticipantAdapter;
import com.example.chatapp.Model.GroupMembers;
import com.example.chatapp.Model.Groups;
import com.example.chatapp.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupInfoActivity extends AppCompatActivity {

    String group_id;
    ActionBar actionBar;
    String my_designation = "";

    TextView edit_group, add_participant, leave_group, no_of_participants, group_description, group_creator;
    ImageView group_image;
    RecyclerView recyclerView;
    FirebaseAuth auth;

    List<Users> usersList;
    AddParticipantAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        edit_group = findViewById(R.id.g_i_edt_grp);
        add_participant = findViewById(R.id.g_i_add_participant);
        leave_group = findViewById(R.id.g_i_leave_grp);
        no_of_participants = findViewById(R.id.g_i_no_of_participants);
        group_description = findViewById(R.id.g_i_descrip_grp);
        group_creator = findViewById(R.id.g_i_created_by);

        group_image = findViewById(R.id.g_i_group_img);


        group_id = getIntent().getStringExtra("group_id");

        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.group_info_reyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersList = new ArrayList<>();
        GroupInfo();
        MyDesignation();

        edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(GroupInfoActivity.this,GroupEditActivity.class);
                intent.putExtra("group_id",group_id);
                startActivity(intent);

            }
        });

        add_participant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(GroupInfoActivity.this,GroupParticipantAddActivity.class);
                intent.putExtra("group_id",group_id);
                startActivity(intent);

            }
        });

        leave_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialog_title="";
                String dialog_description="";
                String positiveBtnTitle="";
                if (my_designation.equals("creator")){


                    dialog_title="Delete Group";
                    dialog_description="Are You Want Really want to Delete Group Permanently?";
                    positiveBtnTitle="Delete";

                }else{
                    dialog_title="Leave Group";
                    dialog_description="Are You Want Really want to Leave Group Permanently?";
                    positiveBtnTitle="Leave";


                }

                AlertDialog.Builder builder=new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dialog_title)
                        .setMessage(dialog_description)
                        .setPositiveButton(positiveBtnTitle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (my_designation.equals("creator"))
                                {

                                    deleteGroup();

                                }else{

                                    leaveGroup();

                                }


                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }).show();


            }
        });


    }

    private void leaveGroup() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.child(group_id).child("GroupMembers").child(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                Toast.makeText(GroupInfoActivity.this,"Group Left Successfully...",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(GroupInfoActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(GroupInfoActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteGroup() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.child(group_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(GroupInfoActivity.this,"Group Left Successfully...",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(GroupInfoActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(GroupInfoActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void MyDesignation() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups");

        reference.child(group_id).child("GroupMembers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupMembers groupMembers = snapshot.getValue(GroupMembers.class);
                    if (groupMembers.getUser_id().equals(auth.getCurrentUser().getUid())) {

                        my_designation = groupMembers.getDesignation();
                        actionBar.setSubtitle(auth.getCurrentUser().getDisplayName() + " (" + my_designation + ")");


                        if (my_designation.equals("participant")) {
                            edit_group.setVisibility(View.GONE);
                            add_participant.setVisibility(View.GONE);
                            leave_group.setText("Leave Group");
                        } else if (my_designation.equals("admin")) {
                            edit_group.setVisibility(View.GONE);
                            add_participant.setVisibility(View.VISIBLE);
                            leave_group.setText("Leave Group");

                        } else if (my_designation.equals("creator")) {

                            edit_group.setVisibility(View.VISIBLE);
                            add_participant.setVisibility(View.VISIBLE);
                            leave_group.setText("Delete Group");

                        }
                    }

                    loadParticipant();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadParticipant() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.child(group_id).child("GroupMembers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final GroupMembers groupMembers = snapshot.getValue(GroupMembers.class);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                Users users = snapshot1.getValue(Users.class);
                                if (users.getId().equals(groupMembers.getUser_id())) {

                                    usersList.add(users);
                                }

                            }
                            adapter = new AddParticipantAdapter(usersList, GroupInfoActivity.this, group_id, my_designation);
                            recyclerView.setAdapter(adapter);
                           no_of_participants.setText("("+usersList.size()+")");
                            adapter.notifyDataSetChanged();


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

    private void GroupInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Groups groups = snapshot.getValue(Groups.class);
                    if (groups.getGroupID().equals(group_id)) {

                        String created_by, dateTimee;

                        created_by = groups.getCreator();
                        dateTimee = groups.getTimestamp();

                        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                        calendar.setTimeInMillis(Long.parseLong(dateTimee));

                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


                        loadCreatorInfo(created_by, dateTime);


                        actionBar.setTitle(groups.getGroup_name());
                        group_description.setText(groups.getGroup_description());

                        try {

                            Picasso.get().load(groups.getGroup_icon()).placeholder(R.drawable.profile).into(group_image);

                        } catch (Exception e) {
                            group_image.setImageResource(R.drawable.profile);
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCreatorInfo(final String created_by, final String dateTime) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users users = snapshot.getValue(Users.class);
                    if (users.getId().equals(created_by)) {

                        group_creator.setText("Created By" + users.getUsername() + "on" + dateTime);


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
