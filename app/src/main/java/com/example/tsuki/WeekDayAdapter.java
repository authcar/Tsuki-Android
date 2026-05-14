package com.example.tsuki;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.DayViewHolder> {

    private final List<Calendar> days;
    private int selectedPosition;

    // Day name abbreviations: Sun=0, Mon=1, ...
    private static final String[] DAY_NAMES = {"S", "M", "T", "W", "T", "F", "S"};

    public interface OnDayClickListener {
        void onDayClick(Calendar day, int position);
    }

    private OnDayClickListener listener;

    public WeekDayAdapter(List<Calendar> days, int todayPosition) {
        this.days = days;
        this.selectedPosition = todayPosition;
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Calendar cal = days.get(position);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sun, 6=Sat
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        holder.tvDayName.setText(DAY_NAMES[dayOfWeek]);
        holder.tvDayNumber.setText(String.format(Locale.getDefault(), "%02d", dayOfMonth));

        boolean isSelected = (position == selectedPosition);

        if (isSelected) {
            holder.tvDayNumber.setBackgroundResource(R.drawable.bg_day_selected);
            holder.tvDayNumber.setTextColor(
                    holder.itemView.getContext().getResources().getColor(R.color.white, null));
            holder.tvDayName.setTextColor(
                    holder.itemView.getContext().getResources().getColor(R.color.primary_pink, null));
            // Bold font for selected
            Typeface bold = ResourcesCompat.getFont(holder.itemView.getContext(), R.font.lato_bold);
            holder.tvDayName.setTypeface(bold);
            holder.tvDayNumber.setTypeface(bold);
        } else {
            holder.tvDayNumber.setBackgroundResource(0);
            holder.tvDayNumber.setTextColor(
                    holder.itemView.getContext().getResources().getColor(R.color.gray, null));
            holder.tvDayName.setTextColor(
                    holder.itemView.getContext().getResources().getColor(R.color.gray, null));
            Typeface regular = ResourcesCompat.getFont(holder.itemView.getContext(), R.font.lato);
            holder.tvDayName.setTypeface(regular);
            holder.tvDayNumber.setTypeface(regular);
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onDayClick(cal, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName;
        TextView tvDayNumber;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }
    }
}
