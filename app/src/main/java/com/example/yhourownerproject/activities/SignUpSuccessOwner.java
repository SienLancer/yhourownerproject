package com.example.yhourownerproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourownerproject.R;

public class SignUpSuccessOwner extends AppCompatActivity {
    Button start_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_success_owner);
        start_btn = findViewById(R.id.start_btn);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpSuccessOwner.this, NewShopActivity.class);
                startActivity(i);
            }
        });
    }
}