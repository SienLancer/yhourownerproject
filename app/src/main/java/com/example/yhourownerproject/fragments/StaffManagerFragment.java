package com.example.yhourownerproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yhourownerproject.R;


public class StaffManagerFragment extends Fragment {
    private View mView;

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

        return mView;
    }
}