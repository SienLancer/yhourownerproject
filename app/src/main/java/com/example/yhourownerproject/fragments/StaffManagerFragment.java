package com.example.yhourownerproject.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.SignUpForStaffActivity;
import com.example.yhourownerproject.adapter.StaffAdapter;
import com.example.yhourownerproject.roles.Staff;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StaffManagerFragment extends Fragment {
    private View mView;

    FloatingActionButton create_staff_btn;

    private RecyclerView recyclerView;
    private StaffAdapter adapter;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


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
        create_staff_btn = mView.findViewById(R.id.create_staff_btn);
        recyclerView = mView.findViewById(R.id.recycler_view_staff_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StaffAdapter(staffList);
        recyclerView.setAdapter(adapter);

        create_staff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignUpForStaffActivity.class);
                startActivity(intent);
            }
        });

        loadDataFromFirebase();

        return mView;
    }

    private void showCustomToast(String message) {
        // Inflate layout cho Toast
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, requireActivity().findViewById(R.id.custom_toast_container));

        // Thiết lập nội dung của Toast
        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(message);

        // Tạo một Toast và đặt layout của nó
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Fetch shop ID of the current user
            firebaseDatabase.getReference("User")
                    .child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            if (userSnapshot.exists()) {
                                String ownerShopId = userSnapshot.child("shopID").getValue(String.class);
                                if (ownerShopId != null) {
                                    // Fetch all users
                                    firebaseDatabase.getReference("User")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                                                    if (usersSnapshot.exists()) {
                                                        // Fetch shop data once
                                                        firebaseDatabase.getReference("Shop")
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot shopSnapshot) {
                                                                        if (shopSnapshot.exists()) {
                                                                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                                                                String userKey = userSnapshot.getKey();
                                                                                Integer userRole = userSnapshot.child("role").getValue(Integer.class);
                                                                                String userName = userSnapshot.child("name").getValue(String.class);
                                                                                String userId = userSnapshot.child("id").getValue(String.class);

                                                                                // Check if the user's role is 1 and if their shop ID matches ownerShopId
                                                                                if (userRole != null && userRole == 1) {
                                                                                    String staffShopId = userSnapshot.child("shopID").getValue(String.class);
                                                                                    DataSnapshot shopData = shopSnapshot.child(ownerShopId);
                                                                                    if (shopData.exists()) {
                                                                                        String shopIdCheck = shopData.child("id").getValue(String.class);
                                                                                        if (shopIdCheck != null && shopIdCheck.equals(staffShopId)) {
                                                                                            Staff staff = new Staff(userId, userName);
                                                                                            staffList.add(staff);
                                                                                        }
                                                                                    } else {
                                                                                        showCustomToast("Shop data not found for user: " + userName);
                                                                                    }
                                                                                }
                                                                            }
                                                                            adapter.notifyDataSetChanged();
                                                                        } else {
                                                                            showCustomToast("Shop data not found");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                        Log.e(TAG, "Error fetching shop data: " + error.getMessage());
                                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    } else {
                                                        showCustomToast("User data not found");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error fetching user data: " + error.getMessage());
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }



}