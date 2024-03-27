package com.example.yhourownerproject.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.StaffListActivity;


public class StaffManagerFragment extends Fragment {
    private View mView;
    Button staff_list_btn;


    public StaffManagerFragment() {
        // Required empty public constructor
    }

    public static StaffManagerFragment newInstance(String param1, String param2) {
        StaffManagerFragment fragment = new StaffManagerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_staff_manager, container, false);
        staff_list_btn = mView.findViewById(R.id.staff_list_btn);

        staff_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StaffListActivity.class);
                startActivity(intent);
            }
        });
        return mView;
    }
}