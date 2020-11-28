package com.example.TraSeApp.fragments;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.ChangePassActivity;
import com.example.TraSeApp.EditProfileActivity;
import com.example.TraSeApp.LogInActivity;
import com.example.TraSeApp.R;
import com.example.TraSeApp.adapter.ProfileStorageAdapter;
import com.example.TraSeApp.model.Post;
import com.example.TraSeApp.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFrag extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    Button btnEditProfile;
    FirebaseUser firebaseUser;
    TextView tvUserProfile, tvUserNameProfile, tvBioUser, tvCountPost, tvCountFollower, tvCountFollowing;
    String profileid;
    ImageView iv_aVaProfile;
    ProfileStorageAdapter adapter;
    List<Post> postList;

    LinearLayout ln_profile;

    ImageView setting;

    RecyclerView rvStorageImage;

    public ProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("caches", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        ln_profile = view.findViewById(R.id.ln_profile);
        registerForContextMenu(ln_profile);
        setHasOptionsMenu(true);

        setting = view.findViewById(R.id.ivSetting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.setting_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.change_pass:
                Intent intent2 = new Intent(getContext(), ChangePassActivity.class);
                startActivity(intent2);
                return true;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LogInActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;

        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        init(view);

        userInfo();
        getFollowers();
        getNrPosts();
        myFoto();
        if (profileid.equals(firebaseUser.getUid())){
            btnEditProfile.setText("Edit Profile");
        }else{
            checkFollow();
        }

    }

    private void init(View view) {

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(this);


        tvCountPost = view.findViewById(R.id.tvCountPost);
        tvCountFollower = view.findViewById(R.id.tvCountFollower);
        tvCountFollowing = view.findViewById(R.id.tvCountFollowing);

        tvUserProfile = view.findViewById(R.id.tvUserProfile);
        tvUserNameProfile = view.findViewById(R.id.tvUserNameProfile);
        tvBioUser = view.findViewById(R.id.tvBioUser);
        iv_aVaProfile = view.findViewById(R.id.iv_AvaProfile);

        rvStorageImage = view.findViewById(R.id.rv_storageImage);
        rvStorageImage.setHasFixedSize(true);
        rvStorageImage.setLayoutManager(new GridLayoutManager(getContext(),3));
        postList = new ArrayList<>();
        adapter = new ProfileStorageAdapter(getContext(), postList);
        rvStorageImage.setAdapter(adapter);




    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEditProfile:
                String btn = btnEditProfile.getText().toString();
                if (btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else if(btn.equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("following").child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("following").child(firebaseUser.getUid()).setValue(true);
                }
                break;
        }
    }



    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                if (getContext() == null){
                    return;
                }
                User user =  dataSnapShot.getValue(User.class);

                Glide.with(getContext()).load(user.getImgUrl()).into(iv_aVaProfile);

                tvUserProfile.setText(user.getFullname());
                tvUserNameProfile.setText(user.getUsername());
                tvBioUser.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                if (dataSnapShot.child(profileid).exists()){
                    btnEditProfile.setText("following");
                }else
                    btnEditProfile.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                tvCountFollower.setText(""+ dataSnapShot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                tvCountFollowing.setText(""+ dataSnapShot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                tvCountPost.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myFoto(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}