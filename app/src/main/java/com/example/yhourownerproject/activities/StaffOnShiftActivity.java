package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.adapter.StaffAdapter;
import com.example.yhourownerproject.adapter.StaffOnShiftAdapter;
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

public class StaffOnShiftActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StaffOnShiftAdapter adapter;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_on_shift);
        recyclerView = findViewById(R.id.recycler_view_staff_on_shift);
        recyclerView.setLayoutManager(new LinearLayoutManager(StaffOnShiftActivity.this));
        adapter = new StaffOnShiftAdapter(staffList);
        recyclerView.setAdapter(adapter);

        loadDataFromFirebase();
    }

    private void showCustomToast(String message) {
        // Inflate layout cho Toast
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        // Thiết lập nội dung của Toast
        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(message);

        // Tạo một Toast và đặt layout của nó
        Toast toast = new Toast(getApplicationContext());
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
                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot shopSnapshot) {
                                                                                    try {
                                                                                        if (shopSnapshot.exists()) {
                                                                                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                                                                                String userKey = userSnapshot.getKey();
                                                                                                Integer userRole = userSnapshot.child("role").getValue(Integer.class);
                                                                                                String userName = userSnapshot.child("name").getValue(String.class);
                                                                                                String userId = userSnapshot.child("id").getValue(String.class);

                                                                                                // Check if the user's role is 1 and if their shop ID matches ownerShopId
                                                                                                if (userRole != null && userRole == 1) {
                                                                                                    String staffShopId = userSnapshot.child("shopID").getValue(String.class);
                                                                                                    DataSnapshot timekeepingSnapshot = userSnapshot.child("timekeeping");
                                                                                                    if (timekeepingSnapshot.exists()) {
                                                                                                        for (DataSnapshot timeSnapshot : timekeepingSnapshot.getChildren()) {
                                                                                                            String checkOutSnapshot = timeSnapshot.child("checkOut").getValue(String.class);
                                                                                                            String checkIn = timeSnapshot.child("checkIn").getValue(String.class);
                                                                                                            Log.d(TAG, "checkOutSnapshot: " + checkOutSnapshot);
                                                                                                            if (checkOutSnapshot == null || checkOutSnapshot.equals("")) {
                                                                                                                DataSnapshot shopData = shopSnapshot.child(ownerShopId);
                                                                                                                if (shopData.exists()) {
                                                                                                                    String shopIdCheck = shopData.child("id").getValue(String.class);
                                                                                                                    if (shopIdCheck != null && shopIdCheck.equals(staffShopId)) {
                                                                                                                        Staff staff = new Staff(userId, userName, checkIn);
                                                                                                                        staffList.add(staff);
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }

                                                                                                    } else {
                                                                                                        Log.d(TAG, "Timekeeping data not found for user: " + userName);
                                                                                                    }

                                                                                                }
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            showCustomToast("Shop data not found");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        Log.e(TAG, "Error fetching shop data: " + e.getMessage());
                                                                                        Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                    Log.e(TAG, "Error fetching shop data: " + error.getMessage());
                                                                                    Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                } else {
                                                                    showCustomToast("User data not found");
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                                                Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                                            Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(StaffOnShiftActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(StaffOnShiftActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                    Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(StaffOnShiftActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching current user: " + e.getMessage());
            Toast.makeText(StaffOnShiftActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

}