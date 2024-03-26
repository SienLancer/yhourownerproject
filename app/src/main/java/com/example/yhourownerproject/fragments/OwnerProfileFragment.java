package com.example.yhourownerproject.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.SignInForOwnerActivity;
import com.google.firebase.auth.FirebaseAuth;


public class OwnerProfileFragment extends Fragment {
    private View mView;
    private TextView staff_name_tv, staff_email_tv;

    Button logoutS_btn;

    public OwnerProfileFragment() {
        // Required empty public constructor
    }

    public static OwnerProfileFragment newInstance(String param1, String param2) {
        OwnerProfileFragment fragment = new OwnerProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_owner_profile, container, false);
        logoutS_btn = mView.findViewById(R.id.logoutS_btn);


        logoutS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInForOwnerActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return mView;
    }
}