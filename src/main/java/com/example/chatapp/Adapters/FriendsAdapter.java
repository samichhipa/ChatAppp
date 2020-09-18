package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.Model.Friends;
import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    List<Friends> friendsList;
    Context context;
    DatabaseReference reference;

    public FriendsAdapter(List<Friends> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.users_layout,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Friends friends=friendsList.get(position);

        final String id=friends.getFriend_id();


        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("image").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();

                    holder.username.setText(username);
                    Picasso.get().load(image).into(holder.img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("user_id",id);
                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img=itemView.findViewById(R.id.user_image);

            username=itemView.findViewById(R.id.Username);
        }
    }
}
