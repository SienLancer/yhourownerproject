package com.example.yhourownerproject.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.adapter.StaffOnShiftAdapter;
import com.example.yhourownerproject.roles.Staff;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerHomeFragment extends Fragment {
    Button staff_on_shift_btn;

    private View mView;
    TextView today, no_data_on_shift_tv;
    ImageView qrcode_imgView;
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Dialog onShiftDialog;
    Animation animation;
    private RecyclerView recyclerView;
    private StaffOnShiftAdapter adapter;
    private List<Staff> staffList = new ArrayList<>();
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

        onShiftDialog = new Dialog(getContext());
        onShiftDialog.setContentView(R.layout.custom_on_shift_dialog);
        onShiftDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        no_data_on_shift_tv = onShiftDialog.findViewById(R.id.no_data_on_shift_tv);

        recyclerView = onShiftDialog.findViewById(R.id.recycler_view_staff_on_shift);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StaffOnShiftAdapter(staffList);
        recyclerView.setAdapter(adapter);

        loadDialog();
        realtimeQrcode();
        loadStaffOnShift();
        staff_on_shift_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), StaffOnShiftActivity.class);
//                startActivity(intent);
                dialogAnimation();

            }
        });


        return mView;
    }

    public void dialogAnimation(){
        Window window = onShiftDialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.DialogAnimation); // Thiết lập animation cho dialog
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM); // Thiết lập dialog nằm ở bên trái

        }

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //layoutParams.x = 100; // Vị trí theo chiều ngang
        layoutParams.y = 170; // Vị trí theo chiều dọc
        window.setAttributes(layoutParams);

        onShiftDialog.show();
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

    private void showCustomToast(String message) {
        // Inflate layout cho Toast
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, requireActivity().findViewById(R.id.custom_toast_container));

        // Thiết lập nội dung của Toast
        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(message);

        // Tạo một Toast và đặt layout của nó
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void loadStaffOnShift() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                // Fetch shop ID of the current user
                firebaseDatabase.getReference("User")
                        .child(userId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                try {
                                    if (userSnapshot.exists()) {
                                        String ownerShopId = userSnapshot.child("shopID").getValue(String.class);
                                        if (ownerShopId != null) {
                                            // Fetch all users
                                            firebaseDatabase.getReference("User")
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                                                            try {
                                                                if (usersSnapshot.exists()) {
                                                                    // Fetch shop data once
                                                                    firebaseDatabase.getReference("Shop")
                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot shopSnapshot) {
                                                                                    try {
                                                                                        if (shopSnapshot.exists()) {
                                                                                            staffList.clear();
                                                                                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                                                                                String userKey = userSnapshot.getKey();
                                                                                                Integer userRole = userSnapshot.child("role").getValue(Integer.class);
                                                                                                String userName = userSnapshot.child("name").getValue(String.class);
                                                                                                String userId = userSnapshot.child("id").getValue(String.class);

                                                                                                // Check if the user's role is 1 and if their shop ID matches ownerShopId
                                                                                                if (userRole != null && userRole == 1) {
                                                                                                    String staffShopId = userSnapshot.child("shopID").getValue(String.class);
                                                                                                    DataSnapshot timekeepingSnapshot = userSnapshot.child("timekeeping");
                                                                                                    if (timekeepingSnapshot.exists()) {
                                                                                                        for (DataSnapshot timeSnapshot : timekeepingSnapshot.getChildren()) {
                                                                                                            String checkOutSnapshot = timeSnapshot.child("checkOut").getValue(String.class);
                                                                                                            String checkIn = timeSnapshot.child("checkIn").getValue(String.class);
                                                                                                            Log.d(TAG, "checkOutSnapshot: " + checkOutSnapshot);
                                                                                                            if (checkOutSnapshot == null || checkOutSnapshot.equals("")) {
                                                                                                                no_data_on_shift_tv.setVisibility(View.INVISIBLE);
                                                                                                                DataSnapshot shopData = shopSnapshot.child(ownerShopId);
                                                                                                                if (shopData.exists()) {
                                                                                                                    String shopIdCheck = shopData.child("id").getValue(String.class);
                                                                                                                    if (shopIdCheck != null && shopIdCheck.equals(staffShopId)) {

                                                                                                                        Staff staff = new Staff(userId, userName, checkIn);
                                                                                                                        staffList.add(staff);


                                                                                                                    }
                                                                                                                }
                                                                                                                return;
                                                                                                            }else {
                                                                                                                no_data_on_shift_tv.setVisibility(View.VISIBLE);
                                                                                                            }
                                                                                                        }

                                                                                                    } else {
                                                                                                        Log.d(TAG, "Timekeeping data not found for user: " + userName);
                                                                                                    }

                                                                                                }
                                                                                            }
                                                                                            adapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            showCustomToast("Shop data not found");
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        Log.e(TAG, "Error fetching shop data: " + e.getMessage());
                                                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                    Log.e(TAG, "Error fetching shop data: " + error.getMessage());
                                                                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                } else {
                                                                    showCustomToast("User data not found");
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error fetching user data: " + e.getMessage());
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching current user: " + e.getMessage());
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
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