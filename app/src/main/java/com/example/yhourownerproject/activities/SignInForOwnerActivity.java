package com.example.yhourownerproject.activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourownerproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInForOwnerActivity extends AppCompatActivity {

    ImageButton backSignInO_imgBtn;
    EditText usernameOLogin_edt, pwOLogin_edt;
    TextView signUpO_txt;
    Button loginO_btn;
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Animation animation;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_owner);
        mAuth = FirebaseAuth.getInstance();

        backSignInO_imgBtn = findViewById(R.id.backSignInO_imgBtn);
        signUpO_txt = findViewById(R.id.signUpO_txt);
        loginO_btn = findViewById(R.id.loginO_btn);
        pwOLogin_edt = findViewById(R.id.pwOLogin_edt);
        usernameOLogin_edt = findViewById(R.id.usernameOLogin_edt);

        loadDialog();



        loginO_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        signUpO_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInForOwnerActivity.this, SignUpForOwnerActivity.class);
                startActivity(i);
            }
        });
    }

    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInForOwnerActivity.this);
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(SignInForOwnerActivity.this, R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
    }

    private void login() {
        try {
            String username, password;
            username = usernameOLogin_edt.getText().toString();
            password = pwOLogin_edt.getText().toString();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Please enter username!", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            loadDialog.show();
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loadDialog.dismiss();
                        String id = task.getResult().getUser().getUid();
                        firebaseDatabase.getReference().child("User").child(id).child("role")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int role = snapshot.getValue(Integer.class);
                                        if (role == 0) {

                                            Intent i = new Intent(SignInForOwnerActivity.this, BottomTabActivity.class);
                                            startActivity(i);
                                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Email or password incorrect!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    } else {
                        loadDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }

}