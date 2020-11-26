package com.example.TraSeApp.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.ChatScreenActivity;
import com.example.TraSeApp.MessageActivity;
import com.example.TraSeApp.R;
import com.example.TraSeApp.model.User;
import com.facebook.share.Share;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;


    public UserChatAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_mes_row,parent,false);
        return new UserChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_send_mess.setVisibility(View.VISIBLE);


        holder.tv_user_search_mess.setText(user.getUsername());
        holder.tv_email_search_mess.setText(user.getFullname());
        Glide.with(mContext.getApplicationContext())
                .load(user.getImgUrl())
                .timeout(5000)
                .placeholder(R.drawable.ic_person_64x64)
                .into(holder.avt_search_mess);


        if (user.getId().equals(firebaseUser.getUid())){
            holder.btn_send_mess.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("caches",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getId());
                editor.apply();
            }
        });

        holder.btn_send_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatScreenActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_user_search_mess, tv_email_search_mess;
        public CircleImageView avt_search_mess;
        public Button btn_send_mess;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_user_search_mess = itemView.findViewById(R.id.tv_user_search_mess);
            tv_email_search_mess = itemView.findViewById(R.id.tv_email_search_mess);
            avt_search_mess = itemView.findViewById(R.id.avt_search_mess);
            btn_send_mess = itemView.findViewById(R.id.btn_send_mess);

        }
    }

}

