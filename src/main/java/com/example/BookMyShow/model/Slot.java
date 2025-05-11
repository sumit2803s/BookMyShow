package com.example.BookMyShow.model;

import java.util.*;

public class Slot {
    private TimeSlot timeSlot;

    private  String showName;
    private int capacity;
    private int booked = 0;
    private Queue<BookingRequest> waitlist = new LinkedList<>();
    private Map<String, Booking> bookings = new HashMap<>();

    public Slot(String showName,TimeSlot timeSlot, int capacity) {
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.showName=showName;
    }

    public boolean isAvailable(int persons) {
        return (capacity - booked) >= persons;
    }

    public boolean hasUser(String user) {
        return bookings.containsKey(user);
    }

    public Booking book(String user, int persons) {
        if (!isAvailable(persons)) return null;
        booked += persons;
        Booking booking = new Booking(UUID.randomUUID().toString(), user,showName, timeSlot, persons);
        bookings.put(user, booking);
        return booking;
    }

    public boolean cancel(String user) {
        Booking removed = bookings.remove(user);
        if (removed != null) {
            booked -= removed.getPersons();
            return true;
        }
        return false;
    }

    public void addToWaitlist(String user, int persons) {
        waitlist.offer(new BookingRequest(user, persons));
    }

    public Booking tryFulfillWaitlist() {
        while (!waitlist.isEmpty()) {
            BookingRequest req = waitlist.peek();
            if (isAvailable(req.getPersons())) {
                waitlist.poll();
                return book(req.getUser(), req.getPersons());
            } else break;
        }
        return null;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getAvailableSeats() {
        return capacity - booked;
    }

    public String displayAvailability(String showName) {
        return showName + ": (" + timeSlot + ") " + getAvailableSeats();
    }
}
