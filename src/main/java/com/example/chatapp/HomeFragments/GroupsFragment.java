package com.example.chatapp.HomeFragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.Adapters.GroupsAdapter;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Groups;
import com.example.chatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupsFragment extends Fragment {

    EditText txt_dialog_group_name, txt_dialog_group_desc;
    Button btnCreateGroup;
    CircleImageView dialog_group_image;
    DatabaseReference reference;
    StorageReference storageReference;
    Uri saveUri;
    ProgressDialog progressDialog;
    Groups newGroup;
    String timestamp;
    FloatingActionButton add_group_btn;


    RecyclerView recyclerView;
    GroupsAdapter adapter;
    List<Groups> groupsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        progressDialog = new ProgressDialog(getContext());

        timestamp = String.valueOf(System.currentTimeMillis());

        reference = FirebaseDatabase.getInstance().getReference().child("Groups");
        storageReference = FirebaseStorage.getInstance().getReference();


        add_group_btn = view.findViewById(R.id.creat_new_group);

        add_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();

            }
        });


        recyclerView = view.findViewById(R.id.groups_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        groupsList = new ArrayList<>();
        adapter = new GroupsAdapter(groupsList, getContext());
        getGroups();



        return view;
    }

    private void getGroups() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groupsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.child("GroupMembers").child(Common.CurrentUser).exists()) {

                        Groups groups = snapshot.getValue(Groups.class);

                        groupsList.add(groups);

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

    private void showDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        LayoutInflater layoutInflater = this.getLayoutInflater();
        View add_group_layout = layoutInflater.inflate(R.layout.add_group_layout, null);

        builder.setView(add_group_layout);

        txt_dialog_group_name = add_group_layout.findViewById(R.id.group_name);
        txt_dialog_group_desc = add_group_layout.findViewById(R.id.group_desc);
        dialog_group_image = add_group_layout.findViewById(R.id.group_img);
        btnCreateGroup = add_group_layout.findViewById(R.id.group_create_btn);


        final AlertDialog alertDialog = builder.create();

        dialog_group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();

            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();
                alertDialog.dismiss();

            }
        });


        alertDialog.show();

    }

    private void uploadImage() {

        progressDialog.setMessage("Creating Group...");
        progressDialog.show();
        if (saveUri != null) {


            if (TextUtils.isEmpty(txt_dialog_group_name.getText().toString())) {
                txt_dialog_group_name.setError("Enter Name");
                progressDialog.dismiss();

            } else {

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

                            newGroup = new Groups();
                            newGroup.setCreator(Common.CurrentUser);
                            Picasso.get().load(myUrl).into(dialog_group_image);

                            if (TextUtils.isEmpty(txt_dialog_group_desc.getText().toString())) {
                                txt_dialog_group_desc.setText("");
                            } else {

                                newGroup.setGroup_description(txt_dialog_group_desc.getText().toString());
                            }

                            newGroup.setGroup_icon(myUrl);
                            newGroup.setGroup_name(txt_dialog_group_name.getText().toString());
                            newGroup.setGroupID(timestamp);
                            newGroup.setTimestamp(timestamp);


                            if (newGroup != null) {

                                reference.child(timestamp).setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("designation", "creator");
                                            hashMap.put("timestamp", timestamp);
                                            hashMap.put("user_id", Common.CurrentUser);

                                            DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(timestamp).child("GroupMembers");

                                            db_ref.child(Common.CurrentUser).setValue(hashMap);


                                        }


                                    }
                                });
                                txt_dialog_group_name.setText("");
                                txt_dialog_group_desc.setText("");
                                saveUri = null;
                                progressDialog.dismiss();

                            }


                            progressDialog.dismiss();


                        } else {


                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            }

        } else {

            progressDialog.dismiss();
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_LONG).show();

        }
    }

    private String getExtensionFile(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void chooseImage() {

        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(GroupsFragment.this.getContext(), GroupsFragment.this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 203) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                saveUri = result.getUri();
                //Glide.with(getContext()).load(this.imageUri).into(this.item_image);
            } else if (resultCode == 204) {
                Toast.makeText(getContext(), result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
