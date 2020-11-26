package com.example.TraSeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.TraSeApp.adapter.CommentsAdapter;
import com.example.TraSeApp.model.Comment;
import com.example.TraSeApp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    RecyclerView rv_cmt;
    List<Comment> comments;
    CommentsAdapter commentsAdapter;

    EditText edt_cmt;
    ImageView iv_avt_cmt;
    TextView tv_post_cmt;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edt_cmt = findViewById(R.id.edt_cmt);
        iv_avt_cmt = findViewById(R.id.iv_avt_cmt);
        tv_post_cmt = findViewById(R.id.tv_post_cmt);
        rv_cmt = findViewById(R.id.rv_cmt);

        rv_cmt.setHasFixedSize(true);
        rv_cmt.setLayoutManager(new LinearLayoutManager(this));
        comments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, comments);
        rv_cmt.setAdapter(commentsAdapter);


        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        // Xử lý khi nhấn nút textview post
        tv_post_cmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_cmt.getText().toString().equals("")) {
                    Toast.makeText(CommentsActivity.this, "Please write something", Toast.LENGTH_SHORT).show();
                } else {
                    addCmt();
                }
            }
        });

        getAvtCmt();
        readComments();

    }

    // Add Comment Vao HashMap Roi Quang Du Lieu Len Firebase Gom ( cmt + UID cua nguoi cmt)
    private void addCmt() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", edt_cmt.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);

        edt_cmt.setText("");
    }

    private void getAvtCmt() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Glide.with(getApplicationContext())
                        .load(user.getImgUrl())
                        .timeout(5000)
                        .placeholder(R.drawable.ic_person_64x64)
                        .into(iv_avt_cmt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}