package com.example.TraSeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.TraSeApp.adapter.MessageAdapter;
import com.example.TraSeApp.model.Chat;
import com.example.TraSeApp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreenActivity extends AppCompatActivity {

    CircleImageView avt_chat_screen;
    TextView tv_username_chat_screen;

    EditText edt_message;
    ImageView iv_send_message;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<Chat> chats;
    RecyclerView rv_display_chat;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);


        Toolbar toolbar = findViewById(R.id.chat_screen_toolbar);

        initView();

        rv_display_chat.setHasFixedSize(true);
        rv_display_chat.setLayoutManager(new LinearLayoutManager(ChatScreenActivity.this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get username  message receiver and display name, avt
        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tv_username_chat_screen.setText(user.getUsername());

                Glide.with(ChatScreenActivity.this)
                        .load(user.getImgUrl())
                        .timeout(5000)
                        .placeholder(R.drawable.ic_person_64x64)
                        .into(avt_chat_screen);

                readMessage(firebaseUser.getUid(), userid, user.getImgUrl());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // handle message when empty
        edt_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_message.getText().toString().isEmpty()) {
                    iv_send_message.setVisibility(View.INVISIBLE);
                } else {
                    iv_send_message.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        iv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mes = edt_message.getText().toString();
                sendMessage(firebaseUser.getUid(), userid, mes);
                edt_message.setText("");
            }
        });


    }

    private void initView() {
        avt_chat_screen = findViewById(R.id.avt_chat_screen);
        tv_username_chat_screen = findViewById(R.id.tv_username_chat_screen);
        edt_message = findViewById(R.id.edt_message);
        iv_send_message = findViewById(R.id.iv_send_message);
        rv_display_chat = findViewById(R.id.rv_display_chat);
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid, final String userid, final String imgurl) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        chats = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        chats.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatScreenActivity.this, chats, imgurl);
                    rv_display_chat.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}