package com.example.yhourownerproject.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.BottomTabActivity;
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

    private View mView;
    TextView today;
    ImageView qrcode_imgView;
    Button rf_btn;
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
        rf_btn = mView.findViewById(R.id.rf_btn);

        rf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), BottomTabActivity.class);
//                startActivity(intent);
//                realtimeQrcode();

            }
        });

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                realtimeQrcode();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);








        return mView;
    }



    public void realtimeQrcode(){



        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            firebaseDatabase.getReference().child("QRCode").child("codescan")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String realtimeqr = snapshot.getValue(String.class);
                            MultiFormatWriter mulitFormatWriter = new MultiFormatWriter();
                            try {
                                BitMatrix matrix = mulitFormatWriter.encode(realtimeqr, BarcodeFormat.QR_CODE, 700, 700);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(matrix);

                                qrcode_imgView.setImageBitmap(bitmap);

                            }catch (Exception e){
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void setToday(String date) {
        today.setText(date);}
}