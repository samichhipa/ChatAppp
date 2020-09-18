package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatapp.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;
    MaterialEditText txt_name,txt_pass,txt_email;
    Button register;
    String imgUrl="https://firebasestorage.googleapis.com/v0/b/chatapp-67407.appspot.com/o/account.png?alt=media&token=f03565b7-a9c8-44df-8315-c3758b5b1777";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        txt_email=findViewById(R.id.txt_email_R);
        txt_name=findViewById(R.id.txt_name_R);
        txt_pass=findViewById(R.id.txt_pass_R);
        register=findViewById(R.id.registerBtn);

        auth=FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name,email,pass;
                name=txt_name.getText().toString();
                email=txt_email.getText().toString();
                pass=txt_pass.getText().toString();


                validation(name,email,pass);

            }
        });


    }

    private void validation(final String name, final String email, final String pass) {

        if (TextUtils.isEmpty(name)){
            txt_name.setError("Enter Name");

        }else if (TextUtils.isEmpty(email)){

            txt_email.setError("Enter Email");

        }else if (TextUtils.isEmpty(pass)){

            txt_pass.setError("Enter Password");
        }else{

            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){

                   Users users=new Users(email,name,imgUrl,auth.getCurrentUser().getUid(),pass,"Online","NoOne");

                   reference.child(auth.getCurrentUser().getUid()).setValue(users);
                   finish();

               }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });
        }
    }

}
