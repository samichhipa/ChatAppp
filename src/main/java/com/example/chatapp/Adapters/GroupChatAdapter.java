package com.example.chatapp.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.Model.Groups;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {



    public static final int LEFT_LAYOUT = 0;
    public static final int RIGHT_LAYOUT = 1;

    List<GroupChat> groupChatList;
    Context context;
    FirebaseUser user;
    GroupChatAdapter.onItemLongClickListener mListener;

    public GroupChatAdapter(List<GroupChat> groupChatList, Context context) {
        this.groupChatList = groupChatList;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        GroupChat groupChat = groupChatList.get(position);

        if (groupChat.getSender().equals(Common.CurrentUser)) {

            return RIGHT_LAYOUT;
        } else {

            return LEFT_LAYOUT;
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RIGHT_LAYOUT) {

            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
            return new GroupChatAdapter.ViewHolder(view);

        } else {


            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
            return new GroupChatAdapter.ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        GroupChat groupChat=groupChatList.get(position);


        setUserName(groupChat,holder);
        holder.txt_msg.setText(groupChat.getMessage());
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(groupChat.getTimestamp()));

        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.txt_timestamp.setText(dateTime);


    }

    private void setUserName(final GroupChat groupChat, final ViewHolder holder) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Users users=snapshot.getValue(Users.class);

                    if (users.getId().equals(groupChat.getSender())){

                        holder.txt_name.setText(users.getUsername());

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{


        TextView txt_name,txt_msg,txt_timestamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_name=itemView.findViewById(R.id.group_chat_sender_name);
            txt_msg=itemView.findViewById(R.id.group_chat_msg);
            txt_timestamp=itemView.findViewById(R.id.group_chat_timestamp);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            if (mListener != null) {

                int position = getAdapterPosition();

                mListener.onLongClick(position);
            }
            return false;
        }
    }

    public interface onItemLongClickListener {

        void onLongClick(int position);

    }

    public void setOnLongClickListener(GroupChatAdapter.onItemLongClickListener onLongClickListener) {

        mListener = onLongClickListener;


    }
}
