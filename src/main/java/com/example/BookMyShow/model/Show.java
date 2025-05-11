package com.example.BookMyShow.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Show {
    private String name;
    private String genre;
    private Map<TimeSlot, Slot> slots = new HashMap<>();

    public Show(String name, String genre) {
        this.name = name;
        this.genre = genre;
    }

    public void addSlot(TimeSlot slot, int capacity) {
        slots.put(slot, new Slot(name,slot, capacity));
    }

    public Collection<Slot> getSlots() {
        return slots.values();
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public Map<TimeSlot, Slot> getSlotMap() {
        return slots;
    }
}
