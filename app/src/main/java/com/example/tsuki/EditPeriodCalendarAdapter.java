package com.example.tsuki;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class EditPeriodCalendarAdapter
        extends RecyclerView.Adapter<EditPeriodCalendarAdapter.DayVH> {

    public static class EditDay {
        final int dayNumber;
        final boolean isCurrentMonth;
        final Calendar calendar;
        final boolean isInRange;
        final boolean isEndpoint;

        EditDay(int dayNumber, boolean isCurrentMonth, Calendar calendar,
                boolean isInRange, boolean isEndpoint) {
            this.dayNumber      = dayNumber;
            this.isCurrentMonth = isCurrentMonth;
            this.calendar       = calendar;
            this.isInRange      = isInRange;
            this.isEndpoint     = isEndpoint;
        }
    }

    public interface OnDayClickListener {
        void onClick(EditDay day, Calendar calendar);
    }

    private List<EditDay> days;
    private final OnDayClickListener listener;

    public EditPeriodCalendarAdapter(List<EditDay> days, OnDayClickListener listener) {
        this.days     = days;
        this.listener = listener;
    }

    public void setDays(List<EditDay> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayVH holder, int position) {
        EditDay day = days.get(position);
        holder.tvDay.setText(String.valueOf(day.dayNumber));

        // Reset
        holder.dayBg.setVisibility(View.INVISIBLE);
        holder.tvDay.setAlpha(day.isCurrentMonth ? 1f : 0.35f);
        holder.tvDay.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.navy));

        if (day.isCurrentMonth) {
            if (day.isEndpoint) {
                // Titik awal/akhir: lingkaran penuh pink
                holder.dayBg.setVisibility(View.VISIBLE);
                holder.dayBg.setBackgroundResource(R.drawable.bg_calendar_day_today);
                holder.tvDay.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            } else if (day.isInRange) {
                // Dalam range: lingkaran pink muda
                holder.dayBg.setVisibility(View.VISIBLE);
                holder.dayBg.setBackgroundResource(R.drawable.bg_calendar_day_period);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(day, day.calendar);
        });
    }

    @Override
    public int getItemCount() {
        return days != null ? days.size() : 0;
    }

    static class DayVH extends RecyclerView.ViewHolder {
        TextView tvDay;
        View dayBg;
        View dotLogged;

        DayVH(@NonNull View itemView) {
            super(itemView);
            tvDay      = itemView.findViewById(R.id.tvDay);
            dayBg      = itemView.findViewById(R.id.dayBg);
            dotLogged  = itemView.findViewById(R.id.dotLogged);
        }
    }
}
