package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chatapp.HomeFragments.ChatFragment;
import com.example.chatapp.HomeFragments.FriendFragment;
import com.example.chatapp.HomeFragments.GroupsFragment;
import com.example.chatapp.HomeFragments.FindFriendFragment;
import com.example.chatapp.HomeFragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {


BottomNavigationView bottomNavigationView;

ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        bottomNavigationView=findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        actionBar=getSupportActionBar();
        actionBar.setTitle("Friends");

        FriendFragment friendFragment=new FriendFragment();
        setFragment(friendFragment);

    }

    public void setFragment(Fragment fragment){


        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment,fragment);
        fragmentTransaction.commit();

    }

public BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()){

            case R.id.nav_chat:
                actionBar.setTitle("Chat");
                ChatFragment chatFragment=new ChatFragment();
                setFragment(chatFragment);

              return true;

            case R.id.nav_friend:
                actionBar.setTitle("Friends");
                FriendFragment friendFragment=new FriendFragment();
                setFragment(friendFragment);

                return true;

            case R.id.nav_find_friend:
                actionBar.setTitle("Find Friend");
                FindFriendFragment findFriendFragment =new FindFriendFragment();
                setFragment(findFriendFragment);
                return true;

            case R.id.nav_settings:
                actionBar.setTitle("Settings");
                SettingFragment settingFragment=new SettingFragment();
                setFragment(settingFragment);
                return true;

            case R.id.nav_groups:
                actionBar.setTitle("Groups");
                GroupsFragment groupsFragment=new GroupsFragment();
                setFragment(groupsFragment);
                return true;




        }


        return false;
    }
};

}
