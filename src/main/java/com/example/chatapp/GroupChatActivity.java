package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.Adapters.GroupChatAdapter;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.Model.Groups;
import com.example.chatapp.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity implements BottomSheetDialog.ItemClickListener, GroupChatAdapter.onItemLongClickListener, BottomdeleteSheet.ItemClickListener{

    String group_id;
    String group_name, desc, group_img, group_creator, group_timestamp;

    TextView txt_group_name, txt_status;
    CircleImageView GroupprofileImage, sendBtn;
    EditText txt_msg;
    Toolbar toolbar;
    FirebaseAuth auth;
    DatabaseReference reference, group_ref;
    String myID, user_id, user_image,my_group_designation;

    ValueEventListener seenEventListener;
    DatabaseReference msg_ref;
    RecyclerView recyclerView;
    List<GroupChat> groupChatList;
    GroupChatAdapter adapter;
    Uri saveUri, pdfUri;
    ImageView attachBtn;
    ProgressDialog progressDialog;


    StorageReference storageReference;
    BottomSheetDialog bottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        toolbar = findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        if (getIntent() != null) {
            group_id = getIntent().getStringExtra("group_id");

        }

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        group_ref = FirebaseDatabase.getInstance().getReference().child("Groups");
        storageReference = FirebaseStorage.getInstance().getReference();


        txt_group_name = findViewById(R.id.group_chat_name);
        txt_status = findViewById(R.id.group_chat_desc);
        GroupprofileImage = findViewById(R.id.group_chat_profile);
        sendBtn = findViewById(R.id.group_chat_send_btn);
        txt_msg = findViewById(R.id.group_chat_text);
        attachBtn = findViewById(R.id.group_attach_btn);

        getGroupInfo();
        loadMyGroupDesignation();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = txt_msg.getText().toString().trim();

                if (TextUtils.isEmpty(msg)) {

                    txt_msg.setError("Can't send Empty Message");
                } else {
                    sendMsg(msg);

                }
            }
        });

        recyclerView = findViewById(R.id.group_chat_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        groupChatList = new ArrayList<>();
        adapter = new GroupChatAdapter(groupChatList, GroupChatActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnLongClickListener(this);

       getGroupChat();





    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.participants_menu,menu);
        menu.findItem(R.id.nav_add_participants).setVisible(false);

        if (my_group_designation.equals("creator") || my_group_designation.equals("admin"))
        {

            menu.findItem(R.id.nav_add_participants).setVisible(true);

        }else{

            menu.findItem(R.id.nav_add_participants).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.nav_add_participants){

            Intent intent=new Intent(this,GroupParticipantAddActivity.class);
            intent.putExtra("group_id",group_id);
            startActivity(intent);

        }


        if (id==R.id.nav_group_info){

            Intent intent=new Intent(this,GroupInfoActivity.class);
            intent.putExtra("group_id",group_id);
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }

    private void loadMyGroupDesignation() {
        FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("GroupMembers").orderByChild("user_id")
                .equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    my_group_designation=""+snapshot.child("designation").getValue();
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getGroupChat() {

DatabaseReference group_ref=FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id).child("GroupChat");

group_ref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        groupChatList.clear();

        for (DataSnapshot snapshot:dataSnapshot.getChildren()){

            GroupChat groupChat=snapshot.getValue(GroupChat.class);

                groupChatList.add(groupChat);

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});




    }

    private void sendMsg(String msg) {
        String id = reference.push().getKey();

        HashMap<String, Object> data = new HashMap<>();
        data.put("sender", Common.CurrentUser);
        data.put("message", msg);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        data.put("msg_type", "text");
        data.put("id", id);

        group_ref.child(group_id).child("GroupChat").child(id).setValue(data);

        txt_msg.setText("");



    }

    private void getGroupInfo() {

        group_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Groups groups = snapshot.getValue(Groups.class);

                    if (groups.getGroupID().equals(group_id)) {

                        group_name = groups.getGroup_name();
                        group_creator = groups.getCreator();
                        group_img = groups.getGroup_icon();
                        group_timestamp = groups.getTimestamp();
                        group_creator = groups.getCreator();

                        Picasso.get().load(group_img).into(GroupprofileImage);
                        txt_group_name.setText(group_name);
                    }

                    /*
                    if (users.getId().equals(user_id)) {

                        Picasso.get().load(users.getImage()).into(GroupprofileImage);
                        txt_username.setText(users.getUsername());
                        user_image = users.getImage();

                        String typing_status = users.getTyping_to();

                        if (typing_status.equals(Common.CurrentUser)) {

                            txt_status.setText("typing...");


                        } else {

                            String status = users.getOnline_status();
                            if (users.getOnline_status().equals("Online")) {
                                txt_status.setText(status);
                            } else {

                                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                                calendar.setTimeInMillis(Long.parseLong(status));

                                String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

                                txt_status.setText("Last Seen at: " + dateTime);
                            }
                        }


                    }
                    */
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLongClick(int position) {

        BottomdeleteSheet bottomdeleteSheet = new BottomdeleteSheet();
        bottomdeleteSheet.show(getSupportFragmentManager(), "asdd");
        GroupChat chat = groupChatList.get(position);
        Common.delete_id = chat.getId();
        Common.TimeStamp = chat.getId();

    }

    @Override
    public void onItemClick(String item) {

        Toast.makeText(GroupChatActivity.this, item, Toast.LENGTH_LONG).show();
        bottomSheetDialogFragment.dismiss();

        if (item.equals("image")) {

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);

        } else if (item.equals("doc")) {

            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);

        }

    }

    @Override
    public void onItemClick() {

        deleteMsg(Common.delete_id);
    }

    private void deleteMsg(final String msg_id) {


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getID().equals(msg_id)) {


                        if (chat.getSender().equals(Common.CurrentUser)) {


                            HashMap<String, Object> data = new HashMap<>();
                            data.put("Message", "Message Deleted");
                            data.put("msg_type", "text");
                            reference.child(msg_id).updateChildren(data);
                            Toast.makeText(GroupChatActivity.this, "msg deleted ", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(GroupChatActivity.this, "You can't delete ", Toast.LENGTH_LONG).show();
                        }

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
