package com.example.TraSeApp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.CommentsActivity;
import com.example.TraSeApp.R;
import com.example.TraSeApp.fragments.ProfileFrag;
import com.example.TraSeApp.model.Post;
import com.example.TraSeApp.model.User;
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
        getCmtsCount(post.getPostid(),holder.tv_cmt_counter);

        holder.img_btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.img_btn_like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(),post.getPostid());
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

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frament_container, new ProfileFrag()).commit();
            }
        });

        holder.iv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("caches", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frament_container, new ProfileFrag()).commit();
            }
        });

        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("caches", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frament_container, new ProfileFrag()).commit();
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
        private ImageView iv_post;
        private ImageButton img_btn_like, img_btn_comment, img_btn_share;
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
    private void getCmtsCount(String postid , final TextView tv_cmt_counter) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_cmt_counter.setText(snapshot.getChildrenCount()+" comments");
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

    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
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
}