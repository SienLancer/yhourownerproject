package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
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
import com.example.yhourownerproject.adapter.WeekAdapter;
import com.example.yhourownerproject.roles.Staff;
import com.example.yhourownerproject.roles.Week;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaffListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StaffAdapter adapter;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        recyclerView = findViewById(R.id.recycler_view_staff_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffAdapter(staffList);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                    if (ownerShopId != null) {
                        firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                boolean shopFound = false;
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userKey = userSnapshot.getKey();
                                    String userName = userSnapshot.child("name").getValue(String.class);
                                    Log.d(TAG, "User Key: " + userKey);
                                    Log.d(TAG, "User Name: " + userName);
                                    Staff staff = new Staff(userName);
                                    staffList.add(staff);
                                    adapter.notifyDataSetChanged();

                                }
//                                if (!shopFound) {
//                                    Toast.makeText(StaffListActivity.this, "Shop not a", Toast.LENGTH_SHORT).show();
//                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(StaffListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(StaffListActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(StaffListActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}