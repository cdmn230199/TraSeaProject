package com.example.TraSeApp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.R;
import com.example.TraSeApp.model.Comment;
import com.example.TraSeApp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> commentList;
    private String postid;
    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context mContext, List<Comment> commentList,String postid) {
        this.mContext = mContext;
        this.commentList = commentList;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment cmt = commentList.get(position);

        holder.tv_cmt_content_item.setText(cmt.getComment());
        getUserInfo(holder.iv_avt_cmt_item,holder.tv_username_cmt_item,cmt.getPublisher());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (cmt.getPublisher().equals(firebaseUser.getUid())) {

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postid).child(cmt.getCommentid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_avt_cmt_item;
        TextView tv_username_cmt_item, tv_cmt_content_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(@NonNull View itemView) {
            iv_avt_cmt_item = itemView.findViewById(R.id.iv_avt_cmt_item);
            tv_username_cmt_item = itemView.findViewById(R.id.tv_username_cmt_item);
            tv_cmt_content_item = itemView.findViewById(R.id.tv_cmt_content_item);
        }


    }

    private void getUserInfo(final CircleImageView avt,final TextView username, final String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext)
                        .load(user.getImgUrl())
                        .timeout(5000)
                        .placeholder(R.drawable.ic_person_64x64)
                        .into(avt);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





}