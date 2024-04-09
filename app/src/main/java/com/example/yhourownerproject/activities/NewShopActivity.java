package com.example.yhourownerproject.activities;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
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
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Animation animation;
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

        loadDialog();
        shop_new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewShop();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(NewShopActivity.this);
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(NewShopActivity.this, R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
    }

    public void addNewShop() {
        try {
            loadDialog.show();
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
                DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").push(); // Generate unique ID
                String id = shopRef.getKey(); // Get the generated ID
                Shop newShop = new Shop(id, name, address, email, phoneNumber);

                // Thêm dữ liệu của cửa hàng mới vào Firebase
                shopRef.setValue(newShop)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Thêm child QRCode với codeScan là "new"
                                    shopRef.child("QRCode").child("codeScan").setValue("new");
                                    firebaseDatabase.getReference().child("User").child(userId).child("shopID").setValue(id); // Store shop ID under user
                                    showCustomToast("New shop added successfully");
                                    loadDialog.dismiss();
                                    Intent intent = new Intent(NewShopActivity.this, BottomTabActivity.class);
                                    startActivity(intent);
                                } else {
                                    showCustomToast("Error adding new shop");
                                }
                            }
                        });
            } else {
                showCustomToast("User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showCustomToast("An error occurred");
        }
    }




}