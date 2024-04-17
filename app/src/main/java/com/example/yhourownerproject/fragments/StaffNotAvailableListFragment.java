package com.example.yhourownerproject.fragments;

import static android.content.ContentValues.TAG;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffNotAvailableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffNotAvailableListFragment extends Fragment {

    View mView;
    private RecyclerView recyclerView;
    private StaffAdapter adapter;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public StaffNotAvailableListFragment() {
        // Required empty public constructor
    }


    public static StaffNotAvailableListFragment newInstance(String param1, String param2) {
        StaffNotAvailableListFragment fragment = new StaffNotAvailableListFragment();

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
        mView = inflater.inflate(R.layout.fragment_staff_not_available_list, container, false);
        recyclerView = mView.findViewById(R.id.recycler_view_staff_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StaffAdapter(staffList);
        recyclerView.setAdapter(adapter);

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
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                // Fetch shop ID of the current user
                firebaseDatabase.getReference("User")
                        .child(userId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                try {
                                    if (userSnapshot.exists()) {
                                        String ownerShopId = userSnapshot.child("shopID").getValue(String.class);
                                        if (ownerShopId != null) {
                                            // Fetch all users
                                            firebaseDatabase.getReference("User")
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                                                            try {
                                                                if (usersSnapshot.exists()) {
                                                                    // Fetch shop data once
                                                                    firebaseDatabase.getReference("Shop")
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot shopSnapshot) {
                                                                                    try {
                                                                                        if (shopSnapshot.exists()) {
                                                                                            staffList.clear();
                                                                                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                                                                                String userKey = userSnapshot.getKey();
                                                                                                Integer userRole = userSnapshot.child("role").getValue(Integer.class);
                                                                                                String userName = userSnapshot.child("name").getValue(String.class);
                                                                                                String userId = userSnapshot.child("id").getValue(String.class);
                                                                                                Integer available = userSnapshot.child("availabilityStatus").getValue(Integer.class);

                                                                                                // Check if the user's role is 1 and if their shop ID matches ownerShopId
                                                                                                if (userRole != null && userRole == 1) {
                                                                                                    String staffShopId = userSnapshot.child("shopID").getValue(String.class);
                                                                                                    DataSnapshot shopData = shopSnapshot.child(ownerShopId);
                                                                                                    if (shopData.exists()) {
                                                                                                        String shopIdCheck = shopData.child("id").getValue(String.class);
                                                                                                        if (shopIdCheck != null && shopIdCheck.equals(staffShopId) && available != null && available == 0) {
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
                                                                                    } catch (Exception e) {
                                                                                        Log.e(TAG, "Error fetching shop data: " + e.getMessage());
                                                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
                                                            } catch (Exception e) {
                                                                Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
                                } catch (Exception e) {
                                    Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Log.e(TAG, "Error fetching current user: " + e.getMessage());
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}