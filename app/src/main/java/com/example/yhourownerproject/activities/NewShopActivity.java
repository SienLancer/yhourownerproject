package com.example.yhourownerproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.roles.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewShopActivity extends AppCompatActivity {
    EditText shop_new_name_edt, shop_new_address_edt, shop_new_phone_edt;
    Button shop_new_btn;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_shop);
        shop_new_name_edt = findViewById(R.id.shop_new_name_edt);
        shop_new_address_edt = findViewById(R.id.shop_new_address_edt);
        shop_new_phone_edt = findViewById(R.id.shop_new_phone_edt);
        shop_new_btn = findViewById(R.id.create_shop_btn);

        shop_new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewShop();
            }
        });



    }

    public void addNewShop() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                String userId = user.getUid();
                String name = shop_new_name_edt.getText().toString().trim();
                String address = shop_new_address_edt.getText().toString().trim();
                String email = user.getEmail();
                String phoneNumberString = shop_new_phone_edt.getText().toString().trim();

                // Kiểm tra xem các trường có rỗng không
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phoneNumberString)) {
                    Toast.makeText(NewShopActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Chuyển đổi số điện thoại sang dạng số nguyên
                int phoneNumber = 0;
                try {
                    phoneNumber = Integer.parseInt(phoneNumberString);
                } catch (NumberFormatException e) {
                    Toast.makeText(NewShopActivity.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo một đối tượng Shop mới với thông tin được cung cấp
                Shop newShop = new Shop(name, address, email, phoneNumber);

                // Thêm dữ liệu của cửa hàng mới vào Firebase
                DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").push();
                shopRef.setValue(newShop)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Thêm child QRCode với codeScan là "new"
                                    shopRef.child("QRCode").child("codeScan").setValue("new");
                                    Toast.makeText(NewShopActivity.this, "New shop added successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NewShopActivity.this, "Error adding new shop", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(NewShopActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(NewShopActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }



}