package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements BottomSheetDialog.ItemClickListener, ChatAdapter.onItemLongClickListener, BottomdeleteSheet.ItemClickListener {

    TextView txt_username, txt_status;
    CircleImageView profileImage, sendBtn;
    EditText txt_msg;
    Toolbar toolbar;
    FirebaseAuth auth;
    DatabaseReference reference, user_ref;
    String myID, user_id, user_image;

    ValueEventListener seenEventListener;
    DatabaseReference msg_ref;
    RecyclerView recyclerView;
    List<Chat> chatList;
    ChatAdapter adapter;
    Uri saveUri, pdfUri;
    ImageView attachBtn;
    ProgressDialog progressDialog;


    StorageReference storageReference;
    BottomSheetDialog bottomSheetDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        if (getIntent() != null) {

            user_id = getIntent().getStringExtra("user_id");
        }

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();


        txt_username = findViewById(R.id.chat_username);
        txt_status = findViewById(R.id.user_status);
        profileImage = findViewById(R.id.chat_user_profile);
        sendBtn = findViewById(R.id.chat_send_btn);
        txt_msg = findViewById(R.id.chat_text);
        attachBtn = findViewById(R.id.attach_btn);


        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialogFragment = new BottomSheetDialog();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "asd");


            }
        });

        getUserInfo();

        recyclerView = findViewById(R.id.chat_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, ChatActivity.this, user_image);
        recyclerView.setAdapter(adapter);

        adapter.setOnLongClickListener(this);

        getMessages();

        seenMessages();


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


        txt_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() == 0) {
                    checkTypingStatus("NoOne");
                } else {
                    checkTypingStatus(user_id);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


    }


    private void uploadImage() {

        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if (saveUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getExtensionFile(saveUri));

            UploadTask uploadTask = fileReference.putFile(saveUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isComplete()) {

                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        String id = reference.push().getKey();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("Sender", Common.CurrentUser);
                        data.put("Receiver", user_id);
                        data.put("Message", myUrl);
                        data.put("TimeStamp", String.valueOf(System.currentTimeMillis()));
                        data.put("isSeen", false);
                        data.put("msg_type", "image");
                        data.put("ID", id);

                        reference.child(id).setValue(data);

                        txt_msg.setText("");

                        progressDialog.dismiss();


                        //pd.dismiss();


                    } else {

                        // pd.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });


        } else {
            //pd.dismiss();
            progressDialog.dismiss();
            Toast.makeText(ChatActivity.this, "No Image Selected", Toast.LENGTH_LONG).show();


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 203) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                saveUri = result.getUri();

                uploadImage();
                //Glide.with(getContext()).load(this.imageUri).into(this.item_image);
            } else if (resultCode == 204) {
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            pdfUri = data.getData();

            uploadPdf();

        } else {
            Toast.makeText(this, "Please Select File", Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadPdf() {


        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if (pdfUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getExtensionFile(pdfUri));

            UploadTask uploadTask = fileReference.putFile(pdfUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isComplete()) {

                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        String id = reference.push().getKey();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("Sender", Common.CurrentUser);
                        data.put("Receiver", user_id);
                        data.put("Message", myUrl);
                        data.put("TimeStamp", String.valueOf(System.currentTimeMillis()));
                        data.put("isSeen", false);
                        data.put("msg_type", "doc");
                        data.put("ID", id);

                        reference.child(id).setValue(data);

                        txt_msg.setText("");

                        progressDialog.dismiss();


                        //pd.dismiss();


                    } else {

                        // pd.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });


        } else {

            progressDialog.dismiss();
            Toast.makeText(ChatActivity.this, "No File Selected", Toast.LENGTH_LONG).show();


        }
    }


    private String getExtensionFile(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    protected void onStart() {

        //checkUserStatus();
        checkOnlineStatus("Online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkTypingStatus("NoOne");
        checkOnlineStatus(String.valueOf(System.currentTimeMillis()));
    }

    @Override
    protected void onResume() {

        checkOnlineStatus("Online");
        super.onResume();
    }

    private void seenMessages() {

        msg_ref = FirebaseDatabase.getInstance().getReference().child("Chats");

        seenEventListener = msg_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(user_id) && chat.getReceiver().equals(Common.CurrentUser)) {

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("isSeen", true);
                        msg_ref.child(chat.getID()).updateChildren(data);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMessages() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(Common.CurrentUser) && chat.getSender().equals(user_id) || chat.getReceiver().equals(user_id) && chat.getSender().equals(Common.CurrentUser)) {

                        chatList.add(chat);

                    }
                }


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOnlineStatus(String status) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Common.CurrentUser);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online_status", status);
        ref.updateChildren(hashMap);


    }

    private void checkTypingStatus(String typing) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Common.CurrentUser);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typing_to", typing);
        ref.updateChildren(hashMap);


    }

    private void sendMsg(String msg) {
        String id = reference.push().getKey();

        HashMap<String, Object> data = new HashMap<>();
        data.put("Sender", Common.CurrentUser);
        data.put("Receiver", user_id);
        data.put("Message", msg);
        data.put("TimeStamp", String.valueOf(System.currentTimeMillis()));
        data.put("isSeen", false);
        data.put("msg_type", "text");
        data.put("ID", id);

        reference.child(id).setValue(data);

        txt_msg.setText("");

    }

    private void getUserInfo() {

        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users users = snapshot.getValue(Users.class);
                    if (users.getId().equals(user_id)) {

                        Picasso.get().load(users.getImage()).into(profileImage);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkUserStatus() {


        if (!Common.CurrentUser.equals(null)) {


        } else if (Common.CurrentUser.equals(null)) {

            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
            finish();

        }


    }

    @Override
    public void onItemClick(String item) {


        Toast.makeText(ChatActivity.this, item, Toast.LENGTH_LONG).show();
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
    public void onLongClick(int position) {

        BottomdeleteSheet bottomdeleteSheet = new BottomdeleteSheet();
        bottomdeleteSheet.show(getSupportFragmentManager(), "asdd");
        Chat chat = chatList.get(position);
        Common.delete_id = chat.getID();
        Common.TimeStamp = chat.getTimeStamp();


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
                            Toast.makeText(ChatActivity.this, "msg deleted ", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(ChatActivity.this, "You can't delete ", Toast.LENGTH_LONG).show();
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
