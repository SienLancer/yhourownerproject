package com.example.yhourownerproject.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.CalendarActivity;
import com.example.yhourownerproject.activities.NewCalendarActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerCalendarFragment extends Fragment {
    private View mView;
    Button view_calendar_btn, new_calendar_btn, list_calendar_btn;


    public OwnerCalendarFragment() {
        // Required empty public constructor
    }

    public static OwnerCalendarFragment newInstance(String param1, String param2) {
        OwnerCalendarFragment fragment = new OwnerCalendarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_owner_calendar, container, false);
        // Inflate the layout for this fragment
        view_calendar_btn = mView.findViewById(R.id.view_calendar_btn);
        new_calendar_btn = mView.findViewById(R.id.new_calendar_btn);
        list_calendar_btn = mView.findViewById(R.id.list_calendar_btn);

        view_calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        new_calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewCalendarActivity.class);
                startActivity(intent);
            }
        });

        return mView;
    }


}