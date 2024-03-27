package com.example.yhourownerproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.roles.Staff;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    private List<Staff> staffs;

    public StaffAdapter(List<Staff> staffs) {
        this.staffs = staffs;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_item_layout, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Staff staff = staffs.get(position);
        holder.staffNameTextView.setText(staff.getName());
//        holder.startDayTextView.setText("Start Day: " + week.getStartDay());
//        holder.endDayTextView.setText("End Day: " + week.getEndDay());
//        holder.detailWeekButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), WeekDetailActivity.class);
//                intent.putExtra("id", week.getId());
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return staffs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView staffNameTextView;

        Button detailStaffButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            staffNameTextView = itemView.findViewById(R.id.staff_name_tv);

            detailStaffButton = itemView.findViewById(R.id.detail_staff_btn);
        }
    }
}

