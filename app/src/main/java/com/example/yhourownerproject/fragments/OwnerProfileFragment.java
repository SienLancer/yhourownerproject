package com.example.yhourownerproject.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.SignInForOwnerActivity;
import com.example.yhourownerproject.activities.StaffListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class OwnerProfileFragment extends Fragment {
    private View mView;
    private TextView owner_name_tv, owner_email_tv, owner_shop_name_tv, owner_shop_address_tv, owner_shop_phone_tv;

    Button logoutS_btn, staff_list_btn;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public OwnerProfileFragment() {
        // Required empty public constructor
    }

    public static OwnerProfileFragment newInstance(String param1, String param2) {
        OwnerProfileFragment fragment = new OwnerProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_owner_profile, container, false);
        logoutS_btn = mView.findViewById(R.id.logoutS_btn);
        owner_email_tv = mView.findViewById(R.id.owner_email_tv);
        staff_list_btn = mView.findViewById(R.id.staff_list_btn);
        owner_name_tv = mView.findViewById(R.id.owner_name_tv);
        owner_shop_name_tv = mView.findViewById(R.id.owner_shop_name_tv);
        owner_shop_address_tv = mView.findViewById(R.id.owner_shop_address_tv);
        owner_shop_phone_tv = mView.findViewById(R.id.owner_shop_phone_tv);

        getUsername();
        getShopInfo();
        logoutS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInForOwnerActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        staff_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StaffListActivity.class);
                startActivity(intent);
            }
        });
        return mView;
    }

    private void showUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return;
        }
        String email = user.getEmail();



        owner_email_tv.setText(email);

    }

    public void getUsername() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId);

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                owner_name_tv.setText(name);
                                String email = snapshot.child("email").getValue(String.class);
                                owner_email_tv.setText(email);
                            } else {
                                Toast.makeText(getContext(), "Data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        }
    }

    public void getShopInfo() {
        try {
            //loadDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                        if (ownerShopId != null) {
                            firebaseDatabase.getReference().child("Shop").child(ownerShopId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String shopName = snapshot.child("name").getValue(String.class);
                                            String shopAddress = snapshot.child("address").getValue(String.class);
                                            Integer shopPhone = snapshot.child("phoneNumber").getValue(Integer.class); // Retrieve as Integer

                                            owner_shop_name_tv.setText(shopName);
                                            owner_shop_address_tv.setText(shopAddress);
                                            owner_shop_phone_tv.setText(String.valueOf(shopPhone)); // Convert Integer to String before setting
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


}