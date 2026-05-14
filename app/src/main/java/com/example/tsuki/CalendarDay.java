package com.example.tsuki;

public class CalendarDay {

    public enum DayType {
        NORMAL,
        PERIOD,
        FERTILE,
        OVULATION,
        TODAY
    }

    private final int day;           // angka tanggal (1-31)
    private final boolean isCurrentMonth;
    private DayType type;
    private boolean isLogged;        // dot merah kecil

    public CalendarDay(int day, boolean isCurrentMonth) {
        this.day = day;
        this.isCurrentMonth = isCurrentMonth;
        this.type = DayType.NORMAL;
        this.isLogged = false;
    }

    public int getDay() { return day; }
    public boolean isCurrentMonth() { return isCurrentMonth; }
    public DayType getType() { return type; }
    public void setType(DayType type) { this.type = type; }
    public boolean isLogged() { return isLogged; }
    public void setLogged(boolean logged) { isLogged = logged; }
}
