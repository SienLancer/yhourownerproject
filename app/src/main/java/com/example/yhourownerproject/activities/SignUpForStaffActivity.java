package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.roles.Salary;
import com.example.yhourownerproject.roles.Staff;
import com.example.yhourownerproject.roles.Timekeeping;
import com.example.yhourownerproject.roles.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;

public class SignUpForStaffActivity extends AppCompatActivity {
    Button createAccS_btn;
    TextView staff_dob_sign_up_edt;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    EditText usernameSSignUp_edt, pwSSignUp_edt,rePwSSignUp_edt, staff_name_sign_up_edt, staff_phone_sign_up_edt, staff_address_sign_up_edt;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_staff);
        staff_dob_sign_up_edt = findViewById(R.id.staff_dob_sign_up_edt);
        staff_name_sign_up_edt = findViewById(R.id.staff_name_sign_up_edt);
        staff_phone_sign_up_edt = findViewById(R.id.staff_phone_sign_up_edt);
        staff_address_sign_up_edt = findViewById(R.id.staff_address_sign_up_edt);

        createAccS_btn = findViewById(R.id.createAccS_btn);
        usernameSSignUp_edt = findViewById(R.id.usernameSSignUp_edt);
        pwSSignUp_edt = findViewById(R.id.pwSSignUp_edt);
        rePwSSignUp_edt = findViewById(R.id.rePwSSignUp_edt);

        createAccS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();

            }
        });
        dateSetListener = (datePicker, year, month, day) -> {
            month = month +1;
            Log.d(TAG, "onDateSet: dd/mm/yyyy " + day + "/" + month + "/" + year);
            String date = day + "/" + month + "/" + year;
            staff_dob_sign_up_edt.setText(date);

        };
        staff_dob_sign_up_edt.setOnClickListener(view -> {
            Calendar kal = Calendar.getInstance();
            int year = kal.get(Calendar.YEAR);
            int month = kal.get(Calendar.MONTH);
            int day = kal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =new DatePickerDialog(SignUpForStaffActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                    dateSetListener, year, month, day);
            dialog.show();
        });
    }

    private void onClickSignUp() {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String username, password, rePassword, fullName, address, dob;
            String phoneStr = staff_phone_sign_up_edt.getText().toString();
            fullName = staff_name_sign_up_edt.getText().toString();
            address = staff_address_sign_up_edt.getText().toString();
            dob = staff_dob_sign_up_edt.getText().toString();
            username = usernameSSignUp_edt.getText().toString();
            password = pwSSignUp_edt.getText().toString();
            rePassword = rePwSSignUp_edt.getText().toString();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                        if (ownerShopId != null) {
                            if (TextUtils.isEmpty(phoneStr) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(address) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
                                Toast.makeText(getApplicationContext(), "You need to fill all required fields!", Toast.LENGTH_SHORT).show();
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                                Toast.makeText(getApplicationContext(), "Invalid email address!", Toast.LENGTH_SHORT).show();
                            } else if (password.length() < 6) {
                                Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    int phone = Integer.parseInt(phoneStr);
                                    if (password.equals(rePassword)) {
                                        mAuth.createUserWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                String id = authResult.getUser().getUid();
                                                Staff staff = new Staff(id, fullName, dob, address, phone, username, "", 0, 1, ownerShopId, password);
                                                firebaseDatabase.getReference().child("User").child(id).setValue(staff);
                                                Toast.makeText(getApplicationContext(), "Sign Up Success!", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(SignUpForStaffActivity.this, SignInForOwnerActivity.class);
                                                startActivity(intent);
                                                SignUpForStaffActivity.this.finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(getApplicationContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(SignUpForStaffActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignUpForStaffActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
        }
    }




}