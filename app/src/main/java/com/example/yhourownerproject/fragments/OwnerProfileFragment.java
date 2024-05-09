package com.example.yhourownerproject.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.ChangePasswordActivity;
import com.example.yhourownerproject.activities.SignInForOwnerActivity;
import com.example.yhourownerproject.activities.StaffDetailActivity;
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
    ImageView edit_shop_name_img_view, loading_imgv, edit_shop_address_img_view, edit_shop_phone_img_view;
    TextView owner_name_tv, owner_email_tv, owner_shop_name_tv, owner_shop_address_tv, owner_shop_phone_tv;
    Dialog dialog, yesNoDialog;
    TextView title_dialog_tv, dialog_yes_no_title, dialog_yes_no_message;
    Button logoutS_btn, staff_list_btn, profile_change_password_btn, add_dialog_btn, button_no, button_yes;
    EditText ip_position_dialog_et;
    AlertDialog loadDialog;
    Animation animation;
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
        profile_change_password_btn = mView.findViewById(R.id.profile_change_password_btn);
        edit_shop_name_img_view = mView.findViewById(R.id.edit_shop_name_img_view);
        edit_shop_address_img_view = mView.findViewById(R.id.edit_shop_address_img_view);
        edit_shop_phone_img_view = mView.findViewById(R.id.edit_shop_phone_img_view);

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.custom_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        title_dialog_tv = dialog.findViewById(R.id.title_dialog_tv);
        ip_position_dialog_et=dialog.findViewById(R.id.ip_shift_et);
        add_dialog_btn =dialog.findViewById(R.id.add_shift_btn);

        add_dialog_btn.setText("Update");

        yesNoDialog = new Dialog(getContext());
        yesNoDialog.setContentView(R.layout.custom_yes_no_dialog);
        yesNoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_yes_no_title = yesNoDialog.findViewById(R.id.dialog_yes_no_title);
        dialog_yes_no_message = yesNoDialog.findViewById(R.id.dialog_yes_no_message);
        button_yes = yesNoDialog.findViewById(R.id.button_yes);
        button_no = yesNoDialog.findViewById(R.id.button_no);

        dialog_yes_no_title.setText("Log out");
        dialog_yes_no_message.setText("Do you want to log out?");

        getUsername();
        getShopInfo();

        edit_shop_name_img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShopName();
            }
        });

        edit_shop_address_img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShopAddress();
            }
        });

        edit_shop_phone_img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShopPhone();
            }
        });
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInForOwnerActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        logoutS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesNoDialog.show();
            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoDialog.dismiss();
            }
        });

        profile_change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
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

    public void updateShopName() {
        try {
            dialog.show();
            title_dialog_tv.setText("Shop Name");
            ip_position_dialog_et.setHint("Enter a new shop name");

            add_dialog_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //loadDialog.show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    if (user != null) {
                        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                if (ownerShopId != null) {
                                    DatabaseReference userReference = firebaseDatabase.getReference().child("Shop").child(ownerShopId);
                                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    try {
                                                        String shopName = ip_position_dialog_et.getText().toString().trim();

                                                        if (shopName.isEmpty()) {
                                                            // Kiểm tra xem bất kỳ trường nào có trống không
                                                            Toast.makeText(getContext(), "Please fill in shop name", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }



                                                        // Cập nhật dữ liệu vào cơ sở dữ liệu
                                                        userReference.child("name").setValue(shopName);


                                                        // Hiển thị Toast khi cập nhật thành công
                                                        Toast.makeText(getContext(), "Shop name updated successfully", Toast.LENGTH_SHORT).show();
                                                        //loadDialog.dismiss();
                                                        dialog.dismiss();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
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
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateShopAddress() {
        try {
            dialog.show();
            title_dialog_tv.setText("Shop Address");
            ip_position_dialog_et.setHint("Enter a new shop name");


            add_dialog_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //loadDialog.show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    if (user != null) {
                        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                if (ownerShopId != null) {
                                    DatabaseReference userReference = firebaseDatabase.getReference().child("Shop").child(ownerShopId);
                                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String shopAddress = ip_position_dialog_et.getText().toString().trim();

                                                if (shopAddress.isEmpty()) {
                                                    // Kiểm tra xem bất kỳ trường nào có trống không
                                                    Toast.makeText(getContext(), "Please fill in shop address", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }



                                                // Cập nhật dữ liệu vào cơ sở dữ liệu
                                                userReference.child("address").setValue(shopAddress);


                                                // Hiển thị Toast khi cập nhật thành công
                                                Toast.makeText(getContext(), "Shop address updated successfully", Toast.LENGTH_SHORT).show();
                                                //loadDialog.dismiss();
                                                dialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
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
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateShopPhone() {
        try {
            dialog.show();
            title_dialog_tv.setText("Shop Phone Number");
            ip_position_dialog_et.setHint("Enter a new shop phone number");


            add_dialog_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //loadDialog.show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    if (user != null) {
                        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                if (ownerShopId != null) {
                                    DatabaseReference userReference = firebaseDatabase.getReference().child("Shop").child(ownerShopId);
                                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                String shopPhone = ip_position_dialog_et.getText().toString().trim();
                                                int newShopPhone = Integer.parseInt(shopPhone);
                                                if (shopPhone.isEmpty()) {
                                                    // Kiểm tra xem bất kỳ trường nào có trống không
                                                    Toast.makeText(getContext(), "Please fill in shop phone number", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }



                                                // Cập nhật dữ liệu vào cơ sở dữ liệu
                                                userReference.child("phoneNumber").setValue(newShopPhone);


                                                // Hiển thị Toast khi cập nhật thành công
                                                Toast.makeText(getContext(), "Shop phone number updated successfully", Toast.LENGTH_SHORT).show();
                                                //loadDialog.dismiss();
                                                dialog.dismiss();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                // If inputText is not an integer, display error message
                                                Toast.makeText(getContext(), "Please fill in the number type", Toast.LENGTH_SHORT).show();
                                                // Clear the content of EditText to request re-entry
                                                ip_position_dialog_et.setText("");
                                                // Focus on EditText to prompt user to re-enter
                                                ip_position_dialog_et.requestFocus();
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
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                                            owner_shop_phone_tv.setText("+84 "+shopPhone); // Convert Integer to String before setting
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