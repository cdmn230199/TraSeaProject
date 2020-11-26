package com.example.TraSeApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.R;
import com.example.TraSeApp.model.Chat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MES_TYPE_LEFT = 0;
    public static final int MES_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    private Context context;
    private List<Chat> chats;
    private String imageurl;

    public MessageAdapter(Context context, List<Chat> chats, String imageurl) {
        this.context = context;
        this.chats = chats;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MES_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.tv_message_chat.setText(chat.getMessage());

        Glide.with(context.getApplicationContext())
                .load(imageurl)
                .timeout(5000)
                .placeholder(R.drawable.ic_person_64x64)
                .into(holder.avt_item_chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message_chat;
        CircleImageView avt_item_chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_message_chat = itemView.findViewById(R.id.tv_message_chat);
            avt_item_chat = itemView.findViewById(R.id.avt_item_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MES_TYPE_RIGHT;
        } else {
            return MES_TYPE_LEFT;
        }
    }
}
