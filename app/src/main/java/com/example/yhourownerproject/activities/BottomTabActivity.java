package com.example.yhourownerproject.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.fragments.OwnerCalendarFragment;
import com.example.yhourownerproject.fragments.OwnerHomeFragment;
import com.example.yhourownerproject.fragments.OwnerProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomTabActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_tab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new OwnerHomeFragment());
        bottomNavigationView.getMenu().findItem(R.id.home_owner).setChecked(true);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_owner) {
                replaceFragment(new OwnerHomeFragment());
            } else if (id == R.id.calendar_owner) {
                replaceFragment(new OwnerCalendarFragment());
            }else if (id == R.id.profile_owner) {
                replaceFragment(new OwnerProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}