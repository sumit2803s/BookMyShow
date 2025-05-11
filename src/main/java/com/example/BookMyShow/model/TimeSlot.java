package com.example.BookMyShow.model;

import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {
    private LocalTime start;
    private LocalTime end;

    public TimeSlot(String start, String end) {
        this.start = LocalTime.parse(start);
        this.end = LocalTime.parse(end);
    }

    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }

    public boolean isValid() {
        return start.plusHours(1).equals(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(start, timeSlot.start) &&
                Objects.equals(end, timeSlot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return start + "-" + end;
    }
}
