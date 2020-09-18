package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    String user_id;
    TextView txt_username;
    Button btnSendReq;
    ImageView profile_image;
    DatabaseReference user_ref,friend_req_ref,friend_ref;
    String current_state="";
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_id=getIntent().getStringExtra("user_id");

        auth=FirebaseAuth.getInstance();

        txt_username=findViewById(R.id.profile_username);
        btnSendReq=findViewById(R.id.send_req_btn);
        profile_image=findViewById(R.id.prof_img);

        current_state="not_friend";

        user_ref= FirebaseDatabase.getInstance().getReference().child("Users");
        friend_req_ref= FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friend_ref=FirebaseDatabase.getInstance().getReference().child("Friends");
        user_ref.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Users users = dataSnapshot.getValue(Users.class);

                    Picasso.get().load(users.getImage()).into(profile_image);
                    txt_username.setText(users.getUsername());

                    friend_req_ref.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(user_id)){

                                String req_type=""+dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                if (req_type.equals("received")){

                                    current_state="request_received";
                                    btnSendReq.setText("Accept Friend Request");
                                }else if (req_type.equals("sent")){

                                    current_state="request_sent";
                                    btnSendReq.setText("Cancel Friend Request");
                                }

                            }else{

                                friend_ref.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(user_id)){

                                            current_state="friend";
                                            btnSendReq.setText("Unfriend");
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



                }else{

                    Toast.makeText(ProfileActivity.this, "Data Unavailable",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnSendReq.setEnabled(false);

                            // Not Friend //
                if (current_state.equals("not_friend"))
                {

                    friend_req_ref.child(auth.getCurrentUser().getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {

                                friend_req_ref.child(user_id).child(auth.getCurrentUser().getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {



                                        current_state="request_sent";
                                        btnSendReq.setText("Cancel Friend Request");

                                        Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }else{

                                Toast.makeText(ProfileActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }

                            btnSendReq.setEnabled(true);


                        }
                    });




                }

                if (current_state.equals("request_sent"))
                {
                    friend_req_ref.child(auth.getCurrentUser().getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            friend_req_ref.child(user_id).child(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    btnSendReq.setEnabled(true);
                                    current_state="not_friend";
                                    btnSendReq.setText("Send Friend Request");


                                }
                            });


                        }
                    });



                }


                       // request received//
                if (current_state.equals("request_received"))
                {

                    final String currentDate= DateFormat.getDateInstance().format(new Date());

                    HashMap<String, Object> hashMap=new HashMap<>();
                    hashMap.put("friend_id",user_id);
                    hashMap.put("date",currentDate);

                    friend_ref.child(auth.getCurrentUser().getUid()).child(user_id).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    HashMap<String, Object> data=new HashMap<>();
                                    data.put("friend_id",auth.getCurrentUser().getUid());
                                    data.put("date",currentDate);

                                    friend_ref.child(user_id).child(auth.getCurrentUser().getUid()).setValue(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            friend_req_ref.child(auth.getCurrentUser().getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    friend_req_ref.child(user_id).child(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            btnSendReq.setEnabled(true);
                                                            current_state="friend";
                                                            btnSendReq.setText("Unfriend ");


                                                        }
                                                    });


                                                }
                                            });


                                        }
                                    });

                                }
                            });



                }
                              // Unfriend //
                if (current_state.equals("friend"))
                {
                    friend_ref.child(auth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                friend_ref.child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        current_state="not_friend";
                                        btnSendReq.setText("Send Friend Request");

                                    }
                                });


                            }
                        }
                    });



                }


            }
        });



    }
}
