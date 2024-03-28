package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaffDetailActivity extends AppCompatActivity {

    private TextView data_staff_name_tv, data_staff_dob_tv, data_staff_address_tv, data_staff_phone_tv,
            data_staff_email_tv, data_staff_position_tv, data_staff_hourly_salary_tv;
    private String staffId;
    Button view_timkeeping_tv;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);
        view_timkeeping_tv = findViewById(R.id.view_timkeeping_tv);
        view_timkeeping_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDetailActivity.this, TimekeepingListActivity.class);
                intent.putExtra("id", staffId);
                startActivity(intent);
            }
        });

        init();

        getAndSetIntentData();
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


                                    if (userKey != null && userKey.equals(staffId)){
//                                        Staff staff = new Staff(userId, userName, userDob, userAddress, userPhone, userEmail, userPosition, userSalary);
//                                        staffList.add(staff);
                                        Toast.makeText(StaffDetailActivity.this, "Equal", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "User Key check: " + userKey);
                                        String userName = userSnapshot.child("name").getValue(String.class);
                                        Log.d(TAG, "User Name: " + userName);
                                        String userDob = userSnapshot.child("dateOfBirth").getValue(String.class);
                                        Log.d(TAG, "User Dob: " + userDob);
                                        String userAddress = userSnapshot.child("address").getValue(String.class);
                                        Log.d(TAG, "User Address: " + userAddress);
                                        String userPhone = userSnapshot.child("phoneNumber").getValue(String.class);
                                        String userEmail = userSnapshot.child("email").getValue(String.class);
                                        Log.d(TAG, "User Phone: " + userPhone);
                                        Log.d(TAG, "User Email: " + userEmail);
                                        String userPosition = userSnapshot.child("position").getValue(String.class);
                                        Log.d(TAG, "User Position: " + userPosition);
                                        Integer userSalary = userSnapshot.child("hourlySalary").getValue(Integer.class);
                                        Log.d(TAG, "User Salary: " + userSalary);


                                        data_staff_name_tv.setText(userName);
                                        data_staff_dob_tv.setText(userDob);
                                        data_staff_address_tv.setText(userAddress);
                                        data_staff_phone_tv.setText(userPhone);
                                        data_staff_email_tv.setText(userEmail);
                                        data_staff_position_tv.setText(userPosition);
                                        data_staff_hourly_salary_tv.setText(userSalary+"");

                                        return; // Kết thúc vòng lặp sau khi tìm thấy tuần có ID trùng khớp
                                    }


                                }
//                                if (!shopFound) {
//                                    Toast.makeText(StaffListActivity.this, "Shop not a", Toast.LENGTH_SHORT).show();
//                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(StaffDetailActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(StaffDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") ) {
            // Geting Data from Intent
            staffId = getIntent().getStringExtra("id");
        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    public void init(){
        data_staff_name_tv = findViewById(R.id.data_staff_name_tv);
        data_staff_dob_tv = findViewById(R.id.data_staff_dob_tv);
        data_staff_address_tv = findViewById(R.id.data_staff_address_tv);
        data_staff_phone_tv = findViewById(R.id.data_staff_phone_tv);
        data_staff_email_tv = findViewById(R.id.data_staff_email_tv);
        data_staff_position_tv = findViewById(R.id.data_staff_position_tv);
        data_staff_hourly_salary_tv = findViewById(R.id.data_staff_hourly_salary_tv);

    }
}