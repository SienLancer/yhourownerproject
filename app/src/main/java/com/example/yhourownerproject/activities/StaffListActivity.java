package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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

public class StaffListActivity extends AppCompatActivity {
    FloatingActionButton create_staff_btn;

    private RecyclerView recyclerView;
    private StaffAdapter adapter;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_list);
        create_staff_btn = findViewById(R.id.create_staff_btn);
        recyclerView = findViewById(R.id.recycler_view_staff_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(StaffListActivity.this));
        adapter = new StaffAdapter(staffList);
        recyclerView.setAdapter(adapter);

        create_staff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffListActivity.this, SignUpForStaffActivity.class);
                startActivity(intent);
            }
        });

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
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                try {
                                    if (userSnapshot.exists()) {
                                        String ownerShopId = userSnapshot.child("shopID").getValue(String.class);
                                        if (ownerShopId != null) {
                                            // Fetch all users
                                            firebaseDatabase.getReference("User")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                                                    } catch (Exception e) {
                                                                                        Log.e(TAG, "Error fetching shop data: " + e.getMessage());
                                                                                        Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                    Log.e(TAG, "Error fetching shop data: " + error.getMessage());
                                                                                    Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                } else {
                                                                    showCustomToast("User data not found");
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                                                Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                                            Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(StaffListActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(StaffListActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                    Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(StaffListActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching current user: " + e.getMessage());
            Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}