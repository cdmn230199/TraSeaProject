package com.example.TraSeApp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.R;
import com.example.TraSeApp.databinding.StorageItemBinding;
import com.example.TraSeApp.fragments.ProfileFrag;
import com.example.TraSeApp.model.Post;

import java.util.List;

public class ProfileStorageAdapter extends RecyclerView.Adapter<ProfileStorageAdapter.ViewHolder> {
    List<Post> posts;
    private Context context;

    public ProfileStorageAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.storage_item, parent, false  );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStorageImage;
        public ViewHolder(@NonNull View view) {
            super(view);


            ivStorageImage = view.findViewById(R.id.ivStorageImage);

        }

        public void bind(final Post post) {
            Glide.with(context).load(post.getPostimage()).into(ivStorageImage);

        }
    }
}
