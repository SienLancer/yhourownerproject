package com.example.yhourownerproject.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.WeekListActivity;
import com.example.yhourownerproject.roles.Week;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class OwnerCalendarFragment extends Fragment {
    private View mView;
    LinearLayout list_week_layout;
    FloatingActionButton new_calendar_btn;
    ViewFlipper viewFlipper;
    TextView start_end_date_tv;
    EditText ip_shift_et, week_name_et;
    Button add_shift_btn, add_calendar_btn;
    List<String> weekKeys = new ArrayList<>();
    Dialog dialog, newWeekDialog;
    FloatingActionButton stastus_table_fabtn;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ValueEventListener listener;
    SwitchCompat stastus_table_btn;
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Animation animation;
    private DatePickerDialog.OnDateSetListener dateStartSetListener, dateEndSetListener;;
    TextView Sun1,Sun2,Sun3,
            Mon1,Mon2,Mon3,
            Tue1,Tue2,Tue3,
            Wed1,Wed2,Wed3,
            Thu1,Thu2,Thu3,
            Fri1,Fri2,Fri3,
            Sat1,Sat2,Sat3,
            morningSstart, morningSend, afternoonSstart, afternoonSend, eveningSstart, eveningSend,
            morningSstart_tue, morningSend_tue, afternoonSstart_tue, afternoonSend_tue, eveningSstart_tue, eveningSend_tue,
            morningSstart_wed, morningSend_wed, afternoonSstart_wed, afternoonSend_wed, eveningSstart_wed, eveningSend_wed,
            morningSstart_thu, morningSend_thu, afternoonSstart_thu, afternoonSend_thu, eveningSstart_thu, eveningSend_thu,
            morningSstart_fri, morningSend_fri, afternoonSstart_fri, afternoonSend_fri, eveningSstart_fri, eveningSend_fri,
            morningSstart_sat, morningSend_sat, afternoonSstart_sat, afternoonSend_sat, eveningSstart_sat, eveningSend_sat,
            morningSstart_sun, morningSend_sun, afternoonSstart_sun, afternoonSend_sun, eveningSstart_sun, eveningSend_sun,
            start_day_tv, end_day_tv, title_timetable_tv;


    public OwnerCalendarFragment() {
        // Required empty public constructor
    }

    public static OwnerCalendarFragment newInstance(String param1, String param2) {
        OwnerCalendarFragment fragment = new OwnerCalendarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_owner_calendar, container, false);
        // Inflate the layout for this fragment


        init();
        loadDialog();
        getDataTable();
        checkStatusButton();
        itemClick();
        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        if (startX < endX) {
                            // Vuốt sang phải
                            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left_viewfliper));
                            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left_viewfliper));
                            viewFlipper.showPrevious();
                        } else if (startX > endX) {
                            // Vuốt sang trái
                            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_viewfliper));
                            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right_viewfliper));
                            viewFlipper.showNext();
                        }
                        break;
                }
                return true;
            }




        });


        stastus_table_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        loadDialog.show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        String ownerShopId = snapshot.getValue(String.class);
                                        if (ownerShopId != null) {
                                            DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                            shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {
                                                        // Lấy tất cả các tuần
                                                        Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                        DataSnapshot lastWeekSnapshot = null;

                                                        // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                        for (DataSnapshot weekSnapshot : weeks) {
                                                            lastWeekSnapshot = weekSnapshot;
                                                        }

                                                        if (lastWeekSnapshot != null) {
                                                            // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                            DatabaseReference statusRef = lastWeekSnapshot.child("status").getRef();
                                                            statusRef.setValue("Opening").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    try {
                                                                        if (task.isSuccessful()) {
                                                                            loadDialog.dismiss();
                                                                            Log.d("TAG", "onComplete: Timetable is opening");
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Failed to add data", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(getContext(), "No weeks found", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    try {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    try {
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        loadDialog.show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        String ownerShopId = snapshot.getValue(String.class);
                                        if (ownerShopId != null) {
                                            DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                            shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {
                                                        // Lấy tất cả các tuần
                                                        Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                        DataSnapshot lastWeekSnapshot = null;

                                                        // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                        for (DataSnapshot weekSnapshot : weeks) {
                                                            lastWeekSnapshot = weekSnapshot;
                                                        }

                                                        if (lastWeekSnapshot != null) {
                                                            // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                            DatabaseReference statusRef = lastWeekSnapshot.child("status").getRef();
                                                            statusRef.setValue("Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    try {
                                                                        if (task.isSuccessful()) {
                                                                            loadDialog.dismiss();
                                                                            Log.d("TAG", "onComplete: Timetable is closed");
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Failed to add data", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(getContext(), "No weeks found", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    try {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    try {
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        new_calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NewCalendarActivity.class);
//                startActivity(intent);
                newWeekDialog.show();
            }
        });

        start_day_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar kal = Calendar.getInstance();
                int year = kal.get(Calendar.YEAR);
                int month = kal.get(Calendar.MONTH);
                int day = kal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog,
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

                DatePickerDialog dialog =new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog,
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

        list_week_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeekListActivity.class);
                startActivity(intent);

            }
        });

        return mView;
    }
    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
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
                                                if (TextUtils.isEmpty(weekName)){
                                                    showCustomToast("Please enter week name");
                                                    return;
                                                }else if (startDay.equals("Click here to select the calendar start date") || endDay.equals("Click here to select the calendar end date")){
                                                    showCustomToast("Please select the calendar start date and end date");
                                                    return;
                                                }
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
                                                        Toast.makeText(getContext(), "Week already exists", Toast.LENGTH_SHORT).show();
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
                                                                    newWeekDialog.dismiss();
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
                                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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


    public void checkStatusButton(){
        FirebaseUser user = mAuth.getCurrentUser();
        int colorOpening = ContextCompat.getColor(getContext(), R.color.green);
        int colorClosed = ContextCompat.getColor(getContext(), R.color.red);
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                        shopRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // Lấy tất cả các tuần
                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                DataSnapshot lastWeekSnapshot = null;

                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                for (DataSnapshot weekSnapshot : weeks) {
                                    lastWeekSnapshot = weekSnapshot;
                                }

                                if (lastWeekSnapshot != null) {
                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                    String statusTable = lastWeekSnapshot.child("status").getValue(String.class);

                                    if (statusTable != null && statusTable.equals("Opening")){
                                        stastus_table_btn.setChecked(true);
                                    }else if(statusTable != null && statusTable.equals("Closed")){
                                        stastus_table_btn.setChecked(false);
                                    }

                                } else {
                                    Toast.makeText(getContext(), "No weeks found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    public void getDataTable(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");
                        shopRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Lấy tất cả các tuần
                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                DataSnapshot lastWeekSnapshot = null;

                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                for (DataSnapshot weekSnapshot : weeks) {
                                    lastWeekSnapshot = weekSnapshot;
                                }

                                if (lastWeekSnapshot != null) {
                                    // Hiển thị dữ liệu từ tuần cuối cùng lên giao diện người dùng
                                    // Lấy dữ liệu từ tuần cuối cùng và hiển thị lên giao diện
                                    String nameWeek = lastWeekSnapshot.child("id").getValue(String.class);
                                    String[] parts = nameWeek.split(":");
                                    String namePart = parts[1];
                                    title_timetable_tv.setText(namePart);
                                    start_end_date_tv.setText(lastWeekSnapshot.child("startDay").getValue(String.class) + " - " + lastWeekSnapshot.child("endDay").getValue(String.class));
                                    Mon1.setText(lastWeekSnapshot.child("mon1").getValue(String.class));
                                    Mon2.setText(lastWeekSnapshot.child("mon2").getValue(String.class));
                                    Mon3.setText(lastWeekSnapshot.child("mon3").getValue(String.class));
                                    Tue1.setText(lastWeekSnapshot.child("tue1").getValue(String.class));
                                    Tue2.setText(lastWeekSnapshot.child("tue2").getValue(String.class));
                                    Tue3.setText(lastWeekSnapshot.child("tue3").getValue(String.class));
                                    Wed1.setText(lastWeekSnapshot.child("wed1").getValue(String.class));
                                    Wed2.setText(lastWeekSnapshot.child("wed2").getValue(String.class));
                                    Wed3.setText(lastWeekSnapshot.child("wed3").getValue(String.class));
                                    Thu1.setText(lastWeekSnapshot.child("thu1").getValue(String.class));
                                    Thu2.setText(lastWeekSnapshot.child("thu2").getValue(String.class));
                                    Thu3.setText(lastWeekSnapshot.child("thu3").getValue(String.class));
                                    Fri1.setText(lastWeekSnapshot.child("fri1").getValue(String.class));
                                    Fri2.setText(lastWeekSnapshot.child("fri2").getValue(String.class));
                                    Fri3.setText(lastWeekSnapshot.child("fri3").getValue(String.class));
                                    Sat1.setText(lastWeekSnapshot.child("sat1").getValue(String.class));
                                    Sat2.setText(lastWeekSnapshot.child("sat2").getValue(String.class));
                                    Sat3.setText(lastWeekSnapshot.child("sat3").getValue(String.class));
                                    Sun1.setText(lastWeekSnapshot.child("sun1").getValue(String.class));
                                    Sun2.setText(lastWeekSnapshot.child("sun2").getValue(String.class));
                                    Sun3.setText(lastWeekSnapshot.child("sun3").getValue(String.class));

                                    morningSstart.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_tue.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_tue.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_tue.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_tue.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_tue.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_tue.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_wed.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_wed.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_wed.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_wed.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_wed.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_wed.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_thu.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_thu.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_thu.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_thu.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_thu.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_thu.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_fri.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_fri.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_fri.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_fri.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_fri.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_fri.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_sat.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_sat.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_sat.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_sat.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_sat.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_sat.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));
                                    morningSstart_sun.setText(lastWeekSnapshot.child("morningSStart").getValue(String.class));
                                    morningSend_sun.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart_sun.setText(lastWeekSnapshot.child("afternoonSStart").getValue(String.class));
                                    afternoonSend_sun.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart_sun.setText(lastWeekSnapshot.child("eveningSStart").getValue(String.class));
                                    eveningSend_sun.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));

                                    // Tiếp tục với các TextView khác tương tự
                                    // ...
                                } else {
                                    Toast.makeText(getContext(), "No weeks found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }



    public void init(){
        list_week_layout = mView.findViewById(R.id.list_week_layout);
        new_calendar_btn = mView.findViewById(R.id.new_calendar_btn);
        stastus_table_btn = mView.findViewById(R.id.stastus_table_btn);
        title_timetable_tv = mView.findViewById(R.id.title_timetable_tv);
        viewFlipper = mView.findViewById(R.id.view_flipper);
        start_end_date_tv = mView.findViewById(R.id.start_end_date_tv);


        Mon1=mView.findViewById(R.id.Monday1);
        Mon2=mView.findViewById(R.id.Monday2);
        Mon3=mView.findViewById(R.id.Monday3);

        Tue1=mView.findViewById(R.id.Tuesday1);
        Tue2=mView.findViewById(R.id.Tuesday2);
        Tue3=mView.findViewById(R.id.Tuesday3);

        Wed1 = mView.findViewById(R.id.Wednesday1);
        Wed2 = mView.findViewById(R.id.Wednesday2);
        Wed3 = mView.findViewById(R.id.Wednesday3);

        Thu1 = mView.findViewById(R.id.Thursday1);
        Thu2 = mView.findViewById(R.id.Thursday2);
        Thu3 = mView.findViewById(R.id.Thursday3);

        Fri1 = mView.findViewById(R.id.Friday1);
        Fri2 = mView.findViewById(R.id.Friday2);
        Fri3 = mView.findViewById(R.id.Friday3);

        Sat1 = mView.findViewById(R.id.Saturday1);
        Sat2 = mView.findViewById(R.id.Saturday2);
        Sat3 = mView.findViewById(R.id.Saturday3);

        Sun1 = mView.findViewById(R.id.Sunday1);
        Sun2 = mView.findViewById(R.id.Sunday2);
        Sun3 = mView.findViewById(R.id.Sunday3);

        morningSstart=mView.findViewById(R.id.morningSstart);
        morningSend=mView.findViewById(R.id.morningSend);
        afternoonSstart=mView.findViewById(R.id.afternoonSstart);
        afternoonSend=mView.findViewById(R.id.afternoonSend);
        eveningSstart=mView.findViewById(R.id.eveningSstart);
        eveningSend=mView.findViewById(R.id.eveningSend);

        morningSstart_tue = mView.findViewById(R.id.morningSstart_tue);
        morningSend_tue = mView.findViewById(R.id.morningSend_tue);
        afternoonSstart_tue = mView.findViewById(R.id.afternoonSstart_tue);
        afternoonSend_tue = mView.findViewById(R.id.afternoonSend_tue);
        eveningSstart_tue = mView.findViewById(R.id.eveningSstart_tue);
        eveningSend_tue = mView.findViewById(R.id.eveningSend_tue);

        morningSstart_wed = mView.findViewById(R.id.morningSstart_wed);
        morningSend_wed = mView.findViewById(R.id.morningSend_wed);
        afternoonSstart_wed = mView.findViewById(R.id.afternoonSstart_wed);
        afternoonSend_wed = mView.findViewById(R.id.afternoonSend_wed);
        eveningSstart_wed = mView.findViewById(R.id.eveningSstart_wed);
        eveningSend_wed = mView.findViewById(R.id.eveningSend_wed);

        morningSstart_thu = mView.findViewById(R.id.morningSstart_thu);
        morningSend_thu = mView.findViewById(R.id.morningSend_thu);
        afternoonSstart_thu = mView.findViewById(R.id.afternoonSstart_thu);
        afternoonSend_thu = mView.findViewById(R.id.afternoonSend_thu);
        eveningSstart_thu = mView.findViewById(R.id.eveningSstart_thu);
        eveningSend_thu = mView.findViewById(R.id.eveningSend_thu);

        morningSstart_fri = mView.findViewById(R.id.morningSstart_fri);
        morningSend_fri = mView.findViewById(R.id.morningSend_fri);
        afternoonSstart_fri = mView.findViewById(R.id.afternoonSstart_fri);
        afternoonSend_fri = mView.findViewById(R.id.afternoonSend_fri);
        eveningSstart_fri = mView.findViewById(R.id.eveningSstart_fri);
        eveningSend_fri = mView.findViewById(R.id.eveningSend_fri);

        morningSstart_sat = mView.findViewById(R.id.morningSstart_sat);
        morningSend_sat = mView.findViewById(R.id.morningSend_sat);
        afternoonSstart_sat = mView.findViewById(R.id.afternoonSstart_sat);
        afternoonSend_sat = mView.findViewById(R.id.afternoonSend_sat);
        eveningSstart_sat = mView.findViewById(R.id.eveningSstart_sat);
        eveningSend_sat = mView.findViewById(R.id.eveningSend_sat);

        morningSstart_sun = mView.findViewById(R.id.morningSstart_sun);
        morningSend_sun = mView.findViewById(R.id.morningSend_sun);
        afternoonSstart_sun = mView.findViewById(R.id.afternoonSstart_sun);
        afternoonSend_sun = mView.findViewById(R.id.afternoonSend_sun);
        eveningSstart_sun = mView.findViewById(R.id.eveningSstart_sun);
        eveningSend_sun = mView.findViewById(R.id.eveningSend_sun);

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.custom_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ip_shift_et=dialog.findViewById(R.id.ip_shift_et);
        add_shift_btn =dialog.findViewById(R.id.add_shift_btn);

        newWeekDialog = new Dialog(getContext());
        newWeekDialog.setContentView(R.layout.add_calendar_dialog);
        newWeekDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        week_name_et = newWeekDialog.findViewById(R.id.week_name_et);
        start_day_tv = newWeekDialog.findViewById(R.id.start_day_tv);
        end_day_tv = newWeekDialog.findViewById(R.id.end_day_tv);
        add_calendar_btn = newWeekDialog.findViewById(R.id.add_new_calendar_btn);



    }


    private void itemClick() {
        Mon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Mon1.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("mon1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Mon1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Mon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Mon2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("mon2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Mon2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Mon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Mon3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("mon3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Mon3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Tue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Tue1.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("tue1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Tue1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Tue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Tue2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("tue2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Tue2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Tue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Tue3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("tue3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Tue3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Wed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Wed1.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("wed1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Wed1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Wed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Wed2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("wed2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Wed2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Wed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Wed3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("wed3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Wed3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Thu3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("thu1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Thu1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Thu2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("thu2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Thu2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Thu3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("thu3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Thu3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Fri1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Fri1.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("fri1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Fri1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Fri2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Fri2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("fri2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Fri2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Fri3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Fri3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("fri3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Fri3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Sat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Sat1.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sat1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Sat1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Sat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Sat2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sat2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Sat2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Sat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Sat3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sat3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Sat3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Sun1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Sun1.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sun1").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Sun1.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Sun2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Sun2.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sun2").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Sun2.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Sun3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(Sun3.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sun3").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        Sun3.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        morningSstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(morningSstart.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("morningSStart").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        morningSstart.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        morningSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(morningSend.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("morningSend").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        morningSend.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        afternoonSstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(afternoonSstart.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("afternoonSStart").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        afternoonSstart.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        afternoonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(afternoonSend.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("afternoonSend").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        afternoonSend.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        eveningSstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(eveningSstart.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("eveningSStart").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        eveningSstart.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        eveningSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                try {
                    dialog.show();
                    ip_shift_et.setText(eveningSend.getText().toString());

                    // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                    add_shift_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.getValue(String.class);
                                                if (ownerShopId != null) {
                                                    DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                                    shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            try {
                                                                // Lấy tất cả các tuần
                                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                                DataSnapshot lastWeekSnapshot = null;

                                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                                for (DataSnapshot weekSnapshot : weeks) {
                                                                    lastWeekSnapshot = weekSnapshot;
                                                                }

                                                                if (lastWeekSnapshot != null) {
                                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("eveningSend").getRef();
                                                                    String statusRef = lastWeekSnapshot.child("status").getValue(String.class);

                                                                    if (statusRef != null && statusRef.equals("Opening")) {
                                                                        sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                try {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        showCustomToast("Timetable updated");
                                                                                        eveningSend.setText(dataItem);
                                                                                    } else {
                                                                                        showCustomToast("Timetable update failed");
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showCustomToast("Timetable is closed");
                                                                    }

                                                                } else {
                                                                    showCustomToast("No weeks found");
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            try {
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showCustomToast("Shop ID not found for this user");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            try {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showCustomToast("User not logged");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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

}