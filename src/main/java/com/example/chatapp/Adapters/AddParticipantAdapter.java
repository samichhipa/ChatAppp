package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Model.GroupMembers;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddParticipantAdapter extends RecyclerView.Adapter<AddParticipantAdapter.ViewHolder> {

    List<Users> usersList;
    int x;
    Context context;
    String group_id, my_designation;

    public AddParticipantAdapter(List<Users> usersList, Context context, String group_id, String designation) {
        this.usersList = usersList;
        this.context = context;
        this.group_id = group_id;
        this.my_designation = designation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_participant_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Users users = usersList.get(position);
        final String uid=users.getId();

        holder.participant_name.setText(users.getUsername());
        holder.participant_email.setText(users.getEmail());
        try {
            Picasso.get().load(users.getImage()).into(holder.participant_img);
        } catch (Exception e) {


        }
        IsParticipantExits(users, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups");
                reference.child(group_id).child("GroupMembers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String user_prev_role = "" + dataSnapshot.child("designation").getValue();


                            String[] options;

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Choose Option");
                            if (my_designation.equals("creator")) {

                                if (user_prev_role.equals("admin")) {

                                    options = new String[]{"Remove Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == 0) {

                                              removeAdmin(users);


                                            } else {

                                               removeGroupMember(users);


                                            }

                                        }
                                    }).show();


                                } else if (user_prev_role.equals("participant")) {

                                    options = new String[]{"Make Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == 0) {

                                              makeAdmin(users);

                                            } else {

                                              removeGroupMember(users);



                                            }


                                        }
                                    }).show();


                                }


                            }

                            else if (my_designation.equals("admin")){


                                if (user_prev_role.equals("creator")){

                                    Toast.makeText(context,"Group Creater",Toast.LENGTH_SHORT).show();

                                }else if (user_prev_role.equals("admin")){


                                    options = new String[]{"Remove Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == 0) {

                                                removeAdmin(users);


                                            } else {

                                                removeGroupMember(users);


                                            }

                                        }
                                    }).show();

                                }else if (my_designation.equals("participant")){



                                    options = new String[]{"Make Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == 0) {

                                                makeAdmin(users);

                                            } else {

                                                removeGroupMember(users);



                                            }


                                        }
                                    }).show();

                                }

                            }



                        }
                        else {


                            AlertDialog.Builder builder1=new AlertDialog.Builder(context);
                            builder1.setTitle("Add Participants")
                                    .setMessage("Add User in this Group")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            addParticipant(users);

                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                }
                            }).show();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    private void removeGroupMember(Users users) {

        FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("GroupMembers").child(users.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void makeAdmin(Users users) {

        HashMap<String, Object> data=new HashMap<>();
        data.put("designation","admin");

        FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("GroupMembers").child(users.getId()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context,"The User is Now Admin...",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();



            }
        });
    }

    private void addParticipant(Users users) {

        String timestamp=String.valueOf(System.currentTimeMillis());

        Map<String,String> data=new HashMap<>();
        data.put("user_id",users.getId());
        data.put("designation","participant");
        data.put("timestamp",timestamp);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child("Groups").child(group_id).child("GroupMembers").child(users.getId()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context,"Added Successfully...",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removeAdmin(Users users) {


        HashMap<String, Object> data=new HashMap<>();
        data.put("designation","participant");

        FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("GroupMembers").child(users.getId()).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context,"The User is no longer admin...",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();



            }
        });




    }

    private void IsParticipantExits(Users users, final ViewHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("GroupMembers");
        reference.child(users.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String designation = "" + dataSnapshot.child("designation").getValue();
                    holder.participant_designation.setText(designation);


                } else {

                    holder.participant_designation.setText("");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView participant_img;
        TextView participant_name, participant_email, participant_designation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            participant_img = itemView.findViewById(R.id.participant_img);
            participant_name = itemView.findViewById(R.id.participant_name);
            participant_email = itemView.findViewById(R.id.participant_email);
            participant_designation = itemView.findViewById(R.id.participant_designation);


        }
    }
}
