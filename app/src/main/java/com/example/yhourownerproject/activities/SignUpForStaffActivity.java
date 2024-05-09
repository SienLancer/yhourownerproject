package com.example.yhourownerproject.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView loading_imgv;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    AlertDialog loadDialog;
    Animation animation;
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
        loadDialog();
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

    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpForStaffActivity.this);
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(SignUpForStaffActivity.this, R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
    }

    private void onClickSignUp() {
        try {
            loadDialog.show();
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
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                        if (ownerShopId != null) {
                            if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(rePassword)) {
                                if (password.equals(rePassword)) {
                                    if (TextUtils.isEmpty(phoneStr) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(address) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(username)) {
                                        Toast.makeText(getApplicationContext(), "You need to fill all required fields!", Toast.LENGTH_SHORT).show();
                                    } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                                        Toast.makeText(getApplicationContext(), "Invalid email address!", Toast.LENGTH_SHORT).show();
                                    } else if (password.length() < 6) {
                                        Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            int phone = Integer.parseInt(phoneStr);
                                            mAuth.createUserWithEmailAndPassword(username, password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                loadDialog.dismiss();
                                                                String id = task.getResult().getUser().getUid();
                                                                Staff staff = new Staff(id, fullName, dob, address, phone, username, "", 0, 1, ownerShopId, password, 1);
                                                                firebaseDatabase.getReference().child("User").child(id).setValue(staff);
                                                                Toast.makeText(getApplicationContext(), "Account created for staff successfully!", Toast.LENGTH_SHORT).show();
                                                                FirebaseAuth.getInstance().signOut();
                                                                Intent intent = new Intent(SignUpForStaffActivity.this, SignInForOwnerActivity.class);
                                                                startActivity(intent);
                                                                SignUpForStaffActivity.this.finish();
                                                            } else {
                                                                loadDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG, "Registration failed: " + task.getException().getMessage());
                                                            }
                                                        }
                                                    });
                                        } catch (NumberFormatException e) {
                                            loadDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    loadDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                loadDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadDialog.dismiss();
                            Toast.makeText(SignUpForStaffActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadDialog.dismiss();
                        Toast.makeText(SignUpForStaffActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
        }
    }





}