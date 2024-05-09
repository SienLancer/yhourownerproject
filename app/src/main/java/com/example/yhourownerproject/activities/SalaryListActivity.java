package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.yhourownerproject.adapter.SalaryAdapter;
import com.example.yhourownerproject.adapter.TimekeeppingAdapter;
import com.example.yhourownerproject.roles.Salary;
import com.example.yhourownerproject.roles.Timekeeping;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SalaryListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SalaryAdapter adapter;
    String salaryId;
    ImageButton back_btn;
    private List<Salary> salaries = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_list);
        back_btn = findViewById(R.id.back_imgBtn);
        recyclerView = findViewById(R.id.salary_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalaryAdapter(salaries);
        recyclerView.setAdapter(adapter);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getAndSetIntentData();
        // Load data from Firebase
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                    if (ownerShopId != null) {
                        firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userKey = userSnapshot.getKey();


                                    if (userKey != null && userKey.equals(salaryId)){

                                        for (DataSnapshot timekeepingSnapshot : snapshot.child(userKey).child("salary").getChildren()) {
                                            String salaryKey = timekeepingSnapshot.getKey();

                                            String sid = timekeepingSnapshot.child("id").getValue(String.class);
                                            String startDate = timekeepingSnapshot.child("startDate").getValue(String.class);
                                            Integer currentSalary = timekeepingSnapshot.child("currentSalary").getValue(Integer.class);
                                            String status = timekeepingSnapshot.child("status").getValue(String.class);
                                            String payDay = timekeepingSnapshot.child("payDay").getValue(String.class);

                                            Log.d(TAG, "Salary Key: " + salaryKey);
                                            Log.d(TAG, "Start Date: " + startDate);
                                            Log.d(TAG, "Status: " + status);
                                            Log.d(TAG, "Pay Day: " + payDay);
                                            Log.d(TAG, "Current Salary: " + currentSalary);

                                            salaries.add(new Salary(sid,currentSalary, status, startDate, payDay));
                                            adapter.notifyDataSetChanged();


                                        }


                                        return; // Kết thúc vòng lặp sau khi tìm thấy tuần có ID trùng khớp
                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SalaryListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(SalaryListActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(SalaryListActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }


    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") ) {
            // Geting Data from Intent
            salaryId = getIntent().getStringExtra("id");
        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}