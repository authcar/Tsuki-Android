package com.example.tsuki;

import java.util.Calendar;

/**
 * Menghitung fase siklus menstruasi berdasarkan tanggal yang dipilih.
 *
 * Fase siklus (default cycle 28 hari, period 5 hari):
 *  - Period       : hari 1 – periodLength
 *  - Follicular   : hari (periodLength+1) – 13
 *  - Ovulation    : hari 14
 *  - Luteal       : hari 15 – cycleLength
 */
public class CycleCalculator {

    public enum Phase {
        PERIOD, FOLLICULAR, OVULATION, LUTEAL
    }

    public static class CycleInfo {
        public final Phase phase;
        public final String phaseName;
        public final int dayInPhase;      // hari ke-N dalam fase ini
        public final int daysLeftInPhase; // sisa hari dalam fase ini
        public final int cycleDay;        // hari ke-N dalam siklus (1-based)

        CycleInfo(Phase phase, String phaseName, int dayInPhase,
                  int daysLeftInPhase, int cycleDay) {
            this.phase           = phase;
            this.phaseName       = phaseName;
            this.dayInPhase      = dayInPhase;
            this.daysLeftInPhase = daysLeftInPhase;
            this.cycleDay        = cycleDay;
        }
    }

    /**
     * Hitung info siklus untuk tanggal yang diberikan.
     *
     * @param selectedDate  Tanggal yang dipilih user
     * @param periodStart   Tanggal mulai period terakhir
     * @param periodLength  Panjang period dalam hari (default 5)
     * @param cycleLength   Panjang siklus dalam hari (default 28)
     */
    public static CycleInfo calculate(Calendar selectedDate,
                                      Calendar periodStart,
                                      int periodLength,
                                      int cycleLength) {

        // Normalisasi ke awal hari (00:00:00)
        Calendar sel   = stripTime(selectedDate);
        Calendar start = stripTime(periodStart);

        // Hitung selisih hari dari period start ke tanggal yang dipilih
        long diffMs   = sel.getTimeInMillis() - start.getTimeInMillis();
        long diffDays = diffMs / (1000L * 60 * 60 * 24);

        // Hitung posisi dalam siklus (0-based, lalu +1 untuk 1-based)
        int cycleDay = (int) (diffDays % cycleLength);
        if (cycleDay < 0) cycleDay += cycleLength; // handle tanggal sebelum period start
        cycleDay += 1; // 1-based

        // Tentukan fase
        int ovulationDay = cycleLength - 14; // biasanya hari ke-14 dari akhir
        if (ovulationDay < periodLength + 1) ovulationDay = periodLength + 1;

        Phase phase;
        String phaseName;
        int phaseStart;
        int phaseEnd;

        if (cycleDay <= periodLength) {
            phase     = Phase.PERIOD;
            phaseName = "Period";
            phaseStart = 1;
            phaseEnd   = periodLength;
        } else if (cycleDay < ovulationDay) {
            phase     = Phase.FOLLICULAR;
            phaseName = "Follicular Phase";
            phaseStart = periodLength + 1;
            phaseEnd   = ovulationDay - 1;
        } else if (cycleDay == ovulationDay) {
            phase     = Phase.OVULATION;
            phaseName = "Ovulation";
            phaseStart = ovulationDay;
            phaseEnd   = ovulationDay;
        } else {
            phase     = Phase.LUTEAL;
            phaseName = "Luteal Phase";
            phaseStart = ovulationDay + 1;
            phaseEnd   = cycleLength;
        }

        int dayInPhase      = cycleDay - phaseStart + 1;
        int daysLeftInPhase = phaseEnd - cycleDay;

        return new CycleInfo(phase, phaseName, dayInPhase, daysLeftInPhase, cycleDay);
    }

    private static Calendar stripTime(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
}
