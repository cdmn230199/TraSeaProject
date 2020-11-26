package com.example.TraSeApp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.TraSeApp.PostActivity;
import com.example.TraSeApp.fragments.AddFrag;
import com.example.TraSeApp.fragments.FragmentMessage;
import com.example.TraSeApp.fragments.FragmentSearchUserMessage;
import com.example.TraSeApp.fragments.HomeFrag;
import com.example.TraSeApp.fragments.NotificationFrag;
import com.example.TraSeApp.fragments.ProfileFrag;
import com.example.TraSeApp.fragments.SearchFrag;

import static androidx.core.content.ContextCompat.startActivity;

public class ViewPagerMessageAdapter extends FragmentStatePagerAdapter {

    int noOfTabs;

    public ViewPagerMessageAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return new FragmentMessage();

            case 1:
                return new FragmentSearchUserMessage();


            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
