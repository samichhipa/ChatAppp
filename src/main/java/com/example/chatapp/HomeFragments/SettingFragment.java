package com.example.chatapp.HomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chatapp.LoginActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {


    Button btnSignOut;
    FirebaseAuth auth;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view= inflater.inflate(R.layout.fragment_setting, container, false);


       auth=FirebaseAuth.getInstance();
       btnSignOut=view.findViewById(R.id.signout);

       btnSignOut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               auth.signOut();
               Intent intent=new Intent(getActivity(), LoginActivity.class);
               startActivity(intent);
               getActivity().finish();
           }
       });


        return view;
    }
}
