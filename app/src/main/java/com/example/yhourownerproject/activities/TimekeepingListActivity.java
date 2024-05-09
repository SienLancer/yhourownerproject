package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.yhourownerproject.adapter.TimekeeppingAdapter;
import com.example.yhourownerproject.adapter.WeekAdapter;
import com.example.yhourownerproject.roles.Timekeeping;
import com.example.yhourownerproject.roles.Week;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimekeepingListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TimekeeppingAdapter adapter;
    String timekeepingId;
    ImageButton back_btn;
    private List<Timekeeping> timekeepingList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timekeeping_list);
        back_btn = findViewById(R.id.back_imgBtn);
        recyclerView = findViewById(R.id.timekeeping_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimekeeppingAdapter(timekeepingList);
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
//                                boolean shopFound = false;
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userKey = userSnapshot.getKey();


                                    if (userKey != null && userKey.equals(timekeepingId)){
//                                        String timekeepingKey = userSnapshot.child("timekeeping").getKey();
//                                        Log.d(TAG, "Timekeeping Key: " + timekeepingKey);
                                        for (DataSnapshot timekeepingSnapshot : snapshot.child(userKey).child("timekeeping").getChildren()) {
                                            //String timekeepingId = timekeepingSnapshot.getKey();

                                            String checkIn = timekeepingSnapshot.child("checkIn").getValue(String.class);
                                            String checkOut = timekeepingSnapshot.child("checkOut").getValue(String.class);
                                            String[] parts = checkIn.split(" "); // Tách chuỗi theo dấu cách
                                            String datePart = parts[0]; // Ghép lại phần ngày tháng năm
                                            Log.d(TAG, "Date: " + datePart);

                                            Log.d(TAG, "Check In: " + checkIn);
                                            Log.d(TAG, "Check Out: " + checkOut);
                                            timekeepingList.add(new Timekeeping(datePart, checkIn, checkOut));
                                            adapter.notifyDataSetChanged();


                                        }


                                        return; // Kết thúc vòng lặp sau khi tìm thấy tuần có ID trùng khớp
                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(TimekeepingListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(TimekeepingListActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(TimekeepingListActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }


    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") ) {
            // Geting Data from Intent
            timekeepingId = getIntent().getStringExtra("id");
        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}