package com.example.TraSeApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.TraSeApp.adapter.ViewPagerHomeAdapter;
import com.example.TraSeApp.adapter.ViewPagerMessageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

public class MessageActivity extends AppCompatActivity {

    private TabLayout tab_layout_mess;
    private ViewPager vp_mess;
    ViewPagerMessageAdapter viewPagerAdapter;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();
        addTabs();

    }

    private void initView() {
        tab_layout_mess = findViewById(R.id.tab_layout_mess);
        vp_mess = findViewById(R.id.vp_mess);
    }

    private void addTabs() {
        tab_layout_mess.addTab(tab_layout_mess.newTab().setIcon(R.drawable.ic_letter).setText("Messages"));
        tab_layout_mess.addTab(tab_layout_mess.newTab().setIcon(R.drawable.ic_baseline_person_outline_64).setText("Users"));


        tab_layout_mess.setTabGravity(TabLayout.GRAVITY_FILL);
        tab_layout_mess.setTabMode(TabLayout.MODE_FIXED);

        //lien ket viewPager voi tabLayout
        viewPagerAdapter = new ViewPagerMessageAdapter(getSupportFragmentManager(),tab_layout_mess.getTabCount());
        vp_mess.setAdapter(viewPagerAdapter);

        vp_mess.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout_mess));
        tab_layout_mess.getTabAt(0).setText("Messages");

        tab_layout_mess.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_mess.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()){
                    case 0 :
                        tab_layout_mess.getTabAt(0).setIcon(R.drawable.ic_letter_fill);
                        break;
                    case 1 :
                        tab_layout_mess.getTabAt(1).setIcon(R.drawable.ic_personal_filled);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0 :
                        tab_layout_mess.getTabAt(0).setIcon(R.drawable.ic_letter);
                        break;
                    case 1 :
                        tab_layout_mess.getTabAt(1).setIcon(R.drawable.ic_baseline_person_outline_64);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}