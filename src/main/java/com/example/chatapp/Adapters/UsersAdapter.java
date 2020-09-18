package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.Model.Users;
import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {


    List<Users> usersList;
    Context context;

    public UsersAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.users_layout,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final Users users=usersList.get(position);

        Picasso.get().load(users.getImage()).into(holder.image);
        holder.txt_name.setText(users.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, ProfileActivity.class);
                intent.putExtra("user_id",users.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_name;
        CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_name=itemView.findViewById(R.id.Username);
            image=itemView.findViewById(R.id.user_image);


        }
    }
}
