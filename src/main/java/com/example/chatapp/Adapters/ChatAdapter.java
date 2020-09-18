package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.BottomdeleteSheet;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    List<Chat> chatList;
    Context context;
    String imageUrl;
    FirebaseUser user;
    ChatAdapter.onItemLongClickListener mListener;

    public static final int LEFT_LAYOUT = 0;
    public static final int RIGHT_LAYOUT = 1;

    public ChatAdapter(List<Chat> chatList, Context context, String imageUrl) {
        this.chatList = chatList;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        Chat chat = chatList.get(position);

        if (chat.getSender().equals(Common.CurrentUser)) {

            return RIGHT_LAYOUT;
        } else {

            return LEFT_LAYOUT;
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RIGHT_LAYOUT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_bg_right, parent, false);
            return new ViewHolder(view);

        } else {


            View view = LayoutInflater.from(context).inflate(R.layout.chat_bg_left, parent, false);
            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Chat chat = chatList.get(position);

        if (chat.getMsg_type().equals("image")) {

            holder.document_msg.setVisibility(View.GONE);
            holder.txt_msg.setVisibility(View.GONE);
            holder.image_msg.setVisibility(View.VISIBLE);
            Picasso.get().load(chat.getMessage()).into(holder.image_msg);


        } else if (chat.getMsg_type().equals("text")) {

            holder.document_msg.setVisibility(View.GONE);
            holder.txt_msg.setVisibility(View.VISIBLE);
            holder.txt_msg.setText(chat.getMessage());
            holder.image_msg.setVisibility(View.GONE);
        } else if (chat.getMsg_type().equals("doc")) {

            holder.document_msg.setVisibility(View.VISIBLE);
            holder.txt_msg.setVisibility(View.GONE);
            holder.image_msg.setVisibility(View.GONE);

            holder.document_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                    context.startActivity(intent);
                }
            });


        }


        Picasso.get().load(imageUrl).into(holder.profileImg);


        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(chat.getTimeStamp()));

        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.txt_timestamp.setText(dateTime);


        Picasso.get().load(imageUrl).into(holder.profileImg);


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView txt_msg, txt_timestamp;
        CircleImageView profileImg;
        TextView txt_chat_isSeen;
        ImageView image_msg, document_msg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_msg = itemView.findViewById(R.id.text_msg);
            txt_timestamp = itemView.findViewById(R.id.chat_time);
            profileImg = itemView.findViewById(R.id.msg_profile_img);
            txt_chat_isSeen = itemView.findViewById(R.id.chat_isSeen);
            image_msg = itemView.findViewById(R.id.image_msg);
            document_msg = itemView.findViewById(R.id.doc_msg);

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

    public void setOnLongClickListener(ChatAdapter.onItemLongClickListener onLongClickListener) {

        mListener = onLongClickListener;


    }

}
