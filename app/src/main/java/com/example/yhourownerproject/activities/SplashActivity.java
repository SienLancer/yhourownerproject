package com.example.yhourownerproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourownerproject.R;
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

public class SplashActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent i = new Intent(this, SignInForOwnerActivity.class);
            startActivity(i);
            finish();
            return;
        }

        String userId = user.getUid();
        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("User").child(userId).exists()) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                     if (ownerShopId.equals("")) {
                        Toast.makeText(SplashActivity.this, "Shop not found", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SplashActivity.this, NewShopActivity.class);
                        startActivity(i);
                    }else {
                         Intent i = new Intent(SplashActivity.this, BottomTabActivity.class);
                         startActivity(i);
                     }
                } else {
                    Toast.makeText(SplashActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut(); // Sign out if user data not found
                    Intent i = new Intent(SplashActivity.this, SignInForOwnerActivity.class);
                    startActivity(i);
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}