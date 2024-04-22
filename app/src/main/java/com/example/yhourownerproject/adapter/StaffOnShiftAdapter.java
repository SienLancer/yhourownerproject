package com.example.yhourownerproject.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.activities.StaffDetailActivity;
import com.example.yhourownerproject.roles.Staff;

import java.util.List;

public class StaffOnShiftAdapter extends RecyclerView.Adapter<StaffOnShiftAdapter.ViewHolder> {

    private List<Staff> staffs;

    public StaffOnShiftAdapter(List<Staff> staffs) {
        this.staffs = staffs;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_on_shift_layout, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Staff staff = staffs.get(position);
        holder.staff_name_on_shift_tv.setText(staff.getName());
        holder.staff_check_in_on_shift_tv.setText("Check in at "+staff.getCheckIn());

    }

    @Override
    public int getItemCount() {
        return staffs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView staff_name_on_shift_tv, staff_check_in_on_shift_tv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            staff_name_on_shift_tv = itemView.findViewById(R.id.staff_name_on_shift_tv);
            staff_check_in_on_shift_tv = itemView.findViewById(R.id.staff_check_in_on_shift_tv);

        }
    }
}

