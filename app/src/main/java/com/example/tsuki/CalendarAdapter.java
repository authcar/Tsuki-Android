package com.example.tsuki;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    private List<CalendarDay> days;

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day, int position);
    }

    private OnDayClickListener listener;

    public CalendarAdapter(List<CalendarDay> days) {
        this.days = days;
    }

    public void setDays(List<CalendarDay> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        holder.tvDay.setText(String.valueOf(day.getDay()));

        // Reset state
        holder.dayBg.setVisibility(View.INVISIBLE);
        holder.dotLogged.setVisibility(View.GONE);

        // Warna teks: abu-abu jika bukan bulan ini
        if (!day.isCurrentMonth()) {
            holder.tvDay.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.tvDay.setAlpha(0.4f);
        } else {
            holder.tvDay.setAlpha(1f);
        }

        // Background lingkaran berdasarkan tipe
        if (day.isCurrentMonth()) {
            switch (day.getType()) {
                case PERIOD:
                    holder.dayBg.setVisibility(View.VISIBLE);
                    holder.dayBg.setBackgroundResource(R.drawable.bg_calendar_day_period);
                    holder.tvDay.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.navy));
                    break;
                case FERTILE:
                    holder.dayBg.setVisibility(View.VISIBLE);
                    holder.dayBg.setBackgroundResource(R.drawable.bg_calendar_day_fertile);
                    holder.tvDay.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.navy));
                    break;
                case OVULATION:
                    holder.dayBg.setVisibility(View.VISIBLE);
                    holder.dayBg.setBackgroundResource(R.drawable.bg_calendar_day_ovulation);
                    holder.tvDay.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.navy));
                    break;
                case TODAY:
                    holder.dayBg.setVisibility(View.VISIBLE);
                    holder.dayBg.setBackgroundResource(R.drawable.bg_calendar_day_today);
                    holder.tvDay.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                    Typeface bold = ResourcesCompat.getFont(
                            holder.itemView.getContext(), R.font.lato_bold);
                    holder.tvDay.setTypeface(bold);
                    break;
                default:
                    holder.tvDay.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.navy));
                    Typeface regular = ResourcesCompat.getFont(
                            holder.itemView.getContext(), R.font.lato);
                    holder.tvDay.setTypeface(regular);
                    break;
            }
        }

        // Dot logged period
        if (day.isLogged()) {
            holder.dotLogged.setVisibility(View.VISIBLE);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && day.isCurrentMonth()) {
                listener.onDayClick(day, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return days != null ? days.size() : 0;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View dayBg;
        View dotLogged;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            dayBg = itemView.findViewById(R.id.dayBg);
            dotLogged = itemView.findViewById(R.id.dotLogged);
        }
    }
}
