package com.example.TraSeApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.TraSeApp.adapter.ViewPagerHomeAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerHomeAdapter viewPagerAdapter;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        init();
        addTabs();
//        saveProfileId(this, "profileid", firebaseUser.getUid());

    }

//    public  static void saveProfileId (Context context, String key, String value){
//        SharedPreferences sp2 = context.getSharedPreferences("caches", Context.MODE_PRIVATE);
//        sp2.edit().putString(key,value).apply();
//    }

    private void init() {
        viewPager = findViewById(R.id.vp_story);
        tabLayout = findViewById(R.id.tab_story);
    }

    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_find_no_fill));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add_no_fill));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_noti));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_personal));

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //lien ket viewPager voi tabLayout
        viewPagerAdapter = new ViewPagerHomeAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_filled);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()){
                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_filled);
                        break;
                    case 1 :
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add);
                        break;
                    case 3 :
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_noti_filled);
                        break;
                    case 4 :
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_personal_filled);
                        break;


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
                        break;
                    case 1 :
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_find_no_fill);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add_no_fill);
                        break;
                    case 3 :
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_noti);
                        break;
                    case 4 :
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_personal);
                        break;


                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}