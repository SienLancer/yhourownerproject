package com.example.yhourownerproject.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.yhourownerproject.roles.Owner;
import com.example.yhourownerproject.roles.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpForOwnerActivity extends AppCompatActivity {
    Button createAccO_btn;
    EditText usernameOSignUp_edt, pwOSignUp_edt,rePwOSignUp_edt;
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Animation animation;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_owner);

        createAccO_btn = findViewById(R.id.createAccO_btn);
        usernameOSignUp_edt = findViewById(R.id.usernameOSignUp_edt);
        pwOSignUp_edt = findViewById(R.id.pwOSignUp_edt);
        rePwOSignUp_edt = findViewById(R.id.rePwOSignUp_edt);
        loadDialog();

        createAccO_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();

            }
        });



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



    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpForOwnerActivity.this);
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            animation = AnimationUtils.loadAnimation(SignUpForOwnerActivity.this, R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
    }

    private void onClickSignUp() {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String username, password, repassword;
            username = usernameOSignUp_edt.getText().toString();
            password = pwOSignUp_edt.getText().toString();
            repassword = rePwOSignUp_edt.getText().toString();

            if (password.matches(repassword)) {
                mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();
                            Owner owner = new Owner(id, "", username, "", 0, password);
                            firebaseDatabase.getReference().child("User").child(id).setValue(owner);

                            showCustomToast("Sign up successful!");
                            Intent i = new Intent(SignUpForOwnerActivity.this, SignUpSuccessOwner.class);
                            startActivity(i);
                        } else {
                            showCustomToast("Sign up failed!");
                        }
                    }
                });
            } else {
                showCustomToast("Password does not match");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showCustomToast("An error occurred");
        }
    }



}