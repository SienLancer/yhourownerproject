package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.fragments.OwnerCalendarFragment;
import com.example.yhourownerproject.roles.Week;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewCalendarActivity extends AppCompatActivity {
    TextView start_day_tv, end_day_tv;
    EditText week_name_et;
    Button add_calendar_btn;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ValueEventListener listener;
    List<String> weekKeys = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener dateStartSetListener, dateEndSetListener;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_calendar);
        start_day_tv = findViewById(R.id.start_day_tv);
        end_day_tv = findViewById(R.id.end_day_tv);
        week_name_et = findViewById(R.id.week_name_et);
        add_calendar_btn = findViewById(R.id.add_calendar_btn);


        start_day_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar kal = Calendar.getInstance();
                int year = kal.get(Calendar.YEAR);
                int month = kal.get(Calendar.MONTH);
                int day = kal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =new DatePickerDialog(NewCalendarActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        dateStartSetListener, year, month, day);
                dialog.show();
            }


        });

        dateStartSetListener = (datePicker, year, month, day) -> {
            month = month +1;
            Log.d(TAG, "onDateSet: dd/mm/yyyy " + day + "/" + month + "/" + year);
            String date = day + "/" + month + "/" + year;
            start_day_tv.setText(date);

        };

        end_day_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar kal = Calendar.getInstance();
                int year = kal.get(Calendar.YEAR);
                int month = kal.get(Calendar.MONTH);
                int day = kal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =new DatePickerDialog(NewCalendarActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        dateEndSetListener, year, month, day);
                dialog.show();
            }
        });

        dateEndSetListener = (datePicker, year, month, day) -> {
            month = month +1;
            Log.d(TAG, "onDateSet: dd/mm/yyyy " + day + "/" + month + "/" + year);
            String date = day + "/" + month + "/" + year;
            end_day_tv.setText(date);

        };

        add_calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataCalendar();
            }
        });
    }

    public void addDataCalendar() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            // Lấy thời gian hiện tại
            long timestamp = System.currentTimeMillis();
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                    firebaseDatabase.getReference("Shop").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean shopFound = false;
                            for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                                String shopKey = shopSnapshot.getKey();
                                Log.d(TAG, "Shop Key: " + shopKey);
                                if (ownerShopId.equals(shopKey)) {
                                    shopFound = true;
                                    //Toast.makeText(NewCalendarActivity.this, "Shop found", Toast.LENGTH_SHORT).show();
                                    // Thực hiện các hành động cần thiết khi tìm thấy cửa hàng

                                    for (DataSnapshot calendarSnapshot : shopSnapshot.child("Calendar").getChildren()) {
                                        String weekId = calendarSnapshot.getKey();
                                        Log.d(TAG, "Week Key: " + weekId);
                                        weekKeys.add(weekId); // Thêm weekKey vào danh sách
                                    }

                                    // Kiểm tra weekName với các weekKey trong danh sách
                                    boolean weekExists = false;
                                    String weekName = week_name_et.getText().toString();
                                    Log.d(TAG, "Week Name: " + weekName);
                                    String startDay = start_day_tv.getText().toString();
                                    String endDay = end_day_tv.getText().toString();
                                    for (String weekKey : weekKeys) {
                                        if (weekName.equalsIgnoreCase(weekKey)) {
                                            weekExists = true;
                                            // Nếu weekName trùng với weekKey
                                            Toast.makeText(NewCalendarActivity.this, "Week already exists", Toast.LENGTH_SHORT).show();
                                            break; // Kết thúc kiểm tra khi tìm thấy weekName trùng với weekKey
                                        }
                                    }
                                    if (!weekExists) {
                                        // Nếu weekName không trùng với bất kỳ weekKey nào trong danh sách
                                        //Toast.makeText(NewCalendarActivity.this, "Week added", Toast.LENGTH_SHORT).show();
                                        // Thực hiện các hành động cần thiết khi thêm tuần mới vào lịch của cửa
                                        Week week = new Week(timestamp+weekName,"", "", "", "", "", "", "", "", "",
                                              "", "", "", "", "", "", "", "", "", "", "",
                                               "", "", "", "", "", "",
                                                "", startDay, endDay);
                                        String weekNameTimestamp = timestamp+ weekName;
                                        firebaseDatabase.getReference().child("Shop").child(shopKey).child("Calendar").child(weekNameTimestamp).setValue(week).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(NewCalendarActivity.this, "Calendar added", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(NewCalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
//                                        Intent intent = new Intent(NewCalendarActivity.this, OwnerCalendarFragment.class);
//                                        startActivity(intent);
                                        break; // Kết thúc vòng lặp khi thêm tuần mới thành công
                                    }

                                    break; // Kết thúc vòng lặp khi đã tìm thấy cửa hàng
                                }
                                break; // Kết thúc vòng lặp khi đã tìm thấy cửa hàng
                            }
                            if (!shopFound) {
                                Toast.makeText(NewCalendarActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(NewCalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(NewCalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}