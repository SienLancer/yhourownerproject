package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.adapter.StaffAdapter;
import com.example.yhourownerproject.roles.Staff;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaffDetailActivity extends AppCompatActivity {

    private TextView data_staff_name_tv, data_staff_dob_tv, data_staff_address_tv, data_staff_phone_tv,
            data_staff_email_tv, data_staff_position_tv, data_staff_hourly_salary_tv, staff_active_tv;
    private String staffId;
    Dialog dialog, yesNoDialog;
    EditText ip_position_dialog_et;
    Button view_timkeeping_btn, set_position_btn, add_dialog_btn,
            set_hourly_salary_btn, salary_list_btn, button_no, button_yes;
    TextView title_dialog_tv, dialog_yes_no_title, dialog_yes_no_message;
    private List<Staff> staffList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);
        view_timkeeping_btn = findViewById(R.id.view_timkeeping_btn);
        set_position_btn = findViewById(R.id.set_position_btn);
        set_hourly_salary_btn = findViewById(R.id.set_hourly_salary_btn);
        salary_list_btn = findViewById(R.id.salary_list_btn);
        staff_active_tv = findViewById(R.id.staff_active_tv);


        dialog=new Dialog(StaffDetailActivity.this);
        dialog.setContentView(R.layout.custom_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        title_dialog_tv = dialog.findViewById(R.id.title_dialog_tv);
        ip_position_dialog_et=dialog.findViewById(R.id.ip_shift_et);
        add_dialog_btn =dialog.findViewById(R.id.add_shift_btn);

        yesNoDialog = new Dialog(StaffDetailActivity.this);
        yesNoDialog.setContentView(R.layout.custom_yes_no_dialog);
        yesNoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_yes_no_title = yesNoDialog.findViewById(R.id.dialog_yes_no_title);
        dialog_yes_no_message = yesNoDialog.findViewById(R.id.dialog_yes_no_message);
        button_yes = yesNoDialog.findViewById(R.id.button_yes);
        button_no = yesNoDialog.findViewById(R.id.button_no);

        salary_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDetailActivity.this, SalaryListActivity.class);
                intent.putExtra("id", staffId);
                startActivity(intent);
            }
        });

        staff_active_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextDialog();
            }
        });

        set_hourly_salary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    title_dialog_tv.setText("Set Hourly Salary");
                    ip_position_dialog_et.setHint("Enter a new hourly salary");

                    add_dialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                // Get value from EditText
                                String inputText = ip_position_dialog_et.getText().toString();

                                // Check if inputText is an integer
                                try {
                                    int newHourlySalary = Integer.parseInt(inputText);

                                    // If the input value is an integer, proceed with processing
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userId = user.getUid();
                                    if (user != null) {
                                        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                                    Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                                                    if (ownerShopId != null) {
                                                        // Get reference to the user's position to update data
                                                        DatabaseReference userReference = firebaseDatabase.getReference("User").child(staffId);
                                                        userReference.child("hourlySalary").setValue(newHourlySalary).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(StaffDetailActivity.this, "Hourly salary updated successfully", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(StaffDetailActivity.this, "Failed to update hourly salary", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                        // After updating data, you can dismiss the dialog or perform other actions here
                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(StaffDetailActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(StaffDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NumberFormatException e) {
                                    // If inputText is not an integer, display error message
                                    Toast.makeText(StaffDetailActivity.this, "Please fill in the number type", Toast.LENGTH_SHORT).show();
                                    // Clear the content of EditText to request re-entry
                                    ip_position_dialog_et.setText("");
                                    // Focus on EditText to prompt user to re-enter
                                    ip_position_dialog_et.requestFocus();
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

        set_position_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    title_dialog_tv.setText("Set Position");
                    ip_position_dialog_et.setHint("Enter a new position");
                    add_dialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid();
                                if (user != null) {
                                    firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                                Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                                                if (ownerShopId != null) {
                                                    String newPosition = ip_position_dialog_et.getText().toString();
                                                    // Get reference to the user's position to update data
                                                    DatabaseReference userReference = firebaseDatabase.getReference("User").child(staffId);
                                                    userReference.child("position").setValue(newPosition).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(StaffDetailActivity.this, "Position updated successfully", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(StaffDetailActivity.this, "Failed to update position", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    // After updating data, you can dismiss the dialog or perform other actions here
                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(StaffDetailActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(StaffDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
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


        view_timkeeping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDetailActivity.this, TimekeepingListActivity.class);
                intent.putExtra("id", staffId);
                startActivity(intent);
            }
        });

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActiveStatus();
            }
        });
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoDialog.dismiss();
            }
        });

        init();

        getAndSetIntentData();
        // Load data from Firebase
        loadDataFromFirebase();
    }

    private void changeActiveStatus() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            int colorOpening = ContextCompat.getColor(StaffDetailActivity.this, R.color.green);
            int colorClosed = ContextCompat.getColor(StaffDetailActivity.this, R.color.red);
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                            if (ownerShopId != null) {
                                firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                String userKey = userSnapshot.getKey();
                                                if (userKey != null && userKey.equals(staffId)){

                                                    Integer active = userSnapshot.child("availabilityStatus").getValue(Integer.class);
                                                    if (active != null) {
                                                        DatabaseReference userRef = firebaseDatabase.getReference("User").child(staffId).child("availabilityStatus");
                                                        if (active == 1) {
                                                            yesNoDialog.dismiss();
                                                            userRef.setValue(0); // Nếu active == 1, thì set giá trị thành 0
                                                        } else if (active == 0) {
                                                            yesNoDialog.dismiss();
                                                            userRef.setValue(1); // Nếu active == 0, thì set giá trị thành 1
                                                        }
                                                        return;
                                                    }

                                                    return;
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(StaffDetailActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(StaffDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setTextDialog() {
        try {
            yesNoDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            int colorOpening = ContextCompat.getColor(StaffDetailActivity.this, R.color.green);
            int colorClosed = ContextCompat.getColor(StaffDetailActivity.this, R.color.red);
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                            if (ownerShopId != null) {
                                firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                String userKey = userSnapshot.getKey();
                                                if (userKey != null && userKey.equals(staffId)){

                                                    Integer active = userSnapshot.child("availabilityStatus").getValue(Integer.class);
                                                    if (active != null) {
                                                        if (active == 1) {
                                                            dialog_yes_no_title.setText("Set Active Status");
                                                            dialog_yes_no_message.setText("Do you want to set the active status to inactive?");
                                                        } else if (active == 0) {
                                                            dialog_yes_no_title.setText("Set Active Status");
                                                            dialog_yes_no_message.setText("Do you want to set the inactive status to active?");
                                                        }
                                                        return;
                                                    }

                                                    return;
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(StaffDetailActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(StaffDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFirebase() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            int colorOpening = ContextCompat.getColor(StaffDetailActivity.this, R.color.green);
            int colorClosed = ContextCompat.getColor(StaffDetailActivity.this, R.color.red);
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                            if (ownerShopId != null) {
                                firebaseDatabase.getReference("User").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                String userKey = userSnapshot.getKey();
                                                if (userKey != null && userKey.equals(staffId)){
                                                    String userName = userSnapshot.child("name").getValue(String.class);
                                                    String userDob = userSnapshot.child("dateOfBirth").getValue(String.class);
                                                    String userAddress = userSnapshot.child("address").getValue(String.class);
                                                    Integer userPhone = userSnapshot.child("phoneNumber").getValue(Integer.class);
                                                    String userEmail = userSnapshot.child("email").getValue(String.class);
                                                    String userPosition = userSnapshot.child("position").getValue(String.class);
                                                    Integer userSalary = userSnapshot.child("hourlySalary").getValue(Integer.class);
                                                    Integer active = userSnapshot.child("availabilityStatus").getValue(Integer.class);
                                                    if (active != null && active == 1){
                                                        staff_active_tv.setBackgroundColor(colorOpening);
                                                    }else if(active != null && active == 0){
                                                        staff_active_tv.setBackgroundColor(colorClosed);
                                                    }
                                                    data_staff_name_tv.setText(userName);
                                                    data_staff_dob_tv.setText(userDob);
                                                    data_staff_address_tv.setText(userAddress);
                                                    data_staff_phone_tv.setText(String.valueOf(userPhone));
                                                    data_staff_email_tv.setText(userEmail);
                                                    data_staff_position_tv.setText(userPosition);
                                                    data_staff_hourly_salary_tv.setText(String.valueOf(userSalary));

                                                    return;
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(StaffDetailActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StaffDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(StaffDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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