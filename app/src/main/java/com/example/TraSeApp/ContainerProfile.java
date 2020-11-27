package com.example.TraSeApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.TraSeApp.fragments.ProfileFrag;

public class ContainerProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_profile);

        ProfileFrag profileFrag = new ProfileFrag();

        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        manager.add(R.id.layout_container, profileFrag);
        manager.commit();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}