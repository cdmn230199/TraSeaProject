package com.example.TraSeApp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.TraSeApp.MainActivity;
import com.example.TraSeApp.PostActivity;
import com.example.TraSeApp.R;
import com.example.TraSeApp.adapter.UserAdapter;
import com.example.TraSeApp.adapter.UserChatAdapter;
import com.example.TraSeApp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearchUserMessage extends Fragment {

    EditText edt_search_mess;
    RecyclerView rv_search_mess;
    UserChatAdapter userChatAdapter;

    private List<User> users;

    public FragmentSearchUserMessage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_search_mess = view.findViewById(R.id.rv_search_mes);
        edt_search_mess = getView().findViewById(R.id.edt_search_mess);
        
        rv_search_mess.setHasFixedSize(true);
        rv_search_mess.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();
        userChatAdapter = new UserChatAdapter(getContext(), users);
        rv_search_mess.setAdapter(userChatAdapter);

        readUsers();

        if(users == null){
            Toast.makeText(getContext(), "Nobody", Toast.LENGTH_SHORT).show();
        }

        edt_search_mess.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
    }

    private void searchUsers(String user) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(user)
                .endAt(user + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }

                userChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (edt_search_mess.getText().toString().equals("")) {
                    users.clear();
                    for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        users.add(user);
                    }
                    userChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}