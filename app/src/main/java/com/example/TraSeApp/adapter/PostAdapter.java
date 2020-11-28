package com.example.TraSeApp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.CommentsActivity;
import com.example.TraSeApp.ContainerProfile;
import com.example.TraSeApp.R;
import com.example.TraSeApp.fragments.ProfileFrag;
import com.example.TraSeApp.model.Post;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context mContext;
    public List<Post> mPost;
    public FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(mContext.getApplicationContext())
                .load(post.getPostimage())
                .timeout(5000)
                .placeholder(R.drawable.ic_person_64x64)
                .into(holder.iv_post);
        if (post.getDescription().equals("")) {
            holder.tv_description.setVisibility(View.GONE);
        } else {
            holder.tv_description.setVisibility(View.VISIBLE);
            holder.tv_description.setText(post.getDescription());
        }

        getPublisherInfo(holder.avt, holder.tv_username, post.getPublisher()); // Get avt, get username, get id publisher
        isLiked(post.getPostid(), holder.img_btn_like);
        nrLikes(holder.tv_like_counter, post.getPostid());
        isLiked(post.getPostid(), holder.img_btn_like); // Check like or not
        nrLikes(holder.tv_like_counter, post.getPostid()); // Like counters
        getCmtsCount(post.getPostid(), holder.tv_cmt_counter);

        holder.img_btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.img_btn_like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.img_btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("caches", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                Intent intent = new Intent(mContext, ContainerProfile.class);
                mContext.startActivity(intent);

//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frament_container, new ProfileFrag()).commit();
            }
        });

//        holder.iv_post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("caches", Context.MODE_PRIVATE).edit();
//                editor.putString("profileid", post.getPublisher());
//                editor.apply();
//
//                Intent intent = new Intent(mContext, ContainerProfile.class);
//                mContext.startActivity(intent);
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frament_container, new ProfileFrag()).commit();
//            }
//        });

        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("caches", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                Intent intent = new Intent(mContext, ContainerProfile.class);
                mContext.startActivity(intent);

//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frament_container, new ProfileFrag()).commit();
            }
        });

        holder.ic_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                editPost(post.getPostid());
                                return true;
                            case R.id.delete:
                                final String id = post.getPostid();
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(post.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    deleteNotifications(id, firebaseUser.getUid());
                                                }
                                            }
                                        });
                                return true;
                            case R.id.report:
                                Toast.makeText(mContext, "Reported", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avt;
        private TextView tv_username, tv_time, tv_like_counter, tv_cmt_counter, tv_description;
        private ImageView iv_post, ic_more;
        private ImageButton img_btn_like, img_btn_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avt = itemView.findViewById(R.id.avt);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_like_counter = itemView.findViewById(R.id.tv_like_counter);
            iv_post = itemView.findViewById(R.id.iv_post);
            img_btn_like = itemView.findViewById(R.id.img_btn_like);
            img_btn_comment = itemView.findViewById(R.id.img_btn_comment);
            tv_cmt_counter = itemView.findViewById(R.id.tv_cmt_counter);
            tv_description = itemView.findViewById(R.id.tv_description);
            ic_more = itemView.findViewById(R.id.ic_more);
        }
    }

    private void getPublisherInfo(final CircleImageView image_profile, final TextView username, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext)
                        .load(user.getImgUrl())
                        .timeout(5000)
                        .placeholder(R.drawable.tokuda)
                        .into(image_profile);


                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // GET CMT FROM DATABASE
    private void getCmtsCount(String postid, final TextView tv_cmt_counter) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_cmt_counter.setText(snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Set Event For Button Like + Check like or not by firebase database + set Tag For button like
    private void isLiked(final String postid, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_filled);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addNotification(String userid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // Dem Like Cho TextView Likes
    private void nrLikes(final TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void editPost(final String postid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}