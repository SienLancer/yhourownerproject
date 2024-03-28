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
import com.example.yhourownerproject.roles.Timekeeping;

import java.util.List;

public class TimekeeppingAdapter extends RecyclerView.Adapter<TimekeeppingAdapter.ViewHolder> {

    private List<Timekeeping> timekeepings;

    public TimekeeppingAdapter(List<Timekeeping> timekeepings) {
        this.timekeepings = timekeepings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timekeeping_item_layout, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Timekeeping timekeeping = timekeepings.get(position);
        holder.timekeeping_name_tv.setText(timekeeping.getId());
        holder.check_in_tv.setText(timekeeping.getCheckIn());
        holder.check_out_tv.setText(timekeeping.getCheckOut());
//        Staff staff = staffs.get(position);
//        holder.staffNameTextView.setText(staff.getName());
////        holder.startDayTextView.setText("Start Day: " + week.getStartDay());
////        holder.endDayTextView.setText("End Day: " + week.getEndDay());
//        holder.detailStaffButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), StaffDetailActivity.class);
//                intent.putExtra("id", staff.getId());
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return timekeepings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timekeeping_name_tv, check_in_tv, check_out_tv;

        Button detailTimekeepingButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timekeeping_name_tv = itemView.findViewById(R.id.timekeeping_name_tv);
            check_in_tv = itemView.findViewById(R.id.check_in_tv);
            check_out_tv = itemView.findViewById(R.id.check_out_tv);

            detailTimekeepingButton = itemView.findViewById(R.id.detail_timekeeping_btn);
        }
    }
}

