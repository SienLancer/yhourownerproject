package com.example.yhourownerproject.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.yhourownerproject.fragments.StaffAvailableListFragment;
import com.example.yhourownerproject.fragments.StaffNotAvailableListFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MyViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new StaffAvailableListFragment();
            case 1:
                return new StaffNotAvailableListFragment();
            default:
                return new StaffAvailableListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
