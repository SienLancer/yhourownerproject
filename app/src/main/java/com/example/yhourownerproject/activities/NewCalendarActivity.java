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
import androidx.fragment.app.FragmentTransaction;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.fragments.OwnerCalendarFragment;
import com.example.yhourownerproject.roles.Week;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        try {
            if (user != null) {
                // Lấy thời gian hiện tại
                long timestamp = System.currentTimeMillis();
                DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.getValue(String.class);
                            if (ownerShopId != null) {
                                DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId);
                                shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            if (snapshot.exists()) {
                                                // Cửa hàng được tìm thấy
                                                DataSnapshot calendarSnapshot = snapshot.child("Calendar");

                                                // Tiếp tục xử lý với dữ liệu của "Calendar"
                                                boolean shopFound = false;
                                                for (DataSnapshot calendarWeekSnapshot : calendarSnapshot.getChildren()) {
                                                    String weekId = calendarWeekSnapshot.getKey();
                                                    // Thêm weekKey vào danh sách
                                                    weekKeys.add(weekId);
                                                }

                                                // Kiểm tra weekName với các weekKey trong danh sách
                                                boolean weekExists = false;
                                                String weekName = week_name_et.getText().toString();
                                                String startDay = start_day_tv.getText().toString();
                                                String endDay = end_day_tv.getText().toString();
                                                SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                try {
                                                    Date startDate = targetFormat.parse(startDay);
                                                    Date endDate = targetFormat.parse(endDay);
                                                    startDay = targetFormat.format(startDate);
                                                    endDay = targetFormat.format(endDate);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

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
                                                    String weekNameTimestamp = timestamp + ":" + weekName;
                                                    Week week = new Week(weekNameTimestamp,
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "Morning shift is empty", "Afternoon shift is empty", "Evening shift is empty",
                                                            "6:00", "12:00", "12:00", "17:00", "17:00",
                                                            "22:00", startDay, endDay, "Opening");

                                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child(weekNameTimestamp).setValue(week).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            try {
                                                                if (task.isSuccessful()) {
                                                                    showCustomToast("Week added successfully");
                                                                    finish();
                                                                } else {
                                                                    showCustomToast("Failed to add week");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                // Cửa hàng không tồn tại
                                                showCustomToast("Shop not found");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        try {
                                            Toast.makeText(NewCalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                // Không tìm thấy shopID cho user này
                                showCustomToast("Shop not found");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        try {
                            Toast.makeText(NewCalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                showCustomToast("User not logged in");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}