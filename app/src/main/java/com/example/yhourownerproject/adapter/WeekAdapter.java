package com.example.yhourownerproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourownerproject.R;
import com.example.yhourownerproject.roles.Week;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private List<Week> weeks;

    public WeekAdapter(List<Week> weeks) {
        this.weeks = weeks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_item_layout, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Week week = weeks.get(position);
        holder.weekNameTextView.setText(week.getId());
        holder.startDayTextView.setText("Start Day: " + week.getStartDay());
        holder.endDayTextView.setText("End Day: " + week.getEndDay());
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weekNameTextView;
        TextView startDayTextView;
        TextView endDayTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weekNameTextView = itemView.findViewById(R.id.week_name_tv);
            startDayTextView = itemView.findViewById(R.id.start_day_tv);
            endDayTextView = itemView.findViewById(R.id.end_day_tv);
        }
    }
}

