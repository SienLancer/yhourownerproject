package com.example.yhourownerproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.roles.Staff;
import com.example.yhourownerproject.roles.Timekeeping;
import com.example.yhourownerproject.roles.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpForStaffActivity extends AppCompatActivity {
    Button createAccS_btn;
    EditText usernameSSignUp_edt, pwSSignUp_edt,rePwSSignUp_edt;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_staff);

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
    }

    private void onClickSignUp() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String username, password, repassword;
        username = usernameSSignUp_edt.getText().toString();
        password = pwSSignUp_edt.getText().toString();
        repassword = rePwSSignUp_edt.getText().toString();

        if (password.matches(repassword)) {
            mAuth.createUserWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    String id = authResult.getUser().getUid();
                    Timekeeping timekeeping = new Timekeeping();
                    timekeeping.addTimeRecord("id", "", "");
                    Staff staff = new Staff(id, "", "", "", 0, "", "", 0, 1, "shop1", username, password, timekeeping);
                    firebaseDatabase.getReference().child("User").child(id).setValue(staff);
                    Toast.makeText(getApplicationContext(), "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                    // Bỏ phần chuyển hướng tự động đến màn hình đăng nhập ở đây
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(SignUpForStaffActivity.this, SignInForOwnerActivity.class);
                    startActivity(intent);
                    SignUpForStaffActivity.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Đăng kí không thành công!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
        }
    }

}