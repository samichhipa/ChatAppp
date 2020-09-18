package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.GroupChatActivity;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.Model.Groups;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.nio.file.LinkPermission;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    List<Groups> groupsList;
    Context context;

    public GroupsAdapter(List<Groups> groupsList, Context context) {
        this.groupsList = groupsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.groups_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final Groups groups=groupsList.get(position);


        holder.group_last_msg_sender.setText("");
        holder.group_last_msg.setText("");
        holder.txt_timestamp.setText("");

        setLastMessage(groups,holder);

        holder.group_name.setText(groups.getGroup_name());
        try {
            Picasso.get().load(groups.getGroup_icon()).into(holder.group_img);
        }
        catch (Exception e){

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, GroupChatActivity.class);
                intent.putExtra("group_id",groups.getGroupID());
                context.startActivity(intent);
            }
        });

    }

    private void setLastMessage(Groups groups, final ViewHolder holder) {


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Groups").child(groups.getGroupID()).child("GroupChat");

        reference.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    GroupChat groupChat=snapshot.getValue(GroupChat.class);

                   holder.group_last_msg.setText(groupChat.getMessage());

                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(groupChat.getMessage()));

                    String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    holder.txt_timestamp.setText(dateTime);

                    final String sender_id=groupChat.getSender();

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                                Users users=snapshot.getValue(Users.class);

                                if (users.getId().equals(sender_id)){

                                    holder.group_last_msg_sender.setText(users.getUsername());

                                }


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
    }



    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView group_img;
        TextView group_name,group_last_msg_sender,group_last_msg,txt_timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            group_name=itemView.findViewById(R.id.groupp_name);
            group_last_msg=itemView.findViewById(R.id.group_last_msg);
            txt_timestamp=itemView.findViewById(R.id.group_timestamp);
            group_last_msg_sender=itemView.findViewById(R.id.group_last_msg_sender);
        }
    }
}
