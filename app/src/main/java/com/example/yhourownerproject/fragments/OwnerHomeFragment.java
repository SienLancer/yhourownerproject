package com.example.yhourownerproject.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.BottomTabActivity;
import com.example.yhourownerproject.activities.StaffOnShiftActivity;
import com.example.yhourownerproject.roles.Users;
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

import java.time.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerHomeFragment extends Fragment {
    Button staff_on_shift_btn;

    private View mView;
    TextView today;
    ImageView qrcode_imgView;
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Animation animation;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public OwnerHomeFragment() {
        // Required empty public constructor
    }


    public static OwnerHomeFragment newInstance(String param1, String param2) {
        OwnerHomeFragment fragment = new OwnerHomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_owner_home, container, false);
        today = mView.findViewById(R.id.today);
        qrcode_imgView = mView.findViewById(R.id.qrcode_imgView);
        staff_on_shift_btn = mView.findViewById(R.id.staff_on_shift_btn);

        loadDialog();
        realtimeQrcode();

        staff_on_shift_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StaffOnShiftActivity.class);
                startActivity(intent);
            }
        });


        return mView;
    }

    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
        loading_imgv.startAnimation(animation);
    }

    public void realtimeQrcode() {
        try {
            loadDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                        if (ownerShopId != null) {
                            firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String realtimeqr = snapshot.getValue(String.class);
                                            MultiFormatWriter mulitFormatWriter = new MultiFormatWriter();
                                            try {
                                                BitMatrix matrix = mulitFormatWriter.encode(realtimeqr, BarcodeFormat.QR_CODE, 700, 700);
                                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                Bitmap bitmap = barcodeEncoder.createBitmap(matrix);

                                                qrcode_imgView.setImageBitmap(bitmap);
                                                loadDialog.dismiss();

                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }


    public void setToday(String date) {
        today.setText(date);}
}